package com.ph.core.story.application.query.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class StorySharedItemResponse {

  public StorySharedItemResponse(
      Long id,
      Long storyId,
      Long sharedUserId,
      String title,
      String content,
      Instant storyCreatedDate,
      Long senderId,
      String senderUsername,
      String senderFullname,
      Instant sharedDate) {
    this.id = id;
    this.storyId = storyId;
    this.sharedUserId = sharedUserId;
    this.title = title;
    this.content = content;
    this.storyCreatedDate = storyCreatedDate;
    this.senderId = senderId;
    this.senderUsername = senderUsername;
    this.senderFullname = senderFullname;
    this.sharedDate = sharedDate;
  }

  private Long id;

  // Story
  private Long storyId;
  private String title;
  private String content;
  private Instant storyCreatedDate;

  // Sender
  private Long senderId;
  private String senderUsername;
  private String senderFullname;

  // ===== FUTURE: avatar =====
  // private Long avatarMediaId;
  // private String avatarUrl;

  // Share info
  private Instant sharedDate;
  private Long sharedUserId;
}