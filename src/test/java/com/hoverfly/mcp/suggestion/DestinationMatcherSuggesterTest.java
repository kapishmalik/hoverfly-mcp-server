package com.hoverfly.mcp.suggestion;

import static org.junit.jupiter.api.Assertions.*;

import com.hoverfly.mcp.suggestion.matcher.DestinationMatcherSuggester;
import com.hoverfly.mcp.suggestion.matcher.MatcherSuggestion;
import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class DestinationMatcherSuggesterTest {
  private final DestinationMatcherSuggester suggester = new DestinationMatcherSuggester();

  private Request buildRequestWithDestination(String... hosts) {
    Request request = new Request();
    request.setDestination(
        Stream.of(hosts).map(RequestFieldMatcher::newExactMatcher).collect(Collectors.toList()));
    return request;
  }

  @Test
  public void shouldSuggestExactMatcherForStaticHost() {
    Request request = buildRequestWithDestination("api.example.com");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    assertFalse(suggestions.isEmpty());
    MatcherSuggestion ms = suggestions.get(0);
    assertEquals("destination", ms.getField());
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "exact".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestGlobMatcherForHostWithDigits() {
    Request request = buildRequestWithDestination("api1.example.com");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "glob".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestRegexMatcherForRegexPatternHost() {
    Request request = buildRequestWithDestination("api[0-9]+.example.com");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "regex".equals(opt.getMatcher())));
  }
}
