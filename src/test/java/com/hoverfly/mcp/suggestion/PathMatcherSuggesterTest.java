package com.hoverfly.mcp.suggestion;

import static org.junit.jupiter.api.Assertions.*;

import com.hoverfly.mcp.suggestion.matcher.MatcherSuggestion;
import com.hoverfly.mcp.suggestion.matcher.PathMatcherSuggester;
import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

public class PathMatcherSuggesterTest {
  private final PathMatcherSuggester suggester = new PathMatcherSuggester();

  private Request buildRequestWithPath(String... paths) {
    Request request = new Request();
    request.setPath(Arrays.stream(paths).map(RequestFieldMatcher::newExactMatcher).toList());
    return request;
  }

  @Test
  public void shouldSuggestExactMatcherForStaticPath() {
    Request request = buildRequestWithPath("/api/v1/resource");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    assertFalse(suggestions.isEmpty());
    MatcherSuggestion ms = suggestions.get(0);
    assertEquals("path", ms.getField());
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "exact".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestGlobMatcherForPathWithDigits() {
    Request request = buildRequestWithPath("/api/v1/resource/123");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "glob".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestGlobMatcherForPathWithWildcard() {
    Request request = buildRequestWithPath("/api/*/resource");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "glob".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestRegexMatcherForRegexPatternPath() {
    Request request = buildRequestWithPath("/api/v1/resource/[0-9]+$");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "regex".equals(opt.getMatcher())));
  }
}
