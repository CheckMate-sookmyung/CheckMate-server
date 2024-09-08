package checkmate.com.checkmate.global.component;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.user.domain.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class EmailSender {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmailWithFile(Event event, List<MultipartFile> files) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(event.getManagerEmail());
            helper.setSubject("체크메이트 : ["+event.getEventTitle() + "] 참석자 명단");
            helper.setText("안녕하세요. 체크메이트에서 보내드리는 " + event.getEventTitle() + " 참석자 명단 이메일입니다. \n감사합니다.");
            for (MultipartFile file : files) {
                helper.addAttachment(file.getOriginalFilename(), new ByteArrayResource(file.getBytes()));
            }
            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
