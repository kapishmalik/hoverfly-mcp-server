package com.hoverfly.mcp.suggestion.matcher;

import java.util.List;

public class MatcherSuggestion {
  private String field;
  private List<MatcherOption> options;

  public MatcherSuggestion() {}

  public MatcherSuggestion(String field, List<MatcherOption> options) {
    this.field = field;
    this.options = options;
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public List<MatcherOption> getOptions() {
    return options;
  }

  public void setOptions(List<MatcherOption> options) {
    this.options = options;
  }
}
