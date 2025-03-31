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
import xyz.demorgan.projectpractice.store.dto.StudentProjectCreationEvent;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private static final String APPROVAL_TEMPLATE = "approval-email";
    private static final String PASSWORD_UPDATE_TEMPLATE = "password-update-email";
    private static final String STUDENT_PROJECT_ID_TEMPLATE = "project-id-email";
    private static final String ENCODING = "UTF-8";

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${smtp.email}")
    private String from;

    @KafkaListener(topics = "company-password-email", groupId = "company-password-email")
    public void sendCompanyPassword(PasswordEvent event, Acknowledgment acknowledgment) {
        if (!isValidEvent(event)) {
            log.error("Invalid PasswordEvent received: {}", event);
            return;
        }

        try {
            MimeMessage message = createPasswordEmailMessage(event);
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

    @KafkaListener(topics = "student-project-id", groupId = "student-project-id")
    public void sendStudentProject(StudentProjectCreationEvent event, Acknowledgment acknowledgment) {
        if (event == null) {
            log.error("Invalid StudentProjectCreationEvent received: {}", event);
            return;
        }

        try {
            MimeMessage message = createIdEmailMessage(event);
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

    private MimeMessage createIdEmailMessage(StudentProjectCreationEvent event)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, ENCODING);

        helper.setFrom(from);
        helper.setTo(event.getEmail());
        helper.setSubject(MimeUtility.encodeText("ID вашего проекта", ENCODING, "B"));

        Context context = new Context();
        context.setVariable("projectId", event.getProjectId());

        String htmlContent = templateEngine.process(STUDENT_PROJECT_ID_TEMPLATE, context);

        helper.setText(htmlContent, true);
        return message;
    }

    private MimeMessage createPasswordEmailMessage(PasswordEvent event)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, ENCODING);

        helper.setFrom(from);
        helper.setTo(event.getEmail());
        helper.setSubject(MimeUtility.encodeText("Ваш аккаунт для проектной практики ПГНИУ", ENCODING, "B"));

        Context context = new Context();
        context.setVariable("password", event.getPassword());

        String template = event.isFirstApprove() ? APPROVAL_TEMPLATE : PASSWORD_UPDATE_TEMPLATE;
        String htmlContent = templateEngine.process(template, context);

        helper.setText(htmlContent, true);
        return message;
    }
}