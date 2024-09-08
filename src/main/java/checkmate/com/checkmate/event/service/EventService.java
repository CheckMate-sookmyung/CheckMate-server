package checkmate.com.checkmate.event.service;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.event.dto.EventManagerRequestDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.eventAttendance.domain.EventAttendance;
import checkmate.com.checkmate.eventAttendance.domain.repository.EventAttendanceRepository;
import checkmate.com.checkmate.eventAttendance.service.EventAttendanceService;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.eventschedule.dto.StrangerEventScheduleResponseDto;
import checkmate.com.checkmate.eventschedule.dto.StudentEventScheduleResponseDto;
import checkmate.com.checkmate.global.config.S3Uploader;
import checkmate.com.checkmate.global.domain.EventTarget;
import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.member.domain.Member;
import checkmate.com.checkmate.member.domain.repository.MemberRepository;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static checkmate.com.checkmate.global.codes.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class EventService {

    private final S3Uploader s3Uploader;
    private final EventRepository eventRepository;
    private final EventScheduleRepository eventScheduleRepository;
    private final EventAttendanceService eventAttendanceService;
    private final EventAttendanceRepository eventAttendanceRepository;
    private final AmazonS3 amazonS3Client;
    @Autowired
    private final MemberRepository memberRepository;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional
    public void postEvent(Accessor accessor, MultipartFile eventImage, MultipartFile attendanceListFile, EventRequestDto eventRequestDto) throws IOException {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event savedEvent = eventRequestDto.toEntity(loginMember);
        eventRepository.save(savedEvent);

        String imageUrl = null;
        if(eventImage != null)
            imageUrl = s3Uploader.saveFile(eventImage, String.valueOf(loginMember.getMemberId()), "event/" + String.valueOf(savedEvent.getEventId()));
        String attendanceListUrl = s3Uploader.saveFile(attendanceListFile, String.valueOf(loginMember.getMemberId()), "event/" + String.valueOf(savedEvent.getEventId()));

        eventRequestDto.getEventSchedules().stream()
                .map(eventScheduleRequestDto -> {
                    EventSchedule eventSchedule = EventSchedule.builder()
                            .eventDate(eventScheduleRequestDto.getEventDate())
                            .eventStartTime(eventScheduleRequestDto.getEventStartTime())
                            .eventEndTime(eventScheduleRequestDto.getEventEndTime())
                            .event(savedEvent)
                            .build();
                    eventScheduleRepository.save(eventSchedule);
                    try {
                        eventAttendanceService.readAndSaveAttendanceList(attendanceListFile, eventSchedule, eventRequestDto.getEventTarget());

                    } catch (IOException e) {
                        throw new GeneralException(IO_EXCEPTION);
                    }
                    return eventSchedule;
                })
                .collect(Collectors.toList());

        savedEvent.registerFileAndAttendanceList(imageUrl, attendanceListUrl);
        eventRepository.save(savedEvent);
    }

    @Transactional
    public List<EventListResponseDto> getEventList(Accessor accessor) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        List<Event> events = eventRepository.findByMemberId(loginMember.getMemberId());
        if (!events.isEmpty()) {
            List<EventListResponseDto> eventListResponseDtos = events.stream()
                    .map(event -> {
                        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(event.getEventId());
                        return EventListResponseDto.of(event, eventSchedules);
                    })
                    .collect(Collectors.toList());
            return eventListResponseDtos;
        } else {
            throw new GeneralException(EVENT_LIST_NOT_FOUND);
        }
    }


    @Transactional
    public EventDetailResponseDto getEventDetail(Accessor accessor, Long eventId){
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event getEvent = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        if (getEvent == null)
            throw new GeneralException(EVENT_NOT_FOUND);
        else {
            List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
            return EventDetailResponseDto.of(getEvent, eventSchedules);
        }
    }
/*
    @Transactional
    public EventDetailResponseDto updateEvent(MultipartFile eventImage, MultipartFile attendanceListFile, Long userId, Long eventId, EventRequestDto eventRequestDto){
        Event updateEvent = eventRepository.findByUserIdAndEventId(userId, eventId);

        if (updateEvent != null) {
            String updatedImageFileName = null;
            String updatedAttendacneListFileName = null;
            if (!eventImage.isEmpty()) {
                String ImagefileName = extractFileNameFromUrl(updateEvent.getEventImage());
                amazonS3Client.deleteObject(bucketName, ImagefileName);
                updatedImageFileName = s3Uploader.saveFile(eventImage, String.valueOf(userId), "event/" + String.valueOf(updateEvent.getEventId()));
            } else
                throw new GeneralException(IMAGE_IS_NULL);
            if (!attendanceListFile.isEmpty()) {
                String AttendanceListfileName = extractFileNameFromUrl(updateEvent.getBeforeAttendanceListFile());
                amazonS3Client.deleteObject(bucketName, AttendanceListfileName);
                updatedAttendacneListFileName = s3Uploader.saveFile(attendanceListFile, String.valueOf(userId), "event/" + String.valueOf(updateEvent.getEventId()));
            } else
                throw new GeneralException(FILE_IS_NULL);

            eventScheduleRepository.deleteByEventEventId(eventId);
            List<EventSchedule> updatedEventSchedules = eventRequestDto.getEventSchedules().stream()
                    .map(eventScheduleRequestDto -> EventSchedule.builder()
                            .eventDate(eventScheduleRequestDto.getEventDate())
                            .eventStartTime(eventScheduleRequestDto.getEventStartTime())
                            .eventEndTime(eventScheduleRequestDto.getEventEndTime())
                            .event(updateEvent)
                            .build())
                    .collect(Collectors.toList());

            updateEvent.updateEvent(
                    eventRequestDto.getEventTitle(),
                    eventRequestDto.getEventDetail(),
                    updatedImageFileName,
                    updatedAttendacneListFileName,
                    updatedEventSchedules,
                    eventRequestDto.getAlarmRequest());
            eventRepository.save(updateEvent);

            return EventDetailResponseDto.of(updateEvent);
        } else
            throw new GeneralException(EVENT_NOT_FOUND);
    }*/

    @Transactional
    public void deleteEvent(Accessor accessor, Long eventId){
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event deleteEvent = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        if (deleteEvent == null)
            throw new GeneralException(EVENT_NOT_FOUND);
        if (deleteEvent.getEventImage() != null) {
            String ImagefileName = extractFileNameFromUrl(deleteEvent.getEventImage());
            amazonS3Client.deleteObject(bucketName, ImagefileName);
        }
        String AttendanceListfileName = extractFileNameFromUrl(deleteEvent.getBeforeAttendanceListFile());
        amazonS3Client.deleteObject(bucketName, AttendanceListfileName);
        eventRepository.delete(deleteEvent);
    }

    private static String extractFileNameFromUrl(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String path = uri.getPath();
            return path.substring(path.lastIndexOf('/') + 1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public List<Object> getAttendanceList(Accessor accessor, Long eventId){
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event event = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
        List<Object> eventScheduleResponseDtos = new ArrayList<>();
        if (eventSchedules.isEmpty())
            throw new GeneralException(EVENT_NOT_FOUND);
        else {
            if(event.getEventTarget() == EventTarget.INTERNAL) {
                for (EventSchedule eventSchedule : eventSchedules) {
                    Long eventScheduleId = eventSchedule.getEventScheduleId();
                    List<EventAttendance> eventAttendances = eventAttendanceRepository.findEventAttendancesById(eventScheduleId);
/*                List<EventAttendanceResponseDto> eventAttendanceResponseDtos = eventAttendances.stream()
                        .map(EventAttendanceResponseDto::of)
                        .collect(Collectors.toList());*/
                    eventScheduleResponseDtos.add(StudentEventScheduleResponseDto.of(eventSchedule, eventAttendances));
                }
            }
            else{
                for (EventSchedule eventSchedule : eventSchedules) {
                    Long eventScheduleId = eventSchedule.getEventScheduleId();
                    List<EventAttendance> eventAttendances = eventAttendanceRepository.findEventAttendancesById(eventScheduleId);
/*                List<EventAttendanceResponseDto> eventAttendanceResponseDtos = eventAttendances.stream()
                        .map(EventAttendanceResponseDto::of)
                        .collect(Collectors.toList());*/
                    eventScheduleResponseDtos.add(StrangerEventScheduleResponseDto.of(eventSchedule, eventAttendances));
                }
            }
            return eventScheduleResponseDtos;

        }
    }

    @Transactional
    public void registerManager(Accessor accessor, Long eventId, EventManagerRequestDto eventManagerRequestDto) {
        final Member loginMember = memberRepository.findMemberByMemberId(accessor.getMemberId());
        Event event = eventRepository.findByMemberIdAndEventId(loginMember.getMemberId(), eventId);
        event.registerEventManager(eventManagerRequestDto.getManagerName(), eventManagerRequestDto.getManagerPhoneNumber(), eventManagerRequestDto.getManagerEmail());
        eventRepository.save(event);
    }
}
