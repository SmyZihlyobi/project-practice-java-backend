package xyz.demorgan.projectpractice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import xyz.demorgan.projectpractice.store.dto.ValidationResponse;

import java.util.List;

@Slf4j
@Service
public class onlinePsuAuthService {
    static final RestTemplate restTemplate = new RestTemplate();

    public static boolean validateUser(String login, String password) {
        String url = "https://test.psu.ru/modules/authorization.php";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action", "enter");
        formData.add("login", login);
        formData.add("pass", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        try {
            ResponseEntity<ValidationResponse> response = restTemplate.postForEntity(
                    url,
                    new HttpEntity<>(formData, headers),
                    ValidationResponse.class
            );

            log.info("Validation response: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ValidationResponse body = response.getBody();
                return body.getCode() == -1 && "".equals(body.getMsg());
            }
        } catch (Exception e) {
            log.error("Error during external service validation: {}", e.getMessage());
        }

        return false;
    }
}
