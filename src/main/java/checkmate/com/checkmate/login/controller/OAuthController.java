package checkmate.com.checkmate.login.controller;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import checkmate.com.checkmate.login.dto.request.MemberInfoRequest;
import checkmate.com.checkmate.login.dto.response.AccessTokenResponse;
import checkmate.com.checkmate.login.dto.response.OAuthMemberResponse;
import checkmate.com.checkmate.login.service.GoogleOAuthService;
import checkmate.com.checkmate.login.service.OAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import checkmate.com.checkmate.auth.MemberOnly;
import checkmate.com.checkmate.auth.Auth;

import static checkmate.com.checkmate.global.codes.SuccessCode.LOGOUT_SUCCESS;

@RestController
@RequiredArgsConstructor
public class OAuthController {
    private final OAuthService oAuthService;
    private final GoogleOAuthService googleOAuthService;

    @PostMapping(value = "/api/v2/signup")
    public ResponseEntity<OAuthMemberResponse> signup(
            @ModelAttribute @Valid final MemberInfoRequest memberInfoRequest
    ) {
        return ResponseEntity.ok(oAuthService.signup(memberInfoRequest));
    }

    @PostMapping("/api/v2/refresh")
    public ResponseEntity<AccessTokenResponse> refresh() {
        return ResponseEntity.ok(oAuthService.reissueAccessToken());
    }

    @GetMapping("/google/login/{accessToken}")
    public ResponseEntity<?> googleOAuthRequest(@PathVariable String accessToken) {
        return ResponseEntity.ok(googleOAuthService.login(accessToken));
    }

    @GetMapping("/api/v2/logout")
    @MemberOnly
    public BaseResponseDto<?> logout(
            @Auth Accessor accessor
    ) {
        oAuthService.logout(accessor.getMemberId());
        return BaseResponseDto.ofSuccess(LOGOUT_SUCCESS);
    }

/*    @DeleteMapping("/api/v2/signout")
    @MemberOnly
    public BaseResponseDto<?> signout(
            @Auth Accessor accessor
    ) {
        oAuthService.signout(accessor.getMemberId());
        return BaseResponseDto.ofSuccess(LOGOUT_SUCCESS);
    }*/
}
