package vn.khanhduc.shoppingbackendservice.service;

import jakarta.mail.MessagingException;
import vn.khanhduc.shoppingbackendservice.dto.event.EmailEvent;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendEmailRabbitMQ(EmailEvent event) throws MessagingException, UnsupportedEncodingException;
    void emailVerification(String to, String name, String otp);
}
