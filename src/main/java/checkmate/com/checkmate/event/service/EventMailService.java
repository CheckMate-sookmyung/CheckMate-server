package checkmate.com.checkmate.event.service;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.event.dto.EventRatioResponseDto;
import checkmate.com.checkmate.member.domain.Member;
import checkmate.com.checkmate.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventMailService {
    private final MemberRepository memberRepository;
    private final EventRepository eventRepository;

    public void sendRemindMail(Accessor accessor, Long eventId) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event getEvent = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        //attendance가져오기
        //모든 attendance에게 메일 보내기
        //메일 값 변수로 다루기

    }

    public void sendServeyMail(Accessor accessor, Long eventId) {
    }
}
