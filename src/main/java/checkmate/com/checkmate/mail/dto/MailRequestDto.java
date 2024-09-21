package checkmate.com.checkmate.mail.dto;

import checkmate.com.checkmate.mail.domain.MailType;
import lombok.*;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class MailRequestDto {
    private final MailType mailType;
    private final String mailTitle;
    private final String mailContent;
    private final String attachUrl;

}
