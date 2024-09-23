package checkmate.com.checkmate.event.service;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.event.dto.EventStatisticDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventStatisticResponseDto;
import checkmate.com.checkmate.event.dto.MostAttendeeRatioEventResponseDto;
import checkmate.com.checkmate.event.dto.BestAttendeeResponseDto;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.student.domain.Student;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
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

    public EventStatisticResponseDto getEventRatioAboutEvent(Accessor accessor, Long eventId) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event event = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
        EventStatisticResponseDto eventStatisticResponseDto = null;
        List<EventStatisticDetailResponseDto> eventStatisticDetailResponseDtos = new ArrayList<>();
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

                    students.forEach(student -> {
                        eventStatisticDetailResponseDtos.add(new EventStatisticDetailResponseDto(student.getStudentNumber(), student.getStudentMajor(), checkEventCompletion(student, event)));
                });
                    eventStatisticResponseDto = EventStatisticResponseDto.of(eventDates, eventStatisticDetailResponseDtos);
                }
            }
        return eventStatisticResponseDto;
    }

    public boolean checkEventCompletion(Student student, Event event) {
        List<EventSchedule> eventSchedule = eventScheduleRepository.findEventScheduleListByEventId(event.getEventId());
        List<Long> eventScheduleIds = eventSchedule.stream()
                .map(EventSchedule::getEventScheduleId)
                .collect(Collectors.toList());
        int attendanceCount = eventAttendanceRepository.countAttendanceByEventScheduleIdsAndStudent(eventScheduleIds, student);
        int completionTime = event.getCompletionTime();
        return attendanceCount >= completionTime;
    }

    public List<BestAttendeeResponseDto> getMostFrequentParticipants(Accessor accessor) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        List<Student> topStudents = studentRepository.findAllStudentsByMemberIdOrderByAttendanceTimeDesc(loginMember.getMemberId());
        return topStudents.stream()
                .map(BestAttendeeResponseDto::of)
                .collect(Collectors.toList());
    }

    public List<MostAttendeeRatioEventResponseDto> getMostAttendeeRatioEvents(Accessor accessor) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        List<Event> topEvents = eventRepository.findAllByMemberIdOrderByEventAttendanceRatioDesc(loginMember.getMemberId());
        return topEvents.stream()
                .map(MostAttendeeRatioEventResponseDto::of)
                .collect(Collectors.toList());
    }
}
