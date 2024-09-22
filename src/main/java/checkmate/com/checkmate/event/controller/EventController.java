package checkmate.com.checkmate.event.controller;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.event.dto.EventManagerRequestDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.event.service.EventService;
import checkmate.com.checkmate.eventschedule.dto.StudentEventScheduleResponseDto;
import checkmate.com.checkmate.global.codes.SuccessCode;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import checkmate.com.checkmate.global.responseDto.ErrorResponseDto;
import com.fasterxml.jackson.databind.ser.Serializers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
@Tag(name="행사 CRUD API", description="행사를 등록/조회/수정/삭제 할 수 있습니다.")
public class EventController {

    @Autowired
    private final EventService eventService;

    @ResponseBody
    @PostMapping(value="", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "행사 등록")
    public BaseResponseDto<?> postEvent(@Parameter(hidden = true) @Auth final Accessor accessor,
                                    @RequestPart(value="eventImage", required = false) MultipartFile eventImage,
                                    @RequestPart(value="attendanceListFile") MultipartFile attendanceListFile,
                                    @RequestPart(value="eventDetail") EventRequestDto eventRequestDto) throws IOException {
        eventService.postEvent(accessor, eventImage, attendanceListFile, eventRequestDto);
        return BaseResponseDto.ofSuccess(POST_EVENT_SUCCESS);
    }

    @ResponseBody
    @GetMapping("")
    @Operation(summary = "행사 목록 조회")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventListResponseDto.class))),
            }
    )
    public ResponseEntity<?> getEventList(@Parameter(hidden = true) @Auth final Accessor accessor){
        List<EventListResponseDto> getEventList = eventService.getEventList(accessor);
        return ResponseEntity.ok(getEventList);
    }

    @ResponseBody
    @GetMapping(value="/{eventId}")
    @Operation(summary = "행사 상세 조회")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventDetailResponseDto.class))),
            }
    )
    public ResponseEntity<?> getEventDetail(@Parameter(hidden = true) @Auth final Accessor accessor,
                                            @PathVariable("eventId") Long eventId){
        EventDetailResponseDto getEvent = eventService.getEventDetail(accessor, eventId);
        return ResponseEntity.ok(getEvent);
    }

    @ResponseBody
    @PutMapping(value="/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "행사 수정")
    public ResponseEntity<?> putEvent(@Parameter(hidden = true) @Auth final Accessor accessor,
                                      @PathVariable("eventId") Long eventId,
                                      @RequestPart(value="eventImage", required = false) MultipartFile eventImage,
                                      @RequestPart(value="eventDetail") EventRequestDto eventRequestDto){
        eventService.updateEvent(accessor, eventId, eventImage, eventRequestDto);
        return ResponseEntity.ok(UPDATE_EVENT_SUCCESS);
    }

    @DeleteMapping(value="/{eventId}")
    @Operation(summary = "행사 삭제")
    public BaseResponseDto<?> deleteEvent(@Parameter(hidden = true) @Auth final Accessor accessor,
                                         @PathVariable("eventId") Long eventId){
        eventService.deleteEvent(accessor, eventId);
        return BaseResponseDto.ofSuccess(DELETE_EVENT_SUCCESS);
    }

}

