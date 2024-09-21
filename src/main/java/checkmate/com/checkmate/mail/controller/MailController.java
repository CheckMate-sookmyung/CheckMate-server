package checkmate.com.checkmate.mail.controller;

import checkmate.com.checkmate.auth.Auth;
import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.dto.EventListResponseDto;
import checkmate.com.checkmate.mail.domain.MailType;
import checkmate.com.checkmate.mail.dto.MailRequestDto;
import checkmate.com.checkmate.mail.dto.MailResponseDto;
import checkmate.com.checkmate.mail.service.MailService;
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
public class MailController {
    @Autowired
    private final MailService mailService;

    @ResponseBody
    @GetMapping("/{eventId}")
    @Operation(summary = "행사 전후 메일 발송")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventListResponseDto.class))),
            }
    )
    public BaseResponseDto<?> sendRemindMail(@Auth final Accessor accessor,
                                             @PathVariable("eventId") Long eventId,
                                             @RequestParam("mailType") MailType mailType) {
        mailService.sendEventMail(accessor, eventId, mailType);
        return BaseResponseDto.ofSuccess(SEND_BEFORE_MAIL);
    }


    @ResponseBody
    @GetMapping("/content/{eventId}")
    @Operation(summary = "메일 내용 조회")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventListResponseDto.class))),
            }
    )
    public ResponseEntity<?> getMailContent(@Auth final Accessor accessor,
                                            @PathVariable("eventId") Long eventId,
                                            @RequestParam("mailType") MailType mailType) {
        MailResponseDto mailResponseDto = mailService.getMailContent(accessor, eventId, mailType);
        return ResponseEntity.ok(mailResponseDto);
    }

    @ResponseBody
    @PutMapping("/content/{mailId}")
    @Operation(summary = "메일 내용 변경")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EventListResponseDto.class))),
            }
    )
    public ResponseEntity<?> putMailContent(@Auth final Accessor accessor,
                                            @PathVariable("mailId") Long mailId,
                                            @RequestBody MailRequestDto mailRequestDto) {
        MailResponseDto mailResponseDto = mailService.updateMailContent(accessor, mailId, mailRequestDto);
        return ResponseEntity.ok(mailResponseDto);
    }
}
