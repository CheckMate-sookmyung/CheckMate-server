package checkmate.com.checkmate.event.controller;

import checkmate.com.checkmate.auth.Auth;
import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.event.dto.EventRatioResponseDto;
import checkmate.com.checkmate.event.dto.MostAttendeeRatioEventResponseDto;
import checkmate.com.checkmate.event.dto.MostFrequentParticipantsResponseDto;
import checkmate.com.checkmate.event.service.EventService;
import checkmate.com.checkmate.event.service.EventStatisticService;
import checkmate.com.checkmate.global.responseDto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name="행사 세부 통계", description="행사별 세부 통계 내용을 조회할 수 있습니다.")
@ApiResponses(
        value = {
                @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        }
)
public class EventStatisticController {
    @Autowired
    private final EventStatisticService eventStatisticService;

    @ResponseBody
    @GetMapping("/{eventId}")
    @Operation(summary = "행사별 세부통계")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventRatioResponseDto.class))),
            }
    )
    public ResponseEntity<?> getEventRatio(@Auth final Accessor accessor,
                                               @PathVariable("eventId") Long eventId) {
        EventRatioResponseDto eventRatio = eventStatisticService.getEventRatioAboutEvent(accessor, eventId);
        return ResponseEntity.ok(eventRatio);
    }

    @ResponseBody
    @GetMapping("/student")
    @Operation(summary = "전체 통계 - 학생")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MostFrequentParticipantsResponseDto.class))),
            }
    )
    public ResponseEntity<?> getMostFrequentParticipants(@Auth final Accessor accessor) {
        List<MostFrequentParticipantsResponseDto> studentStatistic = eventStatisticService.getMostFrequentParticipants(accessor);
        return ResponseEntity.ok(studentStatistic);
    }

    @ResponseBody
    @GetMapping("/event")
    @Operation(summary = "전체 통계 - 행사")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MostAttendeeRatioEventResponseDto.class))),
            }
    )
    public ResponseEntity<?> getMostAttendeeRatioEvents(@Auth final Accessor accessor) {
        List<MostAttendeeRatioEventResponseDto> mostAttendeeRatioEventResponseDtos = eventStatisticService.getMostAttendeeRatioEvents(accessor);
        return ResponseEntity.ok(mostAttendeeRatioEventResponseDtos);
    }

}
