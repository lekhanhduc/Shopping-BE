package vn.khanhduc.shoppingbackendservice.service;

public interface EmailService {
    void send(String to, String subject, String body);
    void emailVerification(String to, String name, String otp);
}
