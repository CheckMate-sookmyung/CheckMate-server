package checkmate.com.checkmate.event.service;

import checkmate.com.checkmate.event.dto.EventListResponseDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3Client;

    @Transactional
    public EventDetailResponseDto postEvent(MultipartFile eventImage, EventRequestDto eventRequestDto, Long userId){
        User user = userRepository.findByUserId(userId);

        String imageUrl = null;
        if (!eventImage.isEmpty())
            imageUrl = s3Uploader.saveFile(eventImage, String.valueOf(userId), "event");

        Event savedEvent= eventRequestDto.toEntity(user, imageUrl);

        List<EventSchedule> savedEventSchedules = eventRequestDto.getEventSchedules().stream()
                .map(eventScheduleRequestDto -> EventSchedule.builder()
                            .eventDate(eventScheduleRequestDto.getEventDate())
                            .eventStartTime(eventScheduleRequestDto.getEventStartTime())
                            .eventEndTime(eventScheduleRequestDto.getEventEndTime())
                            .event(savedEvent)
                            .build())
                        .collect(Collectors.toList());

        savedEvent.setEventSchedules(savedEventSchedules); //이게 진짜 마음에 안 든다
        eventRepository.save(savedEvent); //왜 필요할까?

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

        String fileName = extractFileNameFromUrl(updateEvent.getEventImage());
        amazonS3Client.deleteObject(bucketName, fileName);
        String updatedFileName = s3Uploader.saveFile(eventImage, String.valueOf(userId), "event");

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
                updatedFileName,
                updatedEventSchedules,
                eventRequestDto.getAlarmRequest());
        eventRepository.save(updateEvent);

        return EventDetailResponseDto.of(updateEvent);
    }

    @Transactional
    public void deleteEvent(Long userId, Long eventId){
        Event deleteEvent = eventRepository.findByUserIdAndEventId(userId, eventId);
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
