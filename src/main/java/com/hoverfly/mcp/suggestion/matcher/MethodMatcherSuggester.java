package com.hoverfly.mcp.suggestion.matcher;

import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MethodMatcherSuggester implements FieldMatcherSuggester {
  @Override
  public List<MatcherSuggestion> suggest(Request request) {
    List<MatcherSuggestion> suggestions = new ArrayList<>();
    if (request == null || request.getMethod() == null) return suggestions;
    List<RequestFieldMatcher<String>> methodMatchers = request.getMethod();
    for (int i = 0; i < methodMatchers.size(); i++) {
      String value = methodMatchers.get(i) != null ? methodMatchers.get(i).getValue() : "";
      List<MatcherOption> options = new ArrayList<>();
      /**
       * Suggest an 'exact' matcher for the method field. Reason: HTTP methods (GET, POST, etc.) are
       * always matched exactly and are case-sensitive. No other matcher types are typically needed
       * for this field.
       */
      options.add(
          new MatcherOption(
              "exact",
              value,
              "Use for exact HTTP method match",
              "https://docs.hoverfly.io/en/latest/pages/keyconcepts/matching/matching.html#exact-matcher"));
      String fieldName = methodMatchers.size() > 1 ? "method[" + i + "]" : "method";
      suggestions.add(new MatcherSuggestion(fieldName, options));
    }
    return suggestions;
  }
}
