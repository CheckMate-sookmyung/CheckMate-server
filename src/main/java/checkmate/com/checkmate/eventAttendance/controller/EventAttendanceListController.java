package checkmate.com.checkmate.eventAttendance.controller;

import checkmate.com.checkmate.auth.Auth;
import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.eventAttendance.dto.AttendanceListFileUrlResponseDto;
import checkmate.com.checkmate.eventAttendance.service.EventAttendanceService;
import checkmate.com.checkmate.eventschedule.dto.StudentEventScheduleResponseDto;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static checkmate.com.checkmate.global.codes.SuccessCode.SEND_ATTENDACE_LIST_SUCCESS;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendancelist")
@Tag(name="출석명단 관련 API", description="출석명단을 다운로드, 업로드, 전송할 수 있습니다.")
public class EventAttendanceListController {

    @Autowired
    private final EventAttendanceService eventAttendanceService;

    @ResponseBody
    @GetMapping(value="/{eventId}")
    @Operation(summary="출석명단 조회")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StudentEventScheduleResponseDto.class))),
            }
    )
    public ResponseEntity<?> getAttendanceList(@Parameter(hidden = true) @Auth final Accessor accessor,
                                               @PathVariable("eventId") Long eventId){
        List<Object> eventAttendanceList = eventAttendanceService.getAttendanceList(accessor, eventId);
        return ResponseEntity.ok(eventAttendanceList);
    }

    @ResponseBody
    @GetMapping(value="/download/{eventId}")
    @Operation(summary="(사후) 출석명단 다운")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AttendanceListFileUrlResponseDto.class))),
            }
    )
    public ResponseEntity<?> sendAttendanceListManually(@Parameter(hidden = true) @Auth final Accessor accessor,
                                                        @PathVariable("eventId") Long eventId) throws IOException {
        AttendanceListFileUrlResponseDto attendanceListFileUrlResponseDto = eventAttendanceService.downloadAttendanceList(accessor, eventId);
        return ResponseEntity.ok(attendanceListFileUrlResponseDto);
    }

    @ResponseBody
    @PutMapping(value="/{eventScheduleId}")
    @Operation(summary="온라인 (사후) 출석명단 업로드")
    public ResponseEntity<?> uploadAttendanceListAboutOnline(@Parameter(hidden = true) @Auth final Accessor accessor,
                                                             @PathVariable("eventScheduleId") Long eventScheduleId,
                                                             @RequestPart(value="attendanceListFile") MultipartFile attendanceFile) throws IOException {
        String fileUrl = eventAttendanceService.uploadAttendanceListAboutOnline(accessor, eventScheduleId, attendanceFile);
        return ResponseEntity.ok(fileUrl);
    }

    @ResponseBody
    @PostMapping(value="/sending/{eventId}")
    @Operation(summary="(사후) 출석명단 자동 전송")
    public BaseResponseDto<?> sendAttendanceListAutomatically(@Parameter(hidden = true) @Auth final Accessor accessor,
                                                              @PathVariable("eventId") Long eventId) throws IOException {
        eventAttendanceService.sendAttendanceList(accessor, eventId);
        return BaseResponseDto.ofSuccess(SEND_ATTENDACE_LIST_SUCCESS);
    }
}
