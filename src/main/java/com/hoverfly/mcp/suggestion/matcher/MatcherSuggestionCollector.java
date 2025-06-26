package com.hoverfly.mcp.suggestion.matcher;

import io.specto.hoverfly.junit.core.model.Request;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatcherSuggestionCollector {
  private final List<FieldMatcherSuggester> suggesters;

  @Autowired
  public MatcherSuggestionCollector(List<FieldMatcherSuggester> suggesters) {
    this.suggesters = suggesters;
  }

  public List<MatcherSuggestion> collectSuggestions(Request request) {
    List<MatcherSuggestion> suggestions = new ArrayList<>();
    if (request == null) return suggestions;
    for (FieldMatcherSuggester suggester : suggesters) {
      suggestions.addAll(suggester.suggest(request));
    }
    return suggestions;
  }
}
