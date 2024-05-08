package checkmate.com.checkmate.event.controller;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/event")
@Tag(name="이벤트 CRUD", description="이벤트를 등록/조회/수정/삭제 할 수 있습니다.")
public class EventController {

    @Autowired
    private final EventService eventService;

    @ResponseBody
    @PostMapping(value="/register/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이벤트 등록")
    public ResponseEntity<?> postEvent(@PathVariable("userId") Long userId,
                                       @RequestPart(value="eventImage") MultipartFile eventImage,
                                       @RequestPart(value="attendanceListFile") MultipartFile attendanceListFile,
                                       @RequestPart(value="event") EventRequestDto eventRequestDto) {
        EventDetailResponseDto savedEvent = eventService.postEvent(eventImage, attendanceListFile, eventRequestDto, userId);
        return ResponseEntity.ok().body(savedEvent);
    }

    @ResponseBody
    @GetMapping(value="/list/{userId}")
    @Operation(summary = "이벤트 목록 조회")
    public ResponseEntity<?> getEventList(@PathVariable("userId") Long userId){
        List<EventListResponseDto> getEvnetList = eventService.getEventList(userId);
        return ResponseEntity.ok().body(getEvnetList);
    }

    @ResponseBody
    @GetMapping(value="/detail/{userId}/{eventId}")
    @Operation(summary = "이벤트 상세 조회")
    public ResponseEntity<?> getEventDetail(@PathVariable("userId") Long userId,
                                            @PathVariable("eventId") Long eventId){
        EventDetailResponseDto getEvent = eventService.getEventDetail(userId, eventId);
        return ResponseEntity.ok().body(getEvent);
    }

    @ResponseBody
    @PutMapping(value="modify/{userId}/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이벤트 수정")
    public ResponseEntity<?> putEvent(@PathVariable("userId") Long userId,
                                      @PathVariable("eventId") Long eventId,
                                      @RequestPart(value="eventImage") MultipartFile eventImage,
                                      @RequestPart(value="event") EventRequestDto eventRequestDto){
        EventDetailResponseDto updatedEvent = eventService.updateEvent(eventImage, userId, eventId, eventRequestDto);
        return ResponseEntity.ok().body(updatedEvent);
    }

    @DeleteMapping(value="delete/{userId}/{eventId}")
    @Operation(summary = "이벤트 삭제")
    public ResponseEntity<?> deleteEvent(@PathVariable("userId") Long userId,
                                         @PathVariable("eventId") Long eventId){
        eventService.deleteEvent(userId, eventId);
        return ResponseEntity.ok().build();
    }

}

