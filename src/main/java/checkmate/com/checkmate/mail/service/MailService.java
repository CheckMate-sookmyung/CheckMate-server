package checkmate.com.checkmate.mail.service;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.global.config.SchedulingConfig;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.global.util.DateTimeUtil;
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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static checkmate.com.checkmate.mail.domain.MailType.REMIND;
import static checkmate.com.checkmate.mail.domain.MailType.SURVEY;

@Service
@RequiredArgsConstructor
public class MailService {
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;
    private final EventAttendanceRepository eventAttendanceRepository;
    private final EmailSender emailSender;
    private final MailRepository mailRepository;
    private final EventScheduleRepository eventScheduleRepository;
    private final TaskScheduler taskScheduler;

    public void sendEventMail(Accessor accessor, Long eventId, MailType mailType) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event event = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        List<String> attendeeEmailList = new ArrayList<>();
        if(event.getEventTarget()== EventTarget.INTERNAL) {
            attendeeEmailList = eventRepository.findDistinctStudentEmailsByEventId(eventId);
        } else{
            attendeeEmailList = eventRepository.findDistinctStrangerEmailsByEventId(eventId);
        }
        Mail mail = mailRepository.findByEventIdAndMailType(eventId, mailType);
        attendeeEmailList.add(event.getManagerEmail());
        if(mail.getMailType()== REMIND)
            emailSender.sendEventMail(mail, attendeeEmailList, event.getEventImage(),event.getEventUrl());
        else
            emailSender.sendEventMail(mail, attendeeEmailList, event.getEventImage(),event.getSurveyUrl());
    }

    public MailResponseDto getMailContent(Accessor accessor, Long eventId, MailType mailType) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Mail mail = mailRepository.findByEventIdAndMailType(eventId, mailType);
        return MailResponseDto.of(mail);
    }

    public MailResponseDto updateMailContent(Accessor accessor, Long mailId, MailRequestDto mailRequestDto) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Mail mail = mailRepository.findByMailId(mailId);
        mail.updateMailContent(mailRequestDto);
        mailRepository.save(mail);
        return MailResponseDto.of(mail);
    }

    public void createRemindMail(Event event) {
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(event.getEventId());
        String title = "[체크메이트]" + event.getEventTitle() + "리마인드 메일";
        StringBuilder content = new StringBuilder();
        content.append("안녕하세요.<br>");
        content.append("내일").append(event.getEventTitle()).append("이(가) 진행됩니다! 참석을 잊지 마시고, 일정을 꼭 확인해주세요.<br><br>");
        content.append("행사명: ").append(event.getEventTitle()).append("<br>");
        content.append("내용: ").append(event.getEventDetail()).append("<br>");
        content.append("일시: ");
        for (EventSchedule eventSchedule : eventSchedules) {
            content.append(eventSchedule.getEventDate()).append(" - ").append(eventSchedule.getEventStartTime()).append("~").append(eventSchedule.getEventEndTime()).append(", ");
        }
        content.append("<br>감사합니다.");
        Mail mail = Mail.builder()
                .mailType(REMIND)
                .mailTitle(title)
                .mailContent(content.toString())
                .attachUrl(null)
                .imageUrl(event.getEventImage())
                .event(event)
                .build();
        mailRepository.save(mail);
    }

    public void createSurveyMail(Event event) {
        String title = "[체크메이트]" + event.getEventTitle() + "설문조사 안내 메일";
        StringBuilder content = new StringBuilder();
        content.append("안녕하세요.<br>").append(event.getEventTitle()).append("만족도 조사 안내드립니다.<br>감사합니다.");
        Mail mail = Mail.builder()
                .mailType(MailType.SURVEY)
                .mailTitle(title)
                .mailContent(content.toString())
                .attachUrl(null)
                .imageUrl(event.getEventImage())
                .event(event)
                .build();
        mailRepository.save(mail);
    }

    public void scheduleEventMails(Accessor accessor, List<EventSchedule> eventSchedules, Long eventId) {
        EventSchedule startSchedule = eventSchedules.get(0);
        EventSchedule endSchedule = eventSchedules.get(eventSchedules.size() - 1);

        // 행사 시작 24시간 전 예약
        LocalDateTime startDateTime = DateTimeUtil.parseEventDateTime(startSchedule.getEventDate(), startSchedule.getEventStartTime());
        LocalDateTime reminderDateTime = startDateTime.minusHours(24);

        Date reminderTime = Date.from(reminderDateTime.atZone(ZoneId.systemDefault()).toInstant());
        taskScheduler.schedule(() -> sendEventMail(accessor, eventId, REMIND), reminderTime);

        // 행사 종료 1시간 후 예약
        LocalDateTime endDateTime = DateTimeUtil.parseEventDateTime(endSchedule.getEventDate(), endSchedule.getEventEndTime());
        LocalDateTime followUpDateTime = endDateTime.plusHours(1);

        Date followUpTime = Date.from(followUpDateTime.atZone(ZoneId.systemDefault()).toInstant());
        taskScheduler.schedule(() -> sendEventMail(accessor, eventId, SURVEY), followUpTime);
    }
}
