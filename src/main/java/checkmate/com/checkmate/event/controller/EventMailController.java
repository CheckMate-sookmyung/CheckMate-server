package checkmate.com.checkmate.event.controller;

import checkmate.com.checkmate.auth.Auth;
import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.event.dto.EventRatioResponseDto;
import checkmate.com.checkmate.event.service.EventMailService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static checkmate.com.checkmate.global.codes.SuccessCode.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/events/mail")
@Tag(name="행사 메일 전송", description="행사별 메일 전송 관련 API입니다.")
@ApiResponses(
        value = {
                @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
        }
)
public class EventMailController {
    @Autowired
    private final EventMailService eventMailService;

    @ResponseBody
    @GetMapping("/before/{eventId}")
    @Operation(summary = "행사 리마인드 알람")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventListResponseDto.class))),
            }
    )
    public BaseResponseDto<?> sendRemindMail(@Auth final Accessor accessor,
                                             @PathVariable("eventId") Long eventId) {
        eventMailService.sendRemindMail(accessor, eventId);
        return BaseResponseDto.ofSuccess(SEND_BEFORE_MAIL);
    }

    @ResponseBody
    @GetMapping("/after/{eventId}")
    @Operation(summary = "행사 설문조사 알람")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventListResponseDto.class))),
            }
    )
    public BaseResponseDto<?> sendSurveyMail(@Auth final Accessor accessor,
                                             @PathVariable("eventId") Long eventId) {
        eventMailService.sendServeyMail(accessor, eventId);
        return BaseResponseDto.ofSuccess(SEND_AFTER_MAIL);
    }
}
