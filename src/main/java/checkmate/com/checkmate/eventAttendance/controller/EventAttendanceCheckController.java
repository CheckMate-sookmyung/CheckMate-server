package checkmate.com.checkmate.eventAttendance.controller;

import checkmate.com.checkmate.auth.Auth;
import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.eventAttendance.dto.*;
import checkmate.com.checkmate.eventAttendance.service.EventAttendanceService;
import checkmate.com.checkmate.global.exception.StudentAlreadyAttendedException;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static checkmate.com.checkmate.global.codes.SuccessCode.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance/check")
@Tag(name="출석체크 API", description="출석체크를 할 수 있습니다.")
public class EventAttendanceCheckController {

    @Autowired
    private final EventAttendanceService eventAttendanceService;

    @ResponseBody
    @GetMapping(value = "/studentNumber/{eventId}")
    @Operation(summary = "출석체크(학번용)")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StudentInfoResponseDto.class))),
            }
    )
    public ResponseEntity<?> getStudentInfoByStudentNumber(@Parameter(hidden = true) @Auth final Accessor accessor,
                                            @PathVariable("eventId") Long eventId,
                                            @RequestParam("studentNumber") int studentNumber,
                                            @RequestParam("eventDate") String eventDate) throws StudentAlreadyAttendedException {
        StudentInfoResponseDto studentInfo = eventAttendanceService.getStudentInfoByStudentNumber(accessor, eventId, studentNumber, eventDate);
        return ResponseEntity.ok(studentInfo);
    }

    @ResponseBody
    @GetMapping(value = "/phoneNumber/{eventId}")
    @Operation(summary = "출석체크(전번용)")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StrangerInfoResponseDto.class))),
            }
    )
    public ResponseEntity<?> getStudentInfoByPhoneNumber(@Parameter(hidden = true) @Auth final Accessor accessor,
                                            @PathVariable("eventId") Long eventId,
                                            @RequestParam("phoneNumber") String phoneNumber,
                                            @RequestParam("eventDate") String eventDate) throws StudentAlreadyAttendedException {
        List<StrangerInfoResponseDto> strangerInfo = eventAttendanceService.getStrangerInfoByPhoneNumberSuffix(accessor, eventId, phoneNumber, eventDate);
        return ResponseEntity.ok(strangerInfo);
    }

    @ResponseBody
    @PostMapping(value = "/sign/{eventId}/{attendeeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "전자서명")
    public BaseResponseDto<?> postSign(@Parameter(hidden = true) @Auth final Accessor accessor,
                                      @PathVariable("eventId") Long eventId,
                                      @PathVariable("attendeeId") Long studentInfoId,
                                      @RequestPart(value = "signImage") MultipartFile signImage) {
        eventAttendanceService.postSign(accessor, eventId, studentInfoId, signImage);
        return BaseResponseDto.ofSuccess(ATTENDANCE_CHECK_SUCCESS);
    }

}
