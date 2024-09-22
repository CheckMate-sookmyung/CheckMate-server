package checkmate.com.checkmate.event.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
@Schema(description = "Event Manager Request")
public class EventManagerRequestDto {

    @Schema(description = "행사 담당자 이름", example = "김숙명")
    private final String managerName;

    @Schema(description = "행사 담당자 전화번호", example = "010-5336-5708")
    private final String managerPhoneNumber;

    @Schema(description = "행사 담당자 메일주소", example = "checkmatesmwu@gmail.com")
    private final String managerEmail;
}
