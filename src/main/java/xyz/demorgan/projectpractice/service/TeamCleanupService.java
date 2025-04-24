package xyz.demorgan.projectpractice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamCleanupService {

    private final TeamService teamService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpEmptyTeams() {
        log.info("Running scheduled task to clean up empty teams");
        teamService.deleteEmptyTeams();
    }
}
