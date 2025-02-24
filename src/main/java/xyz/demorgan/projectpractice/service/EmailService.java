package xyz.demorgan.projectpractice.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendEmail(String email, String subject, String text) {
        // send email
    }
    public void sendEmail(String password) { // ЯДЕРНЫЙ ТЕСТОВЫЙ КОСТЫЛЬ СНЕСТИ ОТ ГРЕХА ПОДАЛЬШЕ
        System.out.println("Your account has been approved Your password is: " + password); // ЯДЕРНЫЙ ТЕСТОВЫЙ КОСТЫЛЬ СНЕСТИ ОТ ГРЕХА ПОДАЛЬШЕ
    }
}
