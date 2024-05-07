package checkmate.com.checkmate.event.service;

import checkmate.com.checkmate.global.config.S3Uploader;
import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.domain.repository.EventRepository;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.eventschedule.domain.EventSchedule;
import checkmate.com.checkmate.user.domain.User;
import checkmate.com.checkmate.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Transactional
    public EventDetailResponseDto postEvent(MultipartFile eventImage, EventRequestDto eventRequestDto, Long userId){
        User user = userRepository.findByUserId(userId);
        String imageUrl = null;
        if (!eventImage.isEmpty())
            imageUrl = s3Uploader.saveFile(eventImage, String.valueOf(userId), "event");
        System.out.println(eventRequestDto.getEventSchedules());
        List<EventSchedule> savedEventSchedules = eventRequestDto.getEventSchedules().stream()
                .map(eventScheduleRequestDto -> EventSchedule.builder()
                        .eventDate(eventScheduleRequestDto.getEventDate())
                        .eventStartTime(eventScheduleRequestDto.getEventStartTime())
                        .eventEndTime(eventScheduleRequestDto.getEventEndTime())
                        .build())
                        .collect(Collectors.toList());
        Event savedEvent= eventRepository.save(eventRequestDto.toEntity(user, imageUrl, savedEventSchedules));
        return EventDetailResponseDto.of(savedEvent);
    }

}