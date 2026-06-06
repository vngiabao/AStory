# Notification APIs

## Realtime website

### `GET /api/v1/notifications/stream`

- Auth: `Authorization: Bearer <access_token>`
- Response: `text/event-stream`
- Purpose: website subscribes while the page is open to receive realtime notification events.
- Notes:
  - Browser `EventSource` does not support custom `Authorization` headers.
  - For website JWT auth, use a fetch-based SSE client such as `@microsoft/fetch-event-source`.

Possible SSE events:

- `connected`
- `notification.created`
- `notification.read`
- `notification.read-all`
- `heartbeat`

Sample event payload:

```json
{
  "type": "notification.created",
  "notification": {
    "id": 123,
    "type": "STORY_SHARED",
    "title": "Alice da chia se story voi ban",
    "body": "Ky niem cuoi tuan",
    "storyId": 45,
    "storyShareId": 67,
    "actorUserId": 12,
    "read": false,
    "readAt": null,
    "openedAt": null,
    "createdDate": "2026-05-11T03:15:30Z",
    "modifiedDate": "2026-05-11T03:15:30Z"
  },
  "unreadCount": 4,
  "timestamp": "2026-05-11T03:15:30Z"
}
```

## Shared notification APIs for both mobile and website

### `GET /api/v1/notifications`

- Auth required
- Purpose: get notification inbox with pagination

### `GET /api/v1/notifications/unread-count`

- Auth required
- Purpose: get current unread counter

### `PATCH /api/v1/notifications/{id}/read`

- Auth required
- Purpose: mark one notification as read/opened

### `PATCH /api/v1/notifications/read-all`

- Auth required
- Purpose: mark all notifications as read

## Mobile-only push APIs

### `POST /api/v1/notifications/push-tokens`

- Auth required
- Purpose: register Expo push token for the current device

Request body:

```json
{
  "expoPushToken": "ExponentPushToken[xxxx]",
  "deviceId": "device-001",
  "platform": "android",
  "appVersion": "1.0.0"
}
```

### `POST /api/v1/notifications/push-tokens/unregister`

- Auth required
- Purpose: disable a device push token when logout/uninstall

Request body:

```json
{
  "expoPushToken": "ExponentPushToken[xxxx]"
}
```

## API that triggers a notification

### `POST /api/v1/story-shares`

- Auth required
- Purpose: share a story to another user
- Effect:
  - creates a notification record
  - sends mobile push if recipient has Expo token
  - pushes realtime SSE event to website subscribers
