package checkmate.com.checkmate.eventAttendance.controller;

import checkmate.com.checkmate.auth.Auth;
import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.eventAttendance.dto.AttendeePlustRequestDto;
import checkmate.com.checkmate.eventAttendance.dto.AttendanceUpdateRequestDto;
import checkmate.com.checkmate.eventAttendance.dto.StudentEventAttendanceResponseDto;
import checkmate.com.checkmate.eventAttendance.service.EventAttendanceService;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static checkmate.com.checkmate.global.codes.SuccessCode.REMOVE_ATTENDANCE_SUCCESS;
import static checkmate.com.checkmate.global.codes.SuccessCode.SEND_ATTENDACE_LIST_SUCCESS;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance/manage")
@Tag(name="출석 관리 API", description="출석을 관리할 수 있습니다.")
public class EventAttendanceManageController {

    @Autowired
    private final EventAttendanceService eventAttendanceService;

    @ResponseBody
    @PutMapping(value="/{eventId}")
    @Operation(summary = "출석명단의 출석 수정")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StudentEventAttendanceResponseDto.class))),
            }
    )
    public ResponseEntity<?> updateAttendanceList(@Parameter(hidden = true) @Auth final Accessor accessor,
                                                  @PathVariable("eventId") Long eventId,
                                                  @RequestPart("attendanceList") List<AttendanceUpdateRequestDto> attendanceUpdateRequestDto){
        List<?> studentEventAttendanceResponseDtos = eventAttendanceService.updateAttendanceList(accessor, eventId, attendanceUpdateRequestDto);
        return ResponseEntity.ok(studentEventAttendanceResponseDtos);
    }

    @ResponseBody
    @DeleteMapping(value="/{eventId}/{eventScheduleId}/{attendeeId}")
    @Operation(summary = "출석명단의 출석자 삭제")
    public BaseResponseDto<?> deleteAttendanceList(@Parameter(hidden = true) @Auth final Accessor accessor,
                                                   @PathVariable("eventId") Long eventId,
                                                   @PathVariable("eventScheduleId") Long eventScheduleId,
                                                   @PathVariable("attendeeId") Long attendeeId){
        eventAttendanceService.deleteAttendanceList(accessor, eventId, eventScheduleId, attendeeId);
        return BaseResponseDto.ofSuccess(REMOVE_ATTENDANCE_SUCCESS);
    }

    @ResponseBody
    @PostMapping(value="/{eventId}/{eventScheduleId}")
    @Operation(summary = "출석명단의 출석자 추가")

    public BaseResponseDto<?> registerAttendee(@Parameter(hidden = true) @Auth final Accessor accessor,
                                               @PathVariable("eventId") Long eventId,
                                               @PathVariable("eventScheduleId") Long eventScheduleId,
                                               @RequestPart("attendeeInfo") List<AttendeePlustRequestDto> attendeePlustRequestDtos){
        eventAttendanceService.addAttendee(accessor, eventId, eventScheduleId, attendeePlustRequestDtos);
        return BaseResponseDto.ofSuccess(SEND_ATTENDACE_LIST_SUCCESS);
    }
}
