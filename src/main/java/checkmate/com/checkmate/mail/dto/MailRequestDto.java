package checkmate.com.checkmate.mail.dto;

import checkmate.com.checkmate.mail.domain.MailType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Schema(description = "Mail Register Request")
public class MailRequestDto {

    @Schema(description = "메일 유형", example = "REMIND")
    private final MailType mailType;

    @Size(max = 250, message = "메일에 삽입할 주소는 250자를 초과할 수 없습니다.")
    @Schema(description = "메일 삽입 주소", example = "https://checkmate.pe.kr")
    private final String attachUrl;
}
