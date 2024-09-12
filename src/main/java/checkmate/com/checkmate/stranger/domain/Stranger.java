package checkmate.com.checkmate.stranger.domain;


import checkmate.com.checkmate.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Stranger {
    @Id
    @GeneratedValue
    private Long strangerId;

    @Column(nullable = false)
    private String strangerName;

    @Column(nullable = false)
    private String strangerPhoneNumber;

    @Column
    private String strangerEmail;

    @Column
    private String strangerAffiliation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Stranger(final String strangerName,
                         final String strangerPhoneNumber,
                         final String strangerEmail,
                         final String strangerAffiliation){
        this.strangerName = strangerName;
        this.strangerPhoneNumber = strangerPhoneNumber;
        this.strangerEmail = strangerEmail;
        this.strangerAffiliation = strangerAffiliation;
    }
}
