package checkmate.com.checkmate.event.controller;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.event.service.EventService;
import checkmate.com.checkmate.eventattendanceList.dto.EventAttendanceListResponseDto;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleResponseDto;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1/event")
@Tag(name="이벤트 CRUD", description="이벤트를 등록/조회/수정/삭제 할 수 있습니다.")
public class EventController {

    @Autowired
    private final EventService eventService;

    @ResponseBody
    @PostMapping(value="/register/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "이벤트 등록")
    public BaseResponseDto<EventDetailResponseDto> postEvent(@PathVariable("userId") Long userId,
                                     @RequestPart(value="eventImage") MultipartFile eventImage,
                                     @RequestPart(value="attendanceListFile") MultipartFile attendanceListFile,
                                     @RequestPart(value="event") EventRequestDto eventRequestDto) throws IOException {
        EventDetailResponseDto savedEvent = eventService.postEvent(eventImage, attendanceListFile, eventRequestDto, userId);
        return BaseResponseDto.ofSuccess(POST_EVENT_SUCCESS,savedEvent);
    }

    @ResponseBody
    @GetMapping(value="/list/{userId}")
    @Operation(summary = "이벤트 목록 조회")
    public BaseResponseDto<EventListResponseDto> getEventList(@PathVariable("userId") Long userId){
        List<EventListResponseDto> getEventList = eventService.getEventList(userId);
        return BaseResponseDto.ofSuccess(GET_EVENT_LIST_SUCCESS, getEventList);
    }

    @ResponseBody
    @GetMapping(value="/detail/{userId}/{eventId}")
    @Operation(summary = "이벤트 상세 조회")
    public BaseResponseDto<EventDetailResponseDto> getEventDetail(@PathVariable("userId") Long userId,
                                            @PathVariable("eventId") Long eventId){
        EventDetailResponseDto getEvent = eventService.getEventDetail(userId, eventId);
        return BaseResponseDto.ofSuccess(GET_EVENT_DETAIL_SUCCESS, getEvent);
    }

    @ResponseBody
    @PutMapping(value="modify/{userId}/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이벤트 수정")
    public BaseResponseDto<EventDetailResponseDto> putEvent(@PathVariable("userId") Long userId,
                                      @PathVariable("eventId") Long eventId,
                                      @RequestPart(value="eventImage") MultipartFile eventImage,
                                      @RequestPart(value="attendanceListFile") MultipartFile attendanceListFile,
                                      @RequestPart(value="event") EventRequestDto eventRequestDto){
        EventDetailResponseDto updatedEvent = eventService.updateEvent(eventImage,attendanceListFile, userId, eventId, eventRequestDto);
        return BaseResponseDto.ofSuccess(MODIFY_EVENT_SUCCESS, updatedEvent);
    }

    @DeleteMapping(value="delete/{userId}/{eventId}")
    @Operation(summary = "이벤트 삭제")
    public BaseResponseDto<?> deleteEvent(@PathVariable("userId") Long userId,
                                         @PathVariable("eventId") Long eventId){
        eventService.deleteEvent(userId, eventId);
        return BaseResponseDto.ofSuccess(DELETE_EVENT_SUCCESS);
    }

    @ResponseBody
    @GetMapping(value="/attendancelist/{userId}/{eventId}")
    @Operation(summary="출석명단 확인")
    public BaseResponseDto<EventScheduleResponseDto> getAttendanceList(@PathVariable("userId") Long userId,
                                               @PathVariable("eventId") Long eventId){
        List<EventScheduleResponseDto> eventAttendanceList = eventService.getAttendanceList(userId, eventId);
        return BaseResponseDto.ofSuccess(GET_ATTENDANCE_LIST_SUCCESS, eventAttendanceList);
    }

}

