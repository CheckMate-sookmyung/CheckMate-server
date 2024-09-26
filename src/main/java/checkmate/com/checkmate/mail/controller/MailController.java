package checkmate.com.checkmate.mail.controller;

import checkmate.com.checkmate.auth.Auth;
import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.mail.domain.MailType;
import checkmate.com.checkmate.mail.dto.MailRequestDto;
import checkmate.com.checkmate.mail.dto.MailUpdateRequestDto;
import checkmate.com.checkmate.mail.dto.MailResponseDto;
import checkmate.com.checkmate.mail.service.MailService;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import static checkmate.com.checkmate.global.codes.SuccessCode.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
@Tag(name="행사 메일 전송", description="행사별 메일 전송 관련 API입니다.")
public class MailController {
    @Autowired
    private final MailService mailService;

    @ResponseBody
    @GetMapping("/send/{eventId}")
    @Operation(summary = "행사 전후 메일 발송")
    public BaseResponseDto<?> sendRemindMail(@Parameter(hidden = true) @Auth final Accessor accessor,
                                             @PathVariable("eventId") Long eventId,
                                             @RequestParam("mailType") MailType mailType) {
        mailService.sendEventMail(accessor, eventId, mailType);
        return BaseResponseDto.ofSuccess(SEND_BEFORE_MAIL);
    }

    @ResponseBody
    @PostMapping(value="/{eventId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "메일 등록")
    public BaseResponseDto<?> registerMail(@Parameter(hidden = true) @Auth final Accessor accessor,
                                            @PathVariable("eventId") Long eventId,
                                            @RequestBody MailRequestDto mailRequestDto) {
        mailService.registerMail(accessor, eventId, mailRequestDto);
        return BaseResponseDto.ofSuccess(REGISTER_MAIL_SUCCESS);
    }

    @ResponseBody
    @GetMapping("/{eventId}")
    @Operation(summary = "메일 조회")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MailResponseDto.class))),
            }
    )
    public ResponseEntity<?> getMail(@Parameter(hidden = true) @Auth final Accessor accessor,
                                            @PathVariable("eventId") Long eventId,
                                            @RequestParam("mailType") MailType mailType) {
        MailResponseDto mailResponseDto = mailService.getMailContent(accessor, eventId, mailType);
        return ResponseEntity.ok(mailResponseDto);
    }

    @ResponseBody
    @PutMapping(value = "/{mailId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "메일 변경")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MailResponseDto.class))),
            }
    )
    public ResponseEntity<?> putMail(@Parameter(hidden = true) @Auth final Accessor accessor,
                                            @PathVariable("mailId") Long mailId,
                                            @RequestBody MailUpdateRequestDto mailUpdateRequestDto) {
        MailResponseDto mailResponseDto = mailService.updateMailContent(accessor, mailId, mailUpdateRequestDto);
        return ResponseEntity.ok(mailResponseDto);
    }

    @ResponseBody
    @DeleteMapping("/{mailId}")
    @Operation(summary = "메일 삭제")
    public BaseResponseDto<?> deleteMail(@Parameter(hidden = true) @Auth final Accessor accessor,
                                         @PathVariable("mailId") Long mailId) {
        mailService.deleteMail(accessor, mailId);
        return BaseResponseDto.ofSuccess(DELET_MAIL_SUCCESS);
    }

}
