package com.hoverfly.mcp.suggestion;

import static org.junit.jupiter.api.Assertions.*;

import com.hoverfly.mcp.suggestion.matcher.MatcherSuggestion;
import com.hoverfly.mcp.suggestion.matcher.MethodMatcherSuggester;
import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class MethodMatcherSuggesterTest {
  private final MethodMatcherSuggester suggester = new MethodMatcherSuggester();

  private Request buildRequestWithMethod(String... methods) {
    Request request = new Request();
    request.setMethod(
        Stream.of(methods).map(RequestFieldMatcher::newExactMatcher).collect(Collectors.toList()));
    return request;
  }

  @Test
  public void shouldSuggestExactMatcherForGetMethod() {
    Request request = buildRequestWithMethod("GET");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    assertFalse(suggestions.isEmpty());
    MatcherSuggestion ms = suggestions.get(0);
    assertEquals("method", ms.getField());
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "exact".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestExactMatcherForPostMethod() {
    Request request = buildRequestWithMethod("POST");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "exact".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestExactMatcherForCustomMethod() {
    Request request = buildRequestWithMethod("CUSTOM");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "exact".equals(opt.getMatcher())));
  }
}
