package checkmate.com.checkmate.login.service;


import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.login.dto.google.GoogleMemberOauthResponse;
import checkmate.com.checkmate.login.dto.google.GoogleMemberResponse;
import checkmate.com.checkmate.login.dto.google.GoogleTokenResponse;
import checkmate.com.checkmate.login.dto.response.AccessTokenResponse;
import checkmate.com.checkmate.login.dto.response.OAuthMemberResponse;
import checkmate.com.checkmate.login.provider.JwtProvider;
import checkmate.com.checkmate.member.domain.Member;
import checkmate.com.checkmate.member.domain.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;


import java.util.Optional;

import static checkmate.com.checkmate.global.codes.ErrorCode.*;


@Service
@RequiredArgsConstructor
public class GoogleOAuthService {
    private final MemberRepository memberRepository;
    private final WebClient webClient;
    private final JwtProvider jwtProvider;

    @Value("${oauth2.google.client-id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${oauth2.google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;
    @Value("${oauth2.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;
    @Value("${oauth2.google.base-url}")
    private String GOOGLE_BASE_URL;
    @Value("${oauth2.google.user-base-url}")
    private String GOOGLE_USER_BASE_URL;

    @Transactional
    public OAuthMemberResponse login(final String accessToken) {
        GoogleMemberResponse memberResponse = getUserInfo(accessToken);

        Member member = memberRepository.findMemberBySocialId(memberResponse.getId()).orElse(null);

        if (member==null) {
            return OAuthMemberResponse.builder()
                    .socialId(memberResponse.getId())
                    .name(memberResponse.getName())
                    .email(memberResponse.getEmail())
                    .isNewMember(true)
                    .build();
        }

        String appRefreshToken = jwtProvider.createRefreshToken();
        String appAccessToken = jwtProvider.createAccessToken(member.getMemberId().toString());

        member.updateRefreshToken(appRefreshToken);
        memberRepository.save(member);
        return new OAuthMemberResponse(member, false, appAccessToken);
    }

    /**
     * Get User Info Using Access Token
     */
    public GoogleMemberResponse getUserInfo(final String accessToken) {
        GoogleMemberResponse googleMemberResponse = webClient.get()
                .uri(GOOGLE_USER_BASE_URL, uriBuilder -> uriBuilder.queryParam("access_token", accessToken).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("Social Access Token is unauthorized")))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(GoogleMemberResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElseThrow(() -> new GeneralException(INVALID_USER_INFO));

        return googleMemberResponse;
    }

    /**
     * Get Access Token
     */
    public GoogleTokenResponse getAccessToken(final String code) {
        GoogleTokenResponse googleTokenResponse = webClient.post()
                .uri(GOOGLE_BASE_URL, uriBuilder -> uriBuilder
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", GOOGLE_CLIENT_ID)
                        .queryParam("client_secret", GOOGLE_CLIENT_SECRET)
                        .queryParam("redirect_uri", GOOGLE_REDIRECT_URI)
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("Social Access Token is unauthorized")))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(GoogleTokenResponse.class)
                .flux()
                .toStream()
                .findFirst()
                .orElseThrow(() -> new GeneralException(INVALID_OAUTH_TOKEN));

        return googleTokenResponse;
    }

/*    public AccessTokenResponse processGoogleOAuth(String authorizationCode) {
        String accessToken = getAccessToken(authorizationCode);
        return AccessTokenResponse.builder()
                .accessToken(accessToken)
                .build();
    }*/
}
