package checkmate.com.checkmate.event.mail.dto;

import checkmate.com.checkmate.event.mail.domain.Mail;
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
    private final Long mailId;
    private final String mailTitle;
    private final String mailContent;
    private final String mailUrl;

    public static MailResponseDto of(Mail mail) {
        return MailResponseDto.builder()
                .mailId(mail.getMailId())
                .mailTitle(mail.getMailTitle())
                .mailContent(mail.getMailContent())
                .mailUrl(mail.getAttachUrl())
                .build();
    }
}
