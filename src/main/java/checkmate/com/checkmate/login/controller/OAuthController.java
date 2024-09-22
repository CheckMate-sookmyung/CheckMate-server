package checkmate.com.checkmate.login.controller;

import checkmate.com.checkmate.auth.domain.Accessor;
import checkmate.com.checkmate.event.dto.EventDetailResponseDto;
import checkmate.com.checkmate.global.responseDto.BaseResponseDto;
import checkmate.com.checkmate.login.dto.google.GoogleMemberOauthResponse;
import checkmate.com.checkmate.login.dto.google.GoogleMemberResponse;
import checkmate.com.checkmate.login.dto.google.GoogleTokenResponse;
import checkmate.com.checkmate.login.dto.request.MemberInfoRequest;
import checkmate.com.checkmate.login.dto.response.AccessTokenResponse;
import checkmate.com.checkmate.login.dto.response.OAuthMemberResponse;
import checkmate.com.checkmate.login.service.GoogleOAuthService;
import checkmate.com.checkmate.login.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import checkmate.com.checkmate.member.domain.Member;
import checkmate.com.checkmate.member.domain.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import checkmate.com.checkmate.auth.MemberOnly;
import checkmate.com.checkmate.auth.Auth;

import java.util.Optional;

import static checkmate.com.checkmate.global.codes.SuccessCode.LOGOUT_SUCCESS;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name="구글 로그인 API", description="구글 로그인을 할 수 있습니다.")
public class OAuthController {
    private final OAuthService oAuthService;
    private final GoogleOAuthService googleOAuthService;

    @PostMapping(value = "/signup")
    @Operation(summary = "회원가입")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = OAuthMemberResponse.class))),
            }
    )
    public ResponseEntity<OAuthMemberResponse> signup(
        @ModelAttribute @Valid final MemberInfoRequest memberInfoRequest
    ) {
            return ResponseEntity.ok(oAuthService.signup(memberInfoRequest));
        }


    @GetMapping(value ="/code")
    @Operation(summary = "로그인 시 accessToken 발급")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = GoogleTokenResponse.class))),
            }
    )
    public ResponseEntity<GoogleTokenResponse> googleOAuthCallback(@RequestParam("code") String code) {
        GoogleTokenResponse googleTokenResponse = googleOAuthService.getAccessToken(code);
        return ResponseEntity.ok(googleTokenResponse);
    }

    @PostMapping(value ="/refresh")
    @Operation(summary = "accessToken 만료 시 재발급")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = AccessTokenResponse.class))),
            }
    )
    public ResponseEntity<AccessTokenResponse> refresh() {
        return ResponseEntity.ok(oAuthService.reissueAccessToken());
    }

    @GetMapping(value ="/login")
    @Operation(summary = "로그인")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = OAuthMemberResponse.class))),
            }
    )
    public ResponseEntity<OAuthMemberResponse> googleOAuthRequest(@RequestParam("accessToken") String accessToken) {
        return ResponseEntity.ok(googleOAuthService.login(accessToken));
    }

    @GetMapping(value ="/logout")
    @Operation(summary = "로그아웃")
    @MemberOnly
    public BaseResponseDto<?> logout(
            @Parameter(hidden = true) @Auth Accessor accessor
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
