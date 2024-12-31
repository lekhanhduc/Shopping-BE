package vn.khanhduc.shoppingbackendservice.service.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.khanhduc.shoppingbackendservice.service.EmailService;
import java.io.IOException;
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

    private final SendGrid sendGrid;

    @Override
    public void send(String to, String subject, String body) {
        log.info("Send email start loading ...");
        Email fromEmail = new Email(from, "Khanh Duc");
        Email toEmail = new Email(to);

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response =sendGrid.api(request);
            if(response.getStatusCode() == 202)
                log.info("Send email finish loading ...");
             else
                log.error("Email sent failed");
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
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
