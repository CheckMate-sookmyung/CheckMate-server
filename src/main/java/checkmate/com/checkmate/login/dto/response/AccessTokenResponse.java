package checkmate.com.checkmate.login.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenResponse {
    private String accessToken;
}