package checkmate.com.checkmate.event.controller;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.event.dto.EventManagerRequestDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.event.service.EventService;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleResponseDto;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import checkmate.com.checkmate.global.responseDto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import checkmate.com.checkmate.auth.Auth;

import java.io.IOException;
import java.util.List;

import static checkmate.com.checkmate.global.codes.SuccessCode.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
@Tag(name="행사 CRUD", description="행사를 등록/조회/수정/삭제 할 수 있습니다.")
@ApiResponses(
        value = {
                @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        }
)
public class EventController {

    @Autowired
    private final EventService eventService;

    @ResponseBody
    @PostMapping(value="", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "행사 등록")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventDetailResponseDto.class))),
            }
    )

    public BaseResponseDto<?> postEvent(@Auth final Accessor accessor,
                                    @RequestPart(value="eventImage", required = false) MultipartFile eventImage,
                                    @RequestPart(value="attendanceListFile") MultipartFile attendanceListFile,
                                    @RequestPart(value="eventDetail") EventRequestDto eventRequestDto) throws IOException {
        eventService.postEvent(accessor, eventImage, attendanceListFile, eventRequestDto);
        return BaseResponseDto.ofSuccess(POST_EVENT_SUCCESS);
    }

    @ResponseBody
    @GetMapping("")
    @Operation(summary = "이벤트 목록 조회")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventListResponseDto.class))),
            }
    )
    public ResponseEntity<?> getEventList(@Auth final Accessor accessor){
        List<EventListResponseDto> getEventList = eventService.getEventList(accessor);
        return ResponseEntity.ok(getEventList);
    }

    @ResponseBody
    @GetMapping(value="/{eventId}")
    @Operation(summary = "이벤트 상세 조회")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventDetailResponseDto.class))),
            }
    )
    public ResponseEntity<?> getEventDetail(@Auth final Accessor accessor,
                                            @PathVariable("eventId") Long eventId){
        EventDetailResponseDto getEvent = eventService.getEventDetail(accessor, eventId);
        return ResponseEntity.ok(getEvent);
    }

/*    @ResponseBody
    @PutMapping(value="/{userId}/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이벤트 수정")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventDetailResponseDto.class))),
            }
    )
    public ResponseEntity<?> putEvent(@PathVariable("userId") Long userId,
                                      @PathVariable("eventId") Long eventId,
                                      @RequestPart(value="eventImage", required = false) MultipartFile eventImage,
                                      @RequestPart(value="attendanceListFile", required = false) MultipartFile attendanceListFile,
                                      @RequestPart(value="event") EventRequestDto eventRequestDto){
        EventDetailResponseDto updatedEvent = eventService.updateEvent(eventImage,attendanceListFile, userId, eventId, eventRequestDto);
        return ResponseEntity.ok(updatedEvent);
    }*/

    @DeleteMapping(value="/{eventId}")
    @Operation(summary = "이벤트 삭제")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content),
            }
    )
    public BaseResponseDto<?> deleteEvent(@Auth final Accessor accessor,
                                         @PathVariable("eventId") Long eventId){
        eventService.deleteEvent(accessor, eventId);
        return BaseResponseDto.ofSuccess(DELETE_EVENT_SUCCESS);
    }

    @ResponseBody
    @GetMapping(value="/attendanceList/{eventId}")
    @Operation(summary="행사 출석 명단 확인")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventScheduleResponseDto.class))),
            }
    )
    public ResponseEntity<?> getAttendanceList(@Auth final Accessor accessor,
                                               @PathVariable("eventId") Long eventId){
        List<EventScheduleResponseDto> eventAttendanceList = eventService.getAttendanceList(accessor, eventId);
        return ResponseEntity.ok(eventAttendanceList);
    }

    @ResponseBody
    @PostMapping(value="/manager/{eventId}")
    @Operation(summary = "행사 담당자 등록")
    public BaseResponseDto<?> registerManager(@Auth final Accessor accessor,
                                             @PathVariable("eventId") Long eventId,
                                             @RequestPart(value="manager") EventManagerRequestDto eventManagerRequestDto){
        eventService.registerManager(accessor, eventId, eventManagerRequestDto);
        return BaseResponseDto.ofSuccess(REGISTER_EVENT_MANAGER_SUCCESS);


    }

}

