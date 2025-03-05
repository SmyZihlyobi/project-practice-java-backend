package xyz.demorgan.projectpractice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import xyz.demorgan.projectpractice.store.dto.PasswordEvent;

import java.io.UnsupportedEncodingException;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private static final String EMAIL_TEMPLATE = "password-email";
    private static final String ENCODING = "UTF-8";

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${smtp.email}")
    private String from;

    @KafkaListener(topics = "company-password-email", groupId = "company-password-email")
    public void sendEmail(PasswordEvent event, Acknowledgment acknowledgment) {
        if (!isValidEvent(event)) {
            log.error("Invalid PasswordEvent received: {}", event);
            return;
        }

        try {
            MimeMessage message = createEmailMessage(event);
            mailSender.send(message);
            acknowledgment.acknowledge();
            log.info("Email successfully sent to: {}", event.getEmail());
        } catch (MessagingException e) {
            log.error("Email sending failed to {}: {}", event.getEmail(), e.getMessage());
        } catch (TemplateInputException e) {
            log.error("Template processing error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
        }
    }

    private boolean isValidEvent(PasswordEvent event) {
        return event != null
                && event.getEmail() != null
                && event.getPassword() != null
                && event.getEmail().contains("@");
    }

    private MimeMessage createEmailMessage(PasswordEvent event)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, ENCODING);

        helper.setFrom(from);
        helper.setTo(event.getEmail());
        helper.setSubject(MimeUtility.encodeText("Ваш аккаунт для проектной практики ПГНИУ был одобрен", ENCODING, "B"));

        Context context = new Context();
        context.setVariable("password", event.getPassword());
        String htmlContent = templateEngine.process(EMAIL_TEMPLATE, context);

        helper.setText(htmlContent, true);
        return message;
    }
}