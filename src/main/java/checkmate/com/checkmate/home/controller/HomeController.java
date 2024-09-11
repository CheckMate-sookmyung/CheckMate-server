package checkmate.com.checkmate.home.controller;

import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.eventschedule.dto.EventScheduleRequestDto;
import checkmate.com.checkmate.eventschedule.service.EventScheduleService;
import checkmate.com.checkmate.home.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/home")
@Tag(name="홈 화면 API", description="이벤트 수와 사용자 수를 조회할 수 있습니다.")
public class HomeController {

    @Autowired
    private final HomeService homeService;

    @ResponseBody
    @GetMapping(value="/event")
    @Operation(summary = "진행한 행사 수 조회")
    public ResponseEntity<?> getEventTimes() {
        long eventTimes = homeService.getEventTimes();
        return ResponseEntity.ok(eventTimes);
    }

    @ResponseBody
    @GetMapping(value="/attendance")
    @Operation(summary = "출석한 참석자 수 조회")
    public ResponseEntity<?> getAttendanceTimes() {
        long attendanceTimes = homeService.geAttendanceTimes();
        return ResponseEntity.ok(attendanceTimes);
    }
}
