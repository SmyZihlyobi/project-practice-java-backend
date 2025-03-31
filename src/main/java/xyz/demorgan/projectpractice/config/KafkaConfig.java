package xyz.demorgan.projectpractice.config;

import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KafkaConfig {
    public final static String COMPANY_TOPIC = "company-password-email";
    public final static String PROJECT_ID_TOPIC = "student-project-id";

    @Bean
    public NewTopic companyTopic() {
        return new NewTopic(COMPANY_TOPIC, 1, (short) 1);
    }

    @Bean
    public NewTopic projectTopic() {
        return new NewTopic(PROJECT_ID_TOPIC, 1, (short) 1);
    }
}
