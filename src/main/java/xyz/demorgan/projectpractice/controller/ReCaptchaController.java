package xyz.demorgan.projectpractice.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class ReCaptchaController {

    @Value("${recaptcha.secret.key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/verify-recaptcha")
    public ResponseEntity<?> verifyReCaptcha(@RequestBody Map<String, String> request) {
        String token = request.get("token");

        if (token == null) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                    .body(Map.of("message", "Token not found"));
        }

        String url = String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s", secretKey, token);

        try {
            ReCaptchaResponse response = restTemplate.postForObject(url, null, ReCaptchaResponse.class);

            if (response != null && response.isSuccess()) {
                return ResponseEntity.ok(Map.of("message", "Success"));
            } else {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                        .body(Map.of("message", "Failed to verify"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal Server Error"));
        }
    }

    @Setter
    @Getter
    private static class ReCaptchaResponse {
        private boolean success;
    }
}
