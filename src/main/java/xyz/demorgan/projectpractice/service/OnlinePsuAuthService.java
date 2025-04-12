package xyz.demorgan.projectpractice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OnlinePsuAuthService {
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
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    new HttpEntity<>(formData, headers),
                    String.class
            );

            log.info("Validation response: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();

                var responseMap = objectMapper.readValue(response.getBody(), Map.class);

                int code = (int) responseMap.get("code");
                String msg = (String) responseMap.get("msg");

                return code == -1 && "".equals(msg);
            }
        } catch (Exception e) {
            log.error("Error during external service validation: {}", e.getMessage());
        }

        return false;
    }
}
