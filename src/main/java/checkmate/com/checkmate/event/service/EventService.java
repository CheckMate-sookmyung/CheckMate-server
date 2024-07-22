package checkmate.com.checkmate.event.service;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.event.dto.EventManagerRequestDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.eventattendanceList.domain.EventAttendanceList;
import checkmate.com.checkmate.eventattendanceList.dto.EventAttendanceListResponseDto;
import checkmate.com.checkmate.eventattendanceList.service.EventAttendanceListService;
import checkmate.com.checkmate.eventattendanceList.domain.repository.EventAttendanceListRepository;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.eventschedule.domain.repository.EventScheduleRepository;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleResponseDto;
import checkmate.com.checkmate.global.config.S3Uploader;
import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.user.domain.User;
import checkmate.com.checkmate.user.domain.repository.UserRepository;
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

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final S3Uploader s3Uploader;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final EventScheduleRepository eventScheduleRepository;
    @Autowired
    private final EventAttendanceListRepository eventAttendanceListRespository;
    @Autowired
    private final EventAttendanceListService eventAttendanceListService;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3Client;

    @Transactional
    public EventDetailResponseDto postEvent(MultipartFile eventImage, MultipartFile attendanceListFile, EventRequestDto eventRequestDto, Long userId) throws IOException {
        User user = userRepository.findByUserId(userId);
        if (user == null)
            throw new GeneralException(USER_NOT_FOUND);

        Event savedEvent = eventRequestDto.toEntity(user);
        eventRepository.save(savedEvent);

        String imageUrl = null;
        String attendanceListUrl = null;
        if(eventImage != null)
            imageUrl = s3Uploader.saveFile(eventImage, String.valueOf(userId), "event/" + String.valueOf(savedEvent.getEventId()));
        if(!attendanceListFile.isEmpty())
            attendanceListUrl = s3Uploader.saveFile(attendanceListFile, String.valueOf(userId), "event/" + String.valueOf(savedEvent.getEventId()));
        else
            throw new GeneralException(FILE_IS_NULL);

        List<EventSchedule> savedEventSchedules = eventRequestDto.getEventSchedules().stream()
                .map(eventScheduleRequestDto -> {
                    EventSchedule eventSchedule = EventSchedule.builder()
                            .eventDate(eventScheduleRequestDto.getEventDate())
                            .eventStartTime(eventScheduleRequestDto.getEventStartTime())
                            .eventEndTime(eventScheduleRequestDto.getEventEndTime())
                            .event(savedEvent)
                            .build();
                    eventScheduleRepository.save(eventSchedule);
                    try {
                        List<EventAttendanceList> savedEventAttendanceLists = eventAttendanceListService.readAndSaveAttendanceList(attendanceListFile, eventSchedule);
                        eventSchedule.setEventAttendanceLists(savedEventAttendanceLists);
                    } catch (IOException e) {
                        throw new GeneralException(IO_EXCEPTION);
                    }
                    return eventSchedule;
                })
                .collect(Collectors.toList());

        eventRepository.save(savedEvent);
        savedEvent.postFileAndAttendanceList(imageUrl, attendanceListUrl, savedEventSchedules); //이거 해줘야 함
        eventRepository.save(savedEvent);

        return EventDetailResponseDto.of(savedEvent);
    }

    @Transactional
    public List<EventListResponseDto> getEventList(Long userId){
        List<Event> events = eventRepository.findByUserId(userId);
        if (!events.isEmpty()) {
            List<EventListResponseDto> eventListResponseDtos = events.stream()
                    .map(EventListResponseDto::of)
                    .collect(Collectors.toList());
            return eventListResponseDtos;
        } else
            throw new GeneralException(EVENT_LIST_NOT_FOUND);
    }

    @Transactional
    public EventDetailResponseDto getEventDetail(Long userId, Long eventId){
        Event getEvent = eventRepository.findByUserIdAndEventId(userId, eventId);
        if (getEvent == null)
            throw new GeneralException(EVENT_NOT_FOUND);
        else
            return EventDetailResponseDto.of(getEvent);
    }

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

            updateEvent.update(
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
    }

    @Transactional
    public void deleteEvent(Long userId, Long eventId){
        Event deleteEvent = eventRepository.findByUserIdAndEventId(userId, eventId);
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
    public List<EventScheduleResponseDto> getAttendanceList(Long userId, Long eventId){
        List<EventSchedule> eventSchedules = eventScheduleRepository.findEventScheduleListByEventId(eventId);
        if (eventSchedules.isEmpty())
            throw new GeneralException(EVENT_NOT_FOUND);
        else {
            List<EventScheduleResponseDto> eventScheduleResponseDtos = new ArrayList<>();
            for (EventSchedule eventSchedule : eventSchedules) {
                Long eventScheduleId = eventSchedule.getEventScheduleId();
                List<EventAttendanceList> eventAttendanceLists = eventAttendanceListRespository.findEventAttendanceListById(eventScheduleId);
                List<EventAttendanceListResponseDto> eventAttendanceListResponseDtos = eventAttendanceLists.stream()
                        .map(EventAttendanceListResponseDto::of)
                        .collect(Collectors.toList());
                eventScheduleResponseDtos.add(EventScheduleResponseDto.of(eventSchedule));
            }
            return eventScheduleResponseDtos;

        }
    }

    @Transactional
    public void registerManager(Long userId, Long eventId, EventManagerRequestDto eventManagerRequestDto) {
        Event event = eventRepository.findByUserIdAndEventId(userId, eventId);
        event.registerEventManager(eventManagerRequestDto.getManagerName(), eventManagerRequestDto.getManagerPhoneNumber(), eventManagerRequestDto.getManagerEmail());
        eventRepository.save(event);
    }
}
