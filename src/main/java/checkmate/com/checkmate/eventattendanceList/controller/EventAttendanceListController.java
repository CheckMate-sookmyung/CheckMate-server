package checkmate.com.checkmate.eventattendanceList.controller;

import checkmate.com.checkmate.eventattendanceList.dto.StudentInfoResponseDto;
import checkmate.com.checkmate.eventattendanceList.service.EventAttendanceListService;
import checkmate.com.checkmate.global.exception.StudentAlreadyAttendedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/attendance")
@Tag(name="출석체크", description="출석체크를 할 수 있습니다.")
public class EventAttendanceListController {

    @Autowired
    private final EventAttendanceListService eventAttendanceListService;

    @ResponseBody
    @GetMapping(value="/check/{userId}/{eventId}/{studentNumber}")
    @Operation(summary = "출석체크")
    public ResponseEntity<?> getStudentInfo(@PathVariable("userId") Long userId,
                                            @PathVariable("eventId") Long eventId,
                                            @PathVariable("studentNumber") int studentNumber,
                                            @RequestHeader("eventDate") String eventDate) throws StudentAlreadyAttendedException {
        try {
            StudentInfoResponseDto studentInfo = eventAttendanceListService.getStudentInfo(userId, eventId, studentNumber, eventDate);
            return ResponseEntity.ok().body(studentInfo);
        } catch (StudentAlreadyAttendedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 출석한 학생입니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @ResponseBody
    @PostMapping(value="/sign/{userId}/{eventId}/{studentInfoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "전자서명")
    public ResponseEntity<?> postSign(@PathVariable("userId") Long userId,
                                      @PathVariable("eventId") Long eventId,
                                      @PathVariable("studentInfoId") Long studentInfoId,
                                      @RequestPart(value="signImage") MultipartFile signImage){
        eventAttendanceListService.postSign(userId,eventId,studentInfoId,signImage);
        return ResponseEntity.ok().build();
    }

}
