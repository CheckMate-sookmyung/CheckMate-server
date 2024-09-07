package checkmate.com.checkmate.auth;

import checkmate.com.checkmate.auth.domain.Accessor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;

import javax.security.sasl.AuthenticationException;
import java.util.Arrays;

import static checkmate.com.checkmate.global.codes.ErrorCode.INVALID_AUTHORITY;


@Aspect
@Component
public class MemberOnlyChecker {

    @Before("@annotation(checkmate.com.checkmate.auth.MemberOnly)")
    public void check(final JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .filter(Accessor.class::isInstance)
                .map(Accessor.class::cast)
                .filter(Accessor::isMember)
                .findFirst()
                .orElseThrow(() -> new ErrorResponseException(INVALID_AUTHORITY.getHttpStatus()));
    }
}
