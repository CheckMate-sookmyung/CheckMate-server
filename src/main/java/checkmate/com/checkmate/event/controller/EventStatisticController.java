package checkmate.com.checkmate.event.controller;

import checkmate.com.checkmate.auth.Auth;
import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.dto.EventStatisticResponseDto;
import checkmate.com.checkmate.event.dto.MostAttendeeRatioEventResponseDto;
import checkmate.com.checkmate.event.dto.BestAttendeeResponseDto;
import checkmate.com.checkmate.event.service.EventStatisticService;
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

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/events/statistic")
@Tag(name="행사 통계 API", description="행사별 세부 통계와 전체 통계 내용을 조회할 수 있습니다.")
public class EventStatisticController {
    @Autowired
    private final EventStatisticService eventStatisticService;

    @ResponseBody
    @GetMapping("/{eventId}")
    @Operation(summary = "행사별 세부통계")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventStatisticResponseDto.class))),
            }
    )
    public ResponseEntity<?> getEventRatio(@Parameter(hidden = true) @Auth final Accessor accessor,
                                           @PathVariable("eventId") Long eventId) {
        EventStatisticResponseDto eventRatio = eventStatisticService.getEventRatioAboutEvent(accessor, eventId);
        return ResponseEntity.ok(eventRatio);
    }

    @ResponseBody
    @GetMapping("/student")
    @Operation(summary = "전체 통계 - 출석 횟수가 많은 학생순")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = BestAttendeeResponseDto.class))),
            }
    )
    public ResponseEntity<?> getMostFrequentParticipants(@Parameter(hidden = true) @Auth final Accessor accessor) {
        List<BestAttendeeResponseDto> studentStatistic = eventStatisticService.getMostFrequentParticipants(accessor);
        return ResponseEntity.ok(studentStatistic);
    }

    @ResponseBody
    @GetMapping("/event")
    @Operation(summary = "전체 통계 - 출석률이 좋은 행사순")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MostAttendeeRatioEventResponseDto.class))),
            }
    )
    public ResponseEntity<?> getMostAttendeeRatioEvents(@Parameter(hidden = true) @Auth final Accessor accessor) {
        List<MostAttendeeRatioEventResponseDto> mostAttendeeRatioEventResponseDtos = eventStatisticService.getMostAttendeeRatioEvents(accessor);
        return ResponseEntity.ok(mostAttendeeRatioEventResponseDtos);
    }

}
