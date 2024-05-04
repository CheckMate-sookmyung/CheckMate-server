package checkmate.com.checkmate.event.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Event {

    @Id @GeneratedValue
    private Long eventId;

    private String eventTitle;

    private String eventDetail;

    private String eventImage;

    private Boolean alarm;
    
}
