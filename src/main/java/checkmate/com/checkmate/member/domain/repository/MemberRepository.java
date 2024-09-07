package checkmate.com.checkmate.member.domain.repository;


import checkmate.com.checkmate.global.codes.ErrorCode;
import checkmate.com.checkmate.global.exception.GeneralException;
import checkmate.com.checkmate.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberBySocialId(String socialId);
    Optional<Member> findMemberByRefreshToken(String refreshToken);

    /*
    @Modifying
    @Query("update Member member set member.status = 'DELETED' where member.id = :memberId")
    void deleteMemberByMemberId(@Param("memberId") final Long memberId);
*/

    default Member findMemberByMemberId(
            final Long id
    ) {
        return findById(id).orElseThrow(() -> new GeneralException(ErrorCode.USER_NOT_FOUND));
    }
}
