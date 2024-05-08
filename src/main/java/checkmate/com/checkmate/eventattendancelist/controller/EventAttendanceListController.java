package checkmate.com.checkmate.eventattendancelist.controller;

import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.event.dto.EventRequestDto;
import checkmate.com.checkmate.event.service.EventService;
import checkmate.com.checkmate.eventattendancelist.domain.EventAttendanceList;
import checkmate.com.checkmate.eventattendancelist.dto.StudentInfoResponseDto;
import checkmate.com.checkmate.eventattendancelist.service.EventAttendanceListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
                                            @RequestHeader("eventDate") String eventDate){
        StudentInfoResponseDto studentInfo = eventAttendanceListService.getStudentInfo(userId, eventId, studentNumber, eventDate);
        return ResponseEntity.ok().body(studentInfo);
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
