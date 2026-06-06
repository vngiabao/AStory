package com.ph.core.story.interfaces;

import com.ph.core.story.common.SecurityUtils;
import com.ph.core.story.application.command.NotificationCommandService;
import com.ph.core.story.application.command.UserPushTokenService;
import com.ph.core.story.application.realtime.NotificationRealtimeService;
import com.ph.core.story.application.command.dto.RegisterPushTokenRequest;
import com.ph.core.story.application.command.dto.UnregisterPushTokenRequest;
import com.ph.core.story.application.query.NotificationQueryService;
import com.ph.core.story.application.query.dto.NotificationResponse;
import com.ph.core.story.application.query.dto.UnreadCountResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;
    private final UserPushTokenService userPushTokenService;
    private final NotificationRealtimeService notificationRealtimeService;

    @PostMapping("/push-tokens")
    public void registerPushToken(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RegisterPushTokenRequest request) {
        userPushTokenService.register(SecurityUtils.getUserIdFromJwt(jwt), request);
    }

    @PostMapping("/push-tokens/unregister")
    public void unregisterPushToken(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UnregisterPushTokenRequest request) {
        userPushTokenService.unregister(SecurityUtils.getUserIdFromJwt(jwt), request);
    }

    @GetMapping
    public Page<NotificationResponse> findMyNotifications(
            @AuthenticationPrincipal Jwt jwt,
            Pageable pageable) {
        return notificationQueryService.findForUser(SecurityUtils.getUserIdFromJwt(jwt), pageable);
    }

    @GetMapping("/unread-count")
    public UnreadCountResponse countUnread(@AuthenticationPrincipal Jwt jwt) {
        return notificationQueryService.countUnread(SecurityUtils.getUserIdFromJwt(jwt));
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> stream(@AuthenticationPrincipal Jwt jwt) {
        SseEmitter emitter = notificationRealtimeService.subscribe(SecurityUtils.getUserIdFromJwt(jwt));

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header("X-Accel-Buffering", "no")
                .body(emitter);
    }

    @PatchMapping("/{id}/read")
    public NotificationResponse markRead(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id) {
        return notificationCommandService.markRead(SecurityUtils.getUserIdFromJwt(jwt), id);
    }

    @PatchMapping("/read-all")
    public void markAllRead(@AuthenticationPrincipal Jwt jwt) {
        notificationCommandService.markAllRead(SecurityUtils.getUserIdFromJwt(jwt));
    }
}
