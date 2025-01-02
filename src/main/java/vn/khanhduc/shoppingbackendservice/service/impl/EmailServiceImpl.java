package vn.khanhduc.shoppingbackendservice.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import vn.khanhduc.shoppingbackendservice.dto.event.EmailEvent;
import vn.khanhduc.shoppingbackendservice.service.EmailService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${spring.sendgrid.from-email}")
    private String from;

    @Value("${spring.sendgrid.templateId}")
    private String templateId;

    @Value("${spring.sendgrid.verificationLink}")
    private String verificationLink;

    @Value("${spring.mail.username}")
    String emailFrom;

    private final SendGrid sendGrid;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    @RabbitListener(queues = "emailQueue")
    public void sendEmailRabbitMQ(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        log.info("Send email RabbitMQ start loading ...");

        Context context = new Context();
        context.setVariable("name", event.getRecipient());

        if (event.getParam() != null) {
            context.setVariables(event.getParam());
        } else {
            log.warn("Event param is null, cannot set variables in email template.");
        }

        String htmlContent = templateEngine.process(event.getTemplateCode(), context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        helper.setFrom(emailFrom, "Shopping");
        helper.setTo(event.getRecipient());
        helper.setSubject(event.getSubject());
        helper.setText(htmlContent, true);

        try {
            mailSender.send(mimeMessage);
            log.info("Email sent to {} successfully!", event.getRecipient());
        } catch (Exception e) {
            log.error("Failed to send email to {}", event.getRecipient(), e);
            throw e;
        }
    }

    @Override
    public void emailVerification(String to, String name, String otp) {
        log.info("Send email verification ...");
        Email fromEmail = new Email(from, "Khanh Duc");
        Email toEmail = new Email(to);
        String subject = "Xác thực tài khoản";
        String secretCode = String.format("?email=%s&secretCode=%s", to, otp);

        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("verification_link", verificationLink + secretCode);

        Mail mail = new Mail();
        mail.setFrom(fromEmail);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(toEmail);

        map.forEach(personalization::addDynamicTemplateData);
        mail.addPersonalization(personalization);
        mail.setTemplateId(templateId);

        try {
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            log.info("Response Status {}",response.getStatusCode());
            if(response.getStatusCode() == 202) {
                log.info("Verification successfully");
            } else {
                log.error("Verification failed");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
