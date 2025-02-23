package xyz.demorgan.projectpractice.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return Flyway.configure()
                .dataSource("jdbc:postgresql://postgres:5432/project-practice-api", "postgres", "postgres_secret")
                .locations("classpath:db/migration")
                .load();
    }
}
