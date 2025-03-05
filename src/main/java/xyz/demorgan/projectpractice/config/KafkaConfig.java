package xyz.demorgan.projectpractice.config;

import lombok.Getter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KafkaConfig {
    public final static String COMPANY_TOPIC = "company-password-email";

    @Bean
    public NewTopic companyTopic() {
        return new NewTopic(COMPANY_TOPIC, 1, (short) 1);
    }
}
