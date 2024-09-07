package checkmate.com.checkmate.login.util;

import checkmate.com.checkmate.global.exception.GeneralException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import static checkmate.com.checkmate.global.codes.ErrorCode.INVALID_OAUTH_TOKEN;
import static checkmate.com.checkmate.global.codes.ErrorCode.NULL_TOKEN;

public class JwtHeaderUtil {

    public static String getAccessToken() {
        return getToken("Authorization");
    }

    public static String getRefreshToken() {
        return getToken("RefreshToken");
    }

    private static String getToken(final String tokenHeader) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String headerValue = request.getHeader(tokenHeader);
        if (headerValue == null || headerValue.isEmpty()) throw new GeneralException(NULL_TOKEN);
        if (StringUtils.hasText(headerValue) && headerValue.startsWith("Bearer")) {
            return headerValue.substring("Bearer".length());
        }
        throw new GeneralException(INVALID_OAUTH_TOKEN);
    }
}
