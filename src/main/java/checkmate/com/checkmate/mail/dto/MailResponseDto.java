package checkmate.com.checkmate.mail.dto;

import checkmate.com.checkmate.mail.domain.Mail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Builder
@RequiredArgsConstructor(access=PRIVATE)
@Schema(description = "Mail Response")
public class MailResponseDto {

    @Schema(description = "메일 ID", example = "1")
    private final Long mailId;

    @Schema(description = "메일 제목", example = "메일 제목")
    private final String mailTitle;

    @Schema(description = "메일 내용", example = "메일 내용")
    private final String mailContent;

    @Schema(description = "메일 삽입 주소", example = "https://checkmate.pe.kr")
    private final String attachUrl;

    public static MailResponseDto of(Mail mail) {
        return MailResponseDto.builder()
                .mailId(mail.getMailId())
                .mailTitle(mail.getMailTitle())
                .mailContent(mail.getMailContent())
                .attachUrl(mail.getAttachUrl())
                .build();
    }
}
