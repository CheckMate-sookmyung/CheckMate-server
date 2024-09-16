package checkmate.com.checkmate.mail.component;

import checkmate.com.checkmate.event.domain.Event;
import checkmate.com.checkmate.mail.domain.Mail;
import jakarta.mail.MessagingException;
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

    public void sendAttendanceListFile(Event event, List<MultipartFile> files) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(event.getManagerEmail());
            helper.setSubject("체크메이트 : [" + event.getEventTitle() + "] 참석자 명단");
            helper.setText("안녕하세요. 체크메이트에서 보내드리는 " + event.getEventTitle() + " 참석자 명단 이메일입니다. \n감사합니다.");
            for (MultipartFile file : files) {
                helper.addAttachment(file.getOriginalFilename(), new ByteArrayResource(file.getBytes()));
            }
            emailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEventMail(Mail mail, List<String> receivers, String imageUrl, String buttonUrl) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setBcc(receivers.toArray(new String[0]));
            helper.setSubject(mail.getMailTitle());
            String htmlContent = "<html><body>" +
                    "<p>" + mail.getMailContent() + "</p>" +
                    "<br>" +
                    "<a href='" + buttonUrl + "' style='display:inline-block; padding:10px 20px; font-size:16px; color:white; background-color:#007BFF; text-decoration:none; border-radius:5px;'>" + "WISE 바로가기" + "</a>" +
                    "<br><br>" +
                    "<img src='" + imageUrl + "' alt='Image' style='max-width:100%; height:auto;' />" +
                    "</body></html>";

            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
