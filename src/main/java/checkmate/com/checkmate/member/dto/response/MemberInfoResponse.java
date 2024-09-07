package checkmate.com.checkmate.member.dto.response;

import checkmate.com.checkmate.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor(access = PRIVATE)
public class MemberInfoResponse {

    private final String name;
    private final String email;
    private final String socialId;


    public static MemberInfoResponse of(final Member member) {
        return new MemberInfoResponse(member.getMemberName(), member.getMemberEmail(), member.getSocialId());
    }
}
