package checkmate.com.checkmate.login.filter;

import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.login.provider.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {

        String servletPath = request.getServletPath();
        final String headerValue = request.getHeader("Authorization");

        if (servletPath.equals("/api/v2/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            try {
                if (headerValue != null && headerValue.startsWith("Bearer")) {

                    String accessToken = headerValue.substring("Bearer".length());
                    if (jwtProvider.validateAccessToken(accessToken)) {
                        Authentication authentication = getAuthentication(accessToken);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
                filterChain.doFilter(request, response);
            } catch (GeneralException e) {
                //exceptionHandler(response, e);
            }
        }
    }

    private Authentication getAuthentication(final String accessToken) {
        Claims claims = jwtProvider.getTokenClaims(accessToken);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{claims.get("role").toString()})
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(
                jwtProvider.getSubject(accessToken),
                accessToken,
                authorities
        );
    }

/*    private void exceptionHandler(
            final HttpServletResponse response,
            final GeneralException error
    ) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        ErrorReasonDTO errorReasonDTO = error.getErrorReasonHttpStatus();

        ObjectMapper mapper = new ObjectMapper();
        String result = mapper.writeValueAsString(errorReasonDTO);

        response.setStatus(errorReasonDTO.getHttpStatus().value());
        response.getWriter().write(result);
    }*/
}
