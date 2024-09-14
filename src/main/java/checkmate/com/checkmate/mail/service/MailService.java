package checkmate.com.checkmate.mail.service;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.mail.domain.Mail;
import checkmate.com.checkmate.mail.domain.MailType;
import checkmate.com.checkmate.mail.domain.repository.MailRepository;
import checkmate.com.checkmate.mail.dto.MailRequestDto;
import checkmate.com.checkmate.mail.dto.MailResponseDto;
import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.mail.component.EmailSender;
import checkmate.com.checkmate.member.domain.Member;
import checkmate.com.checkmate.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final EventAttendanceRepository eventAttendanceRepository;
    private final EmailSender emailSender;
    private final MailRepository mailRepository;
    private final EventScheduleRepository eventScheduleRepository;

    public void sendRemindMail(Accessor accessor, Long eventId, MailType mailType) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        List<String> attendeeEmailList = eventAttendanceRepository.findAllEmailsByEventId(eventId);
        Mail mail = mailRepository.findByEventIdAndMailType(eventId, mailType);
        emailSender.sendEventMail(mail, attendeeEmailList);
    }

    public MailResponseDto getMailContent(Accessor accessor, Long eventId, MailType mailType) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Mail mail = mailRepository.findByEventIdAndMailType(eventId, mailType);
        return MailResponseDto.of(mail);
    }

    public MailResponseDto updateMailContent(Accessor accessor, Long mailId, MailRequestDto mailRequestDto) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Mail mail = mailRepository.findByEventIdAndMailType(mailId, mailRequestDto.getMailType());
        mail.updateMailContent(mailRequestDto);
        mailRepository.save(mail);
        return MailResponseDto.of(mail);
    }

    public void createRemindMail(Event event) {
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(event.getEventId());
        String title = "[체크메이트]" + event.getEventTitle() + "리마인드 메일";
        StringBuilder content = new StringBuilder();
        content.append("안녕하세요.\n");
        content.append("내일").append(event.getEventTitle()).append("이(가) 진행됩니다! 참석을 잊지 마시고, 일정을 꼭 확인해주세요.\n\n");
        content.append("행사명: ").append(event.getEventTitle()).append("\n");
        content.append("내용: ").append(event.getEventDetail()).append("\n");
        content.append("일시: ");
        for (EventSchedule eventSchedule : eventSchedules) {
            content.append(eventSchedule.getEventDate()).append(" - ").append(eventSchedule.getEventStartTime()).append("~").append(eventSchedule.getEventEndTime()).append(", ");
        }
        content.append("\n감사합니다.");
        Mail mail = Mail.builder()
                .mailRequest(MailRequestDto.of(MailType.REMIND,title, String.valueOf(content),null))
                .event(event)
                .build();
    }

    public void createSurveyMail(Event event) {
        String title = "[체크메이트]" + event.getEventTitle() + "설문조사 안내 메일";
        StringBuilder content = new StringBuilder();
        content.append("안녕하세요.\n").append(event.getEventTitle()).append("만족도 조사 안내드립니다.\n감사합니다.");
        Mail mail = Mail.builder()
                .mailRequest(MailRequestDto.of(MailType.SURVEY, title, String.valueOf(content), null))
                .event(event)
                .build();
    }


}