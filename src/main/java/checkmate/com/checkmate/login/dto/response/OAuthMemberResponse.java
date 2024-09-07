package checkmate.com.checkmate.login.dto.response;

import checkmate.com.checkmate.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthMemberResponse {
    private Long id;
    private String socialId;
    private String name;
    private String email;
    private String accessToken;
    private String refreshToken;
    private Boolean isNewMember;

    @Builder
    public OAuthMemberResponse(
            final String socialId,
            final String name,
            final String email,
            final String accessToken,
            final String refreshToken,
            final Boolean isNewMember
    ) {
        this.socialId = socialId;
        this.name = name;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isNewMember = isNewMember;
    }

    public OAuthMemberResponse(
            final Member member,
            final Boolean isNewMember,
            final String accessToken
    ) {
        this.id = member.getMemberId();
        this.socialId = member.getSocialId();
        this.name = member.getMemberName();
        this.email = member.getMemberEmail();
        this.accessToken = accessToken;
        this.refreshToken = member.getRefreshToken();
        this.isNewMember = isNewMember;
    }
}
