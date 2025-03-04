package xyz.demorgan.projectpractice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FlywayConfig {
    @Bean
    public static FlywayMigrationStrategy migrate() {
        return flyway -> {
            log.info("Checking Flyway migrations...");
            var info = flyway.info();
            log.info("Pending migrations: {}", info.pending().length);

            if (info.pending().length > 0) {
                log.info("Starting Flyway migration...");
                flyway.migrate();
                log.info("Flyway migration completed.");
            } else {
                log.info("No pending migrations.");
            }
        };
    }
}
