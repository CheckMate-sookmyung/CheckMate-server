package checkmate.com.checkmate.eventattendanceList.controller;

import checkmate.com.checkmate.eventattendanceList.dto.StudentInfoResponseDto;
import checkmate.com.checkmate.eventattendanceList.service.EventAttendanceListService;
import checkmate.com.checkmate.global.exception.StudentAlreadyAttendedException;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static checkmate.com.checkmate.global.codes.SuccessCode.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance")
@Tag(name="출석체크", description="출석체크를 할 수 있습니다.")
@ApiResponses(
        value = {
                @ApiResponse(responseCode = "200"),
                @ApiResponse(responseCode = "404", content = @Content)
        }
)
public class EventAttendanceListController {

    @Autowired
    private final EventAttendanceListService eventAttendanceListService;

    @ResponseBody
    @GetMapping(value = "/check/{userId}/{eventId}/{studentNumber}")
    @Operation(summary = "출석체크")
    public BaseResponseDto<StudentInfoResponseDto> getStudentInfo(@PathVariable("userId") Long userId,
                                                                  @PathVariable("eventId") Long eventId,
                                                                  @PathVariable("studentNumber") int studentNumber,
                                                                  @RequestParam("eventDate") String eventDate) throws StudentAlreadyAttendedException {
        StudentInfoResponseDto studentInfo = eventAttendanceListService.getStudentInfo(userId, eventId, studentNumber, eventDate);
        return BaseResponseDto.ofSuccess(GET_STUDENT_INFO_SUCCESS, studentInfo);
    }

    @ResponseBody
    @PostMapping(value = "/sign/{userId}/{eventId}/{studentInfoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "전자서명")
    public BaseResponseDto<?> postSign(@PathVariable("userId") Long userId,
                                      @PathVariable("eventId") Long eventId,
                                      @PathVariable("studentInfoId") Long studentInfoId,
                                      @RequestPart(value = "signImage") MultipartFile signImage) {
        eventAttendanceListService.postSign(userId, eventId, studentInfoId, signImage);
        return BaseResponseDto.ofSuccess(ATTENDANCE_CHECK_SUCCESS);
    }

    @ResponseBody
    @GetMapping(value="/list/{userId}/{eventId}")
    @Operation(summary="출석명단 전송")
    public BaseResponseDto<?> sendAttendanceList(@PathVariable("userId") Long userId,
                                                @PathVariable("eventId") Long eventId) throws IOException {
        eventAttendanceListService.sendAttendanceList(userId, eventId);
        return BaseResponseDto.ofSuccess(SEND_ATTENDACE_LIST_SUCCESS);
    }
}
