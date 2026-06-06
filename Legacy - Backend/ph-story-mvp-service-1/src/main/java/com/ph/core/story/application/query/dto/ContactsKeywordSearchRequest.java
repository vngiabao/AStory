package com.ph.core.story.application.query.dto;

import com.ph.core.lib.query.model.PageRequest;
import com.ph.core.lib.query.model.SortRequest;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ContactsKeywordSearchRequest {
  private String keyword;
  private Long categoryId;
  private PageRequest pagination;
  private List<SortRequest> sorts;
}