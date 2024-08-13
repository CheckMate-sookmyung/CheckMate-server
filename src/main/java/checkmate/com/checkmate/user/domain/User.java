package checkmate.com.checkmate.user.domain;

import checkmate.com.checkmate.event.domain.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class User {
    @Id @GeneratedValue
    private Long userId;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String socialRefreshToken;

    @OneToMany(mappedBy = "user")
    private List<Event> events = new ArrayList<>();

    @Builder
    public User(final String userName,
                final String userEmail,
                final String socialId,
                final String refreshToken,
                final String socialRefreshToken){
        this.userName = userName;
        this.userEmail = userEmail;
        this.socialId = socialId;
        this.refreshToken = refreshToken;
        this.socialRefreshToken = socialRefreshToken;
    }

    public void updateUser(String userName){
        this.userName = userName;
    }

    public void updateRefreshToken(final String refreshToken){
        this.refreshToken = refreshToken;
    }
}
