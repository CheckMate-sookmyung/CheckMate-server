package checkmate.com.checkmate.event.service;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.event.dto.EventRatioResponseDto;
import checkmate.com.checkmate.event.dto.MostAttendeeRatioEventResponseDto;
import checkmate.com.checkmate.event.dto.MostFrequentParticipantsResponseDto;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import checkmate.com.checkmate.eventAttendance.service.EventAttendanceService;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.student.domain.Student;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.eventschedule.dto.StrangerEventScheduleResponseDto;
import checkmate.com.checkmate.eventschedule.dto.StudentEventScheduleResponseDto;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.member.domain.Member;
import checkmate.com.checkmate.member.domain.repository.MemberRepository;
import checkmate.com.checkmate.student.domain.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static checkmate.com.checkmate.global.codes.ErrorCode.EVENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class EventStatisticService {
    private final EventRepository eventRepository;
    private final EventScheduleRepository eventScheduleRepository;
    private final EventAttendanceRepository eventAttendanceRepository;
    private final MemberRepository memberRepository;
    private final StudentRepository studentRepository;

    public EventRatioResponseDto getEventRatioAboutEvent(Accessor accessor, Long eventId) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event event = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
        EventRatioResponseDto eventRatioResponseDto = null;
        List<String> eventDates = new ArrayList<>();
        if (eventSchedules.isEmpty())
            throw new GeneralException(EVENT_NOT_FOUND);
        else {
            if (event.getEventTarget() == EventTarget.INTERNAL) {
                for (EventSchedule eventSchedule : eventSchedules) {
                    eventDates.add(eventSchedule.getEventDate());
                }
                    Long eventScheduleId = eventSchedules.get(0).getEventScheduleId();
                    List<EventAttendance> eventAttendances = eventAttendanceRepository.findEventAttendancesById(eventScheduleId);
                    List<Student> students = eventAttendances.stream()
                        .map(EventAttendance::getStudent)
                        .collect(Collectors.toList());
                    eventRatioResponseDto = EventRatioResponseDto.of(eventDates, students, checkEventCompletion(eventId));
                }
            }
        return eventRatioResponseDto;
    }

    public boolean checkEventCompletion(Long eventId) {
        int attendanceCount = eventRepository.countAttendanceForEvent(eventId);
        int completionTime = eventRepository.findCompletionTimeByEventId(eventId);
        return attendanceCount >= completionTime;
    }

    public List<MostFrequentParticipantsResponseDto> getMostFrequentParticipants(Accessor accessor) {
        List<Student> topStudents = studentRepository.findAllByOrderByAttendanceTimeDesc();
        return topStudents.stream()
                .map(MostFrequentParticipantsResponseDto::of)
                .collect(Collectors.toList());
    }

    public List<MostAttendeeRatioEventResponseDto> getMostAttendeeRatioEvents(Accessor accessor) {
        List<Event> topEvents = eventRepository.findAllByOrderByEventAttendanceRatioDesc();
        return topEvents.stream()
                .map(MostAttendeeRatioEventResponseDto::of)
                .collect(Collectors.toList());
    }
}
