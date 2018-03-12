package kelly.monitor.alert.notify;

import kelly.monitor.common.AlertInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    JavaMailSender mailSender;

    @Override
    public void notify(AlertInfo alertInfo) {
        try {
            final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setFrom("admin@monitor.klplay.cn");
            message.setTo("kellyleemz285@163.com");
            message.setSubject(alertInfo.toSms());
            message.setText(alertInfo.toEmail());
            this.mailSender.send(mimeMessage);
            log.info("send email : {}", alertInfo.toEmail());
        } catch (Exception ex) {
            log.error("send email fail : {}", ex.getMessage());
        }
    }
}
