package com.ph.core.story.infrastructure.push;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpoPushClient {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    public ExpoPushResult send(ExpoPushMessage message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

        Map<String, Object> payload = new HashMap<>();
        payload.put("to", message.getTo());
        payload.put("title", message.getTitle());
        payload.put("body", message.getBody());
        payload.put("sound", "default");
        payload.put("priority", "high");
        payload.put("channelId", "story-updates");
        payload.put("categoryId", "STORY_SHARE");
        payload.put("data", message.getData());

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    EXPO_PUSH_URL,
                    new HttpEntity<>(payload, headers),
                    String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("data");
            String status = data.path("status").asText();
            String ticketId = data.path("id").asText(null);
            String error = data.path("message").asText(null);
            String errorCode = data.path("details").path("error").asText(null);

            boolean ok = response.getStatusCode().is2xxSuccessful() && "ok".equalsIgnoreCase(status);
            return ExpoPushResult.builder()
                    .success(ok)
                    .ticketId(ticketId)
                    .error(error)
                    .errorCode(errorCode)
                    .sentAt(Instant.now())
                    .build();
        } catch (RestClientException ex) {
            log.warn("Expo push request failed", ex);
            return ExpoPushResult.builder()
                    .success(false)
                    .error(ex.getMessage())
                    .sentAt(Instant.now())
                    .build();
        } catch (Exception ex) {
            log.warn("Expo push response parsing failed", ex);
            return ExpoPushResult.builder()
                    .success(false)
                    .error(ex.getMessage())
                    .sentAt(Instant.now())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ExpoPushMessage {
        private String to;
        private String title;
        private String body;
        private Map<String, Object> data;
    }

    @Getter
    @Builder
    public static class ExpoPushResult {
        private boolean success;
        private String ticketId;
        private String error;
        private String errorCode;
        private Instant sentAt;
    }
}
