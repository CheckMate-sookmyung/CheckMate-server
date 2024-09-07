package checkmate.com.checkmate.eventAttendance.controller;

import checkmate.com.checkmate.eventAttendance.dto.EventAttendanceRequestDto;
import checkmate.com.checkmate.eventAttendance.dto.EventAttendanceResponseDto;
import checkmate.com.checkmate.eventAttendance.dto.StudentInfoResponseDto;
import checkmate.com.checkmate.eventAttendance.service.EventAttendanceService;
import checkmate.com.checkmate.global.config.S3Download;
import checkmate.com.checkmate.global.exception.StudentAlreadyAttendedException;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import checkmate.com.checkmate.global.responseDto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/attendance")
@Tag(name="출석체크", description="출석체크를 할 수 있습니다.")
@ApiResponses(
        value = {
                @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        }
)
public class EventAttendanceController {

    @Autowired
    private final EventAttendanceService eventAttendanceService;
    private final S3Download s3Download;

    @ResponseBody
    @GetMapping(value = "/check/studentNumber/{userId}/{eventId}")
    @Operation(summary = "출석체크(학번용)")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StudentInfoResponseDto.class))),
            }
    )
    public ResponseEntity<?> getStudentInfoByStudentNumber(@PathVariable("userId") Long userId,
                                            @PathVariable("eventId") Long eventId,
                                            @RequestParam("studentNumber") int studentNumber,
                                            @RequestParam("eventDate") String eventDate) throws StudentAlreadyAttendedException {
        StudentInfoResponseDto studentInfo = eventAttendanceService.getStudentInfoByStudentNumber(userId, eventId, studentNumber, eventDate);
        return ResponseEntity.ok(studentInfo);
    }

    @ResponseBody
    @GetMapping(value = "/check/phoneNumber/{userId}/{eventId}")
    @Operation(summary = "출석체크(전번용)")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StudentInfoResponseDto.class))),
            }
    )
    public ResponseEntity<?> getStudentInfoByPhoneNumber(@PathVariable("userId") Long userId,
                                            @PathVariable("eventId") Long eventId,
                                            @RequestParam("phoneNumber") String phoneNumber,
                                            @RequestParam("eventDate") String eventDate) throws StudentAlreadyAttendedException {
        List<StudentInfoResponseDto> studentInfo = eventAttendanceService.getStudentInfoByPhoneNumberSuffix(userId, eventId, phoneNumber, eventDate);
        return ResponseEntity.ok(studentInfo);
    }

    @ResponseBody
    @PostMapping(value = "/sign/{userId}/{eventId}/{studentInfoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "전자서명")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content),
            }
    )
    public BaseResponseDto<?> postSign(@PathVariable("userId") Long userId,
                                      @PathVariable("eventId") Long eventId,
                                      @PathVariable("studentInfoId") Long studentInfoId,
                                      @RequestPart(value = "signImage") MultipartFile signImage) {
        eventAttendanceService.postSign(userId, eventId, studentInfoId, signImage);
        return BaseResponseDto.ofSuccess(ATTENDANCE_CHECK_SUCCESS);
    }

    @ResponseBody
    @GetMapping(value="/list/{userId}/{eventId}")
    @Operation(summary="출석명단 다운")
    public ResponseEntity<?> sendAttendanceListManually(@PathVariable("userId") Long userId,
                                                 @PathVariable("eventId") Long eventId) throws IOException {
        List<String> filenames = eventAttendanceService.downloadAttendanceList(userId, eventId);
        return s3Download.getObject(filenames.get(0), filenames.get(1));
    }

    @ResponseBody
    @GetMapping(value="/list/sending/{userId}/{eventId}")
    @Operation(summary="출석명단 자동 전송")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content),
            }
    )
    public BaseResponseDto<?> sendAttendanceListAutomatically(@PathVariable("userId") Long userId,
                                                @PathVariable("eventId") Long eventId) throws IOException {
        eventAttendanceService.sendAttendanceList(userId, eventId);
        return BaseResponseDto.ofSuccess(SEND_ATTENDACE_LIST_SUCCESS);
    }

    @ResponseBody
    @PutMapping(value="/list/{userId}/{eventId}")
    @Operation(summary = "출석명단 수정")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventAttendanceResponseDto.class))),
            }
    )
    public ResponseEntity<?> updateAttendanceList(@PathVariable("userId") Long userId,
                                                   @PathVariable("eventId") Long eventId,
                                                   @RequestPart("attendanceList") List<EventAttendanceRequestDto> eventAttendanceRequestDto){
        List<EventAttendanceResponseDto> eventAttendanceResponseDtos = eventAttendanceService.updateAttendanceList(userId, eventId, eventAttendanceRequestDto);
        return ResponseEntity.ok(eventAttendanceResponseDtos);
    }
}
