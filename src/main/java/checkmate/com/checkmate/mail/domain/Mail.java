package checkmate.com.checkmate.mail.domain;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.mail.dto.MailUpdateRequestDto;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Mail {
    @Id
    @GeneratedValue
    private Long mailId;

    @Column
    private String mailTitle;

    @Column
    private String mailContent;

    @Column
    private MailType mailType;

    @Column
    private String attachUrl;

    @Column
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="event_id")
    private Event event;

    public Mail(MailUpdateRequestDto mailUpdateRequestDto, Event event){
        this.mailTitle = mailUpdateRequestDto.getMailTitle();
        this.mailContent = mailUpdateRequestDto.getMailContent();
        this.mailType = mailUpdateRequestDto.getMailType();
        this.attachUrl = mailUpdateRequestDto.getAttachUrl();
        this.imageUrl = event.getEventImage();
        this.event = event;
    }

    public void updateMailContent(MailUpdateRequestDto mailUpdateRequestDto){
        System.out.println("=================="+ mailUpdateRequestDto.getMailTitle());
        this.mailTitle = mailUpdateRequestDto.getMailTitle();
        this.mailContent = mailUpdateRequestDto.getMailContent();
        this.attachUrl = mailUpdateRequestDto.getAttachUrl();
    }
}
