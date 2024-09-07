package checkmate.com.checkmate.login.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInfoRequest {

    @NotNull(message = "사용자의 이름은 필수입니다.")
    private String name;

    private String email;

    @NotNull(message = "사용자의 소셜 아이디는 필수입니다.")
    private String socialId;

    private String socialRefreshToken;
}
