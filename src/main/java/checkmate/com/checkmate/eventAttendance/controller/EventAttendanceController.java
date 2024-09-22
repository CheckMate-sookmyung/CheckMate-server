package checkmate.com.checkmate.eventAttendance.controller;

import checkmate.com.checkmate.auth.Auth;
import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.eventAttendance.dto.*;
import checkmate.com.checkmate.eventAttendance.service.EventAttendanceService;
import checkmate.com.checkmate.global.config.S3Download;
import checkmate.com.checkmate.global.domain.EventTarget;
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
    @GetMapping(value = "/check/studentNumber/{eventId}")
    @Operation(summary = "출석체크(학번용)")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StudentInfoResponseDto.class))),
            }
    )
    public ResponseEntity<?> getStudentInfoByStudentNumber(@Auth final Accessor accessor,
                                            @PathVariable("eventId") Long eventId,
                                            @RequestParam("studentNumber") int studentNumber,
                                            @RequestParam("eventDate") String eventDate) throws StudentAlreadyAttendedException {
        StudentInfoResponseDto studentInfo = eventAttendanceService.getStudentInfoByStudentNumber(accessor, eventId, studentNumber, eventDate);
        return ResponseEntity.ok(studentInfo);
    }

    @ResponseBody
    @GetMapping(value = "/check/phoneNumber/{eventId}")
    @Operation(summary = "출석체크(전번용)")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StudentInfoResponseDto.class))),
            }
    )
    public ResponseEntity<?> getStudentInfoByPhoneNumber(@Auth final Accessor accessor,
                                            @PathVariable("eventId") Long eventId,
                                            @RequestParam("phoneNumber") String phoneNumber,
                                            @RequestParam("eventDate") String eventDate) throws StudentAlreadyAttendedException {
        List<StrangerInfoResponseDto> strangerInfo = eventAttendanceService.getStrangerInfoByPhoneNumberSuffix(accessor, eventId, phoneNumber, eventDate);
        return ResponseEntity.ok(strangerInfo);
    }

    @ResponseBody
    @PostMapping(value = "/sign/{eventId}/{studentInfoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "전자서명")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content),
            }
    )
    public BaseResponseDto<?> postSign(@Auth final Accessor accessor,
                                      @PathVariable("eventId") Long eventId,
                                      @PathVariable("studentInfoId") Long studentInfoId,
                                      @RequestPart(value = "signImage") MultipartFile signImage) {
        eventAttendanceService.postSign(accessor, eventId, studentInfoId, signImage);
        return BaseResponseDto.ofSuccess(ATTENDANCE_CHECK_SUCCESS);
    }

    @ResponseBody
    @GetMapping(value="/list/{eventId}")
    @Operation(summary="출석명단 다운")
    public ResponseEntity<?> sendAttendanceListManually(@Auth final Accessor accessor,
                                                 @PathVariable("eventId") Long eventId) throws IOException {
        String filenames = eventAttendanceService.downloadAttendanceList(accessor, eventId);
        return ResponseEntity.ok(filenames);
    }

    @ResponseBody
    @GetMapping(value="/list/upload/{eventScheduleId}")
    @Operation(summary="온라인 참석명단 업로드")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content),
            }
    )
    public ResponseEntity<?> uploadAttendanceListAboutOnline(@Auth final Accessor accessor,
                                                             @PathVariable("eventScheduleId") Long eventScheduleId,
                                                             @RequestPart(value="attendanceListFile") MultipartFile attendanceFile) throws IOException {
        String fileUrl = eventAttendanceService.uploadAttendanceListAboutOnline(accessor, eventScheduleId, attendanceFile);
        return ResponseEntity.ok(fileUrl);
    }

    @ResponseBody
    @PostMapping(value="/list/sending/{eventId}")
    @Operation(summary="출석명단 자동 전송")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content),
            }
    )
    public BaseResponseDto<?> sendAttendanceListAutomatically(@Auth final Accessor accessor,
                                                              @PathVariable("eventId") Long eventId) throws IOException {
        eventAttendanceService.sendAttendanceList(accessor, eventId);
        return BaseResponseDto.ofSuccess(SEND_ATTENDACE_LIST_SUCCESS);
    }

    @ResponseBody
    @PutMapping(value="/list/{eventId}")
    @Operation(summary = "출석명단 출석 수정")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StudentEventAttendanceResponseDto.class))),
            }
    )
    public ResponseEntity<?> updateAttendanceList(@Auth final Accessor accessor,
                                                   @PathVariable("eventId") Long eventId,
                                                   @RequestPart("attendanceList") List<EventAttendanceRequestDto> eventAttendanceRequestDto){
        List<?> studentEventAttendanceResponseDtos = eventAttendanceService.updateAttendanceList(accessor, eventId, eventAttendanceRequestDto);
        return ResponseEntity.ok(studentEventAttendanceResponseDtos);
    }

    @ResponseBody
    @PutMapping(value="/list/{eventId}/{eventScheduleId}/{attendeeId}")
    @Operation(summary = "출석명단 중 참석자 삭제")
    public BaseResponseDto<?> deleteAttendanceList(@Auth final Accessor accessor,
                                                  @PathVariable("eventId") Long eventId,
                                                  @PathVariable("eventScheduleId") Long eventScheduleId,
                                                  @PathVariable("attendeeId") Long attendeeId){
        eventAttendanceService.deleteAttendanceList(accessor, eventId, eventScheduleId, attendeeId);
        return BaseResponseDto.ofSuccess(REMOVE_ATTENDANCE_SUCCESS);
    }

    @ResponseBody
    @PostMapping(value="/list/{eventId}/{eventScheduleId}")
    @Operation(summary = "출석명단 추가")

    public BaseResponseDto<?> registerAttendee(@Auth final Accessor accessor,
                                               @PathVariable("eventId") Long eventId,
                                               @PathVariable("eventScheduleId") Long eventScheduleId,
                                               @RequestPart("attendeeInfo") List<EventAttendanceDetailRequestDto> eventAttendanceDetailRequestDtos){
        eventAttendanceService.addAttendee(accessor, eventId, eventScheduleId, eventAttendanceDetailRequestDtos);
        return BaseResponseDto.ofSuccess(SEND_ATTENDACE_LIST_SUCCESS);
    }
}
