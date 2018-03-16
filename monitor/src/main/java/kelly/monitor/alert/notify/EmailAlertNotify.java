package kelly.monitor.alert.notify;

import kelly.monitor.common.AlertInfo;
import kelly.monitor.common.Owner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

/**
 * Created by kelly-lee on 2018/2/13.
 */
@Slf4j
@Component
public class EmailAlertNotify implements AlertNotify {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void notify(AlertInfo alertInfo) {
        alertInfo.getOwners().stream().forEach(owner -> {
            MimeMessage message = buildMimeMessage(alertInfo, owner);
            if (message != null) {
                this.mailSender.send(message);
                log.info("send email : {}", alertInfo.toEmail());
            }
        });
    }

    private MimeMessage buildMimeMessage(AlertInfo alertInfo, Owner owner) {
        try {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom(fromEmail);
            message.setTo(owner.getEmail());
            message.setSubject(alertInfo.toSms());
            message.setText(alertInfo.toEmail());
            return mimeMessage;
        } catch (Exception e) {
            log.error("build mime message fail {}", e.getMessage());
            return null;
        }
    }
}
