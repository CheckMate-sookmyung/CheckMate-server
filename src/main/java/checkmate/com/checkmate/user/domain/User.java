package checkmate.com.checkmate.user.domain;

import checkmate.com.checkmate.event.domain.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
    private String userName;
    private String userMail;

/*    @OneToMany(mappedBy = "user")
    private List<Event> events = new ArrayList<>();*/
}
