package checkmate.com.checkmate.eventschedule.controller;

import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.eventschedule.service.EventScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/eventSchedules")
public class EventScheduleController {

    @Autowired
    private final EventScheduleService eventScheduleService;

/*    @ResponseBody
    @PostMapping(value="{userId}/{eventId}")
    public ResponseEntity<?> postEventSchedule(@PathVariable("userId") Long userId,
                                               @PathVariable("eventId") Long eventId,
                                               @RequestPart(value="attendanceListFile") MultipartFile attendanceListFile,
                                               @RequestPart(value="eventSchedule") List<EventScheduleRequestDto> eventScheduleRequestDto) {

        EventDetailResponseDto event = eventScheduleService.postEventSchedule(attendanceListFile,eventScheduleRequestDto, userId, eventId);
        return ResponseEntity.ok(event);
    }*/
}

