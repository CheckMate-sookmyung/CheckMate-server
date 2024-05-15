package checkmate.com.checkmate.event.controller;

import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.event.service.EventService;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleResponseDto;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseWrapCipher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static checkmate.com.checkmate.global.codes.SuccessCode.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@Tag(name="행사 CRUD", description="행사를 등록/조회/수정/삭제 할 수 있습니다.")
@ApiResponses(
        value = {
                @ApiResponse(responseCode = "200"),
                @ApiResponse(responseCode = "404", content = @Content)
        }
)
public class EventController {

    @Autowired
    private final EventService eventService;

    @ResponseBody
    @PostMapping(value="/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "행사 등록")
    public ResponseEntity<?> postEvent(@PathVariable("userId") Long userId,
                                    @RequestPart(value="eventImage", required = false) MultipartFile eventImage,
                                    @RequestPart(value="attendanceListFile") MultipartFile attendanceListFile,
                                    @RequestPart(value="event") EventRequestDto eventRequestDto) throws IOException {
        EventDetailResponseDto savedEvent = eventService.postEvent(eventImage, attendanceListFile, eventRequestDto, userId);
        return ResponseEntity.ok(savedEvent);
    }

    @ResponseBody
    @GetMapping(value="/{userId}")
    @Operation(summary = "이벤트 목록 조회")
    public ResponseEntity<?> getEventList(@PathVariable("userId") Long userId){
        List<EventListResponseDto> getEventList = eventService.getEventList(userId);
        return ResponseEntity.ok(getEventList);
    }

    @ResponseBody
    @GetMapping(value="/{userId}/{eventId}")
    @Operation(summary = "이벤트 상세 조회")
    public ResponseEntity<?> getEventDetail(@PathVariable("userId") Long userId,
                                            @PathVariable("eventId") Long eventId){
        EventDetailResponseDto getEvent = eventService.getEventDetail(userId, eventId);
        return ResponseEntity.ok(getEvent);
    }

    @ResponseBody
    @PutMapping(value="/{userId}/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이벤트 수정")
    public ResponseEntity<?> putEvent(@PathVariable("userId") Long userId,
                                      @PathVariable("eventId") Long eventId,
                                      @RequestPart(value="eventImage", required = false) MultipartFile eventImage,
                                      @RequestPart(value="attendanceListFile", required = false) MultipartFile attendanceListFile,
                                      @RequestPart(value="event") EventRequestDto eventRequestDto){
        EventDetailResponseDto updatedEvent = eventService.updateEvent(eventImage,attendanceListFile, userId, eventId, eventRequestDto);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping(value="/{userId}/{eventId}")
    @Operation(summary = "이벤트 삭제")
    public BaseResponseDto<?> deleteEvent(@PathVariable("userId") Long userId,
                                         @PathVariable("eventId") Long eventId){
        eventService.deleteEvent(userId, eventId);
        return BaseResponseDto.ofSuccess(DELETE_EVENT_SUCCESS);
    }

    @ResponseBody
    @GetMapping(value="/attendanceList/{userId}/{eventId}")
    @Operation(summary="행사 출석명단 확인")
    public ResponseEntity<?> getAttendanceList(@PathVariable("userId") Long userId,
                                               @PathVariable("eventId") Long eventId){
        List<EventScheduleResponseDto> eventAttendanceList = eventService.getAttendanceList(userId, eventId);
        return ResponseEntity.ok(eventAttendanceList);
    }

}

