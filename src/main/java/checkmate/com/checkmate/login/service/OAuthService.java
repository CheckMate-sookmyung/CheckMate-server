package checkmate.com.checkmate.login.service;

import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.login.dto.request.MemberInfoRequest;
import checkmate.com.checkmate.login.dto.response.AccessTokenResponse;
import checkmate.com.checkmate.login.dto.response.OAuthMemberResponse;
import checkmate.com.checkmate.login.provider.JwtProvider;
import checkmate.com.checkmate.login.util.JwtHeaderUtil;
import checkmate.com.checkmate.member.domain.Member;
import checkmate.com.checkmate.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static checkmate.com.checkmate.global.codes.ErrorCode.*;


@Service
@RequiredArgsConstructor
@Transactional
public class OAuthService {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    public OAuthMemberResponse signup(final MemberInfoRequest memberInfoRequest) {

        Optional<Member> findMember = memberRepository.findMemberBySocialId(memberInfoRequest.getSocialId());
        if (findMember.isPresent()) throw new GeneralException(USER_EXISTS);


        String refreshToken = jwtProvider.createRefreshToken();

        Member member = Member.builder()
                .memberName(memberInfoRequest.getName())
                .memberEmail(memberInfoRequest.getEmail())
                .socialId(memberInfoRequest.getSocialId())
                .refreshToken(refreshToken)
                .socialRefreshToken(memberInfoRequest.getSocialRefreshToken())
                .build();
        memberRepository.save(member);

        String accessToken = jwtProvider.createAccessToken(member.getMemberId().toString());

        return new OAuthMemberResponse(member, false, accessToken);
    }

    @Transactional(readOnly = true)
    public AccessTokenResponse reissueAccessToken() {
        String accessToken = JwtHeaderUtil.getAccessToken();
        String refreshToken = JwtHeaderUtil.getRefreshToken();
        if (jwtProvider.isValidRefreshAndInvalidAccess(refreshToken, accessToken)) {
            Member member = memberRepository.findMemberByRefreshToken(refreshToken)
                    .orElseThrow(() -> new GeneralException(INVALID_REFRESH_TOKEN));
            return AccessTokenResponse.builder()
                    .accessToken(jwtProvider.createAccessToken(member.getMemberId().toString()))
                    .build();
        }
        if (jwtProvider.isValidRefreshAndValidAccess(refreshToken, accessToken)) {
            return AccessTokenResponse.builder()
                    .accessToken(accessToken)
                    .build();
        }
        throw new GeneralException(FAIL_VALIDATE_TOKEN);
    }

    public void logout(final Long id) {
        final Member member = memberRepository.findById(id)
                .orElseThrow(() -> new GeneralException(INVALID_ACCESS_TOKEN));
        member.updateRefreshToken(null);
    }

    public void signout(final Long id) {
        final Member member = memberRepository.findById(id)
                .orElseThrow(() -> new GeneralException(INVALID_ACCESS_TOKEN));

        memberRepository.deleteMemberByMemberId(member.getMemberId());
    }
}
