package com.hoverfly.mcp.suggestion;

import static org.junit.jupiter.api.Assertions.*;

import com.hoverfly.mcp.suggestion.matcher.HeadersMatcherSuggester;
import com.hoverfly.mcp.suggestion.matcher.MatcherSuggestion;
import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.*;
import org.junit.jupiter.api.Test;

public class HeadersMatcherSuggesterTest {
  private final HeadersMatcherSuggester suggester = new HeadersMatcherSuggester();

  private Request buildRequestWithHeader(String header, String... values) {
    Request request = new Request();
    Map<String, List<RequestFieldMatcher>> headers = new HashMap<>();
    headers.put(
        header,
        Arrays.stream(values)
            .map(RequestFieldMatcher::newExactMatcher)
            .map(m -> (RequestFieldMatcher) m)
            .toList());
    request.setHeaders(headers);
    return request;
  }

  @Test
  public void shouldSuggestExactMatcherForSingleHeader() {
    Request request = buildRequestWithHeader("Content-Type", "application/json");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    assertFalse(suggestions.isEmpty());
    MatcherSuggestion ms = suggestions.get(0);
    assertEquals("header:Content-Type", ms.getField());
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "exact".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestArrayMatcherForMultipleHeaderValues() {
    Request request = buildRequestWithHeader("X-Header", "one", "two");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "array".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestGlobAndJwtMatcherForAuthorizationBearer() {
    Request request = buildRequestWithHeader("Authorization", "Bearer abc.def.ghi");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "glob".equals(opt.getMatcher())));
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "jwt".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestGlobMatcherForHeaderWithSemicolon() {
    Request request = buildRequestWithHeader("Cookie", "foo=bar;baz=qux");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "glob".equals(opt.getMatcher())));
  }
}
