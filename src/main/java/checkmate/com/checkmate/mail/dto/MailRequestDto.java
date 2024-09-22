package checkmate.com.checkmate.mail.dto;

import checkmate.com.checkmate.mail.domain.MailType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Schema(description = "Mail Request")
public class MailRequestDto {

    @Schema(description = "메일 유형", example = "REMIND")
    private final MailType mailType;

    @Size(max = 30, message = "메일 제목은 30자를 초과할 수 없습니다.")
    @Schema(description = "메일 제목", example = "메일 제목")
    private final String mailTitle;

    @Size(max = 250, message = "메일 내용은 250자를 초과할 수 없습니다.")
    @Schema(description = "메일 내용", example = "메일 내용")
    private final String mailContent;

    @Size(max = 250, message = "메일에 삽입할 주소는 250자를 초과할 수 없습니다.")
    @Schema(description = "메일 삽입 주소", example = "https://checkmate.pe.kr")
    private final String attachUrl;

}
