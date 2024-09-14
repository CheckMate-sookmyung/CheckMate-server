package checkmate.com.checkmate.event.mail.dto;

import checkmate.com.checkmate.event.mail.domain.MailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class MailRequestDto {
    private final MailType mailType;
    private final String mailTitle;
    private final String mailContent;
    private final String attachUrl;

    public static MailRequestDto of(MailType mailType, String mailTitle, String mailContent, String attachUrl) {
        return MailRequestDto.builder()
                .mailType(mailType)
                .mailTitle(mailTitle)
                .mailContent(mailContent)
                .attachUrl(attachUrl)
                .build();
    }
}
