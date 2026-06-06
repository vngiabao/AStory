package com.ph.core.story.application.query.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryDetailResponse {

  private Long id;
  private String title;
  private String content;
  private StoriesResponse story;
  private List<MediaFilesResponse> medias;

}