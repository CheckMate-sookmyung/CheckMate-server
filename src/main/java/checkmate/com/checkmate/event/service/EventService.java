package checkmate.com.checkmate.event.service;

import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.eventattendanceList.service.EventAttendanceListService;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.global.config.S3Uploader;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.user.domain.User;
import checkmate.com.checkmate.user.domain.repository.UserRepository;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final S3Uploader s3Uploader;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final EventScheduleRepository eventScheduleRepository;
    @Autowired
    private final EventAttendanceListService eventAttendanceListService;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3Client;

    @Transactional
    public EventDetailResponseDto postEvent(MultipartFile eventImage, MultipartFile attendanceListFile, EventRequestDto eventRequestDto, Long userId) throws IOException {
        User user = userRepository.findByUserId(userId);

        Event savedEvent = eventRequestDto.toEntity(user);
        eventRepository.save(savedEvent);

        String imageUrl = s3Uploader.saveFile(eventImage, String.valueOf(userId), "event/" + String.valueOf(savedEvent.getEventId()));
        String attendanceListUrl = s3Uploader.saveFile(attendanceListFile, String.valueOf(userId), "event/" + String.valueOf(savedEvent.getEventId()));
        List<EventSchedule> savedEventSchedules = eventRequestDto.getEventSchedules().stream()
                .map(eventScheduleRequestDto -> {
                    EventSchedule eventSchedule = EventSchedule.builder()
                            .eventDate(eventScheduleRequestDto.getEventDate())
                            .eventStartTime(eventScheduleRequestDto.getEventStartTime())
                            .eventEndTime(eventScheduleRequestDto.getEventEndTime())
                            .event(savedEvent)
                            .build();
                    try {
                        eventAttendanceListService.readAndSaveAttendanceList(attendanceListFile, eventSchedule);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return eventSchedule;
                })
                .collect(Collectors.toList());

        savedEvent.postFileAndAttendanceList(imageUrl, attendanceListUrl, savedEventSchedules);
        eventRepository.save(savedEvent);

        return EventDetailResponseDto.of(savedEvent);
    }

    @Transactional
    public List<EventListResponseDto> getEventList(Long userId){
        List<Event> events = eventRepository.findByUserId(userId);
        List<EventListResponseDto> eventListResponseDtos = events.stream()
                .map(EventListResponseDto::of)
                .collect(Collectors.toList());
        return eventListResponseDtos;
    }

    @Transactional
    public EventDetailResponseDto getEventDetail(Long userId, Long eventId){
        Event getEvent = eventRepository.findByUserIdAndEventId(userId, eventId);
        return EventDetailResponseDto.of(getEvent);
    }

    @Transactional
    public EventDetailResponseDto updateEvent(MultipartFile eventImage, Long userId, Long eventId, EventRequestDto eventRequestDto){
        Event updateEvent = eventRepository.findByUserIdAndEventId(userId, eventId);

        String ImagefileName = extractFileNameFromUrl(updateEvent.getEventImage());
        amazonS3Client.deleteObject(bucketName, ImagefileName);
        String updatedImageFileName = s3Uploader.saveFile(eventImage, String.valueOf(userId), "event/"+String.valueOf(updateEvent.getEventId()));

        String AttendanceListfileName = extractFileNameFromUrl(updateEvent.getEventAttendanceListFile());
        amazonS3Client.deleteObject(bucketName, AttendanceListfileName);
        String updatedAttendacneListFileName = s3Uploader.saveFile(eventImage, String.valueOf(userId), "event/"+"event/"+String.valueOf(updateEvent.getEventId()));


        eventScheduleRepository.deleteByEventEventId(eventId);
        List<EventSchedule> updatedEventSchedules = eventRequestDto.getEventSchedules().stream()
                .map(eventScheduleRequestDto -> EventSchedule.builder()
                        .eventDate(eventScheduleRequestDto.getEventDate())
                        .eventStartTime(eventScheduleRequestDto.getEventStartTime())
                        .eventEndTime(eventScheduleRequestDto.getEventEndTime())
                        .event(updateEvent)
                        .build())
                .collect(Collectors.toList());

        updateEvent.update(
                eventRequestDto.getEventTitle(),
                eventRequestDto.getEventDetail(),
                updatedImageFileName,
                updatedAttendacneListFileName,
                updatedEventSchedules,
                eventRequestDto.getAlarmRequest());
        eventRepository.save(updateEvent);

        return EventDetailResponseDto.of(updateEvent);
    }

    @Transactional
    public void deleteEvent(Long userId, Long eventId){
        Event deleteEvent = eventRepository.findByUserIdAndEventId(userId, eventId);
        String ImagefileName = extractFileNameFromUrl(deleteEvent.getEventImage());
        amazonS3Client.deleteObject(bucketName, ImagefileName);
        String AttendanceListfileName = extractFileNameFromUrl(deleteEvent.getEventAttendanceListFile());
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
}
