package checkmate.com.checkmate.member.domain;

import checkmate.com.checkmate.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String memberName;

    @Column(nullable = false)
    private String memberEmail;

    @Column(nullable = false)
    private String socialId;

    private String refreshToken;

    private String socialRefreshToken;

    @Builder
    public Member(
            final String memberName,
            final String memberEmail,
            final String socialId,
            final String refreshToken,
            final String socialRefreshToken
    ) {
        this.memberName = memberName;
        this.memberEmail = memberEmail;
        this.socialId = socialId;
        this.refreshToken = refreshToken;
        this.socialRefreshToken = socialRefreshToken;

    }
    public void updateRefreshToken(final String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
