package com.hoverfly.mcp.suggestion;

import static org.junit.jupiter.api.Assertions.*;

import com.hoverfly.mcp.suggestion.matcher.MatcherSuggestion;
import com.hoverfly.mcp.suggestion.matcher.QueryMatcherSuggester;
import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.*;
import org.junit.jupiter.api.Test;

public class QueryMatcherSuggesterTest {
  private final QueryMatcherSuggester suggester = new QueryMatcherSuggester();

  private Request buildRequestWithQuery(String param, String... values) {
    Request request = new Request();
    Map<String, List<RequestFieldMatcher>> query = new HashMap<>();
    query.put(
        param,
        Arrays.stream(values)
            .map(RequestFieldMatcher::newExactMatcher)
            .map(m -> (RequestFieldMatcher) m)
            .toList());
    request.setQuery(query);
    return request;
  }

  @Test
  public void shouldSuggestExactMatcherForSingleQueryParam() {
    Request request = buildRequestWithQuery("foo", "bar");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    assertFalse(suggestions.isEmpty());
    MatcherSuggestion ms = suggestions.get(0);
    assertEquals("query:foo", ms.getField());
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "exact".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestArrayMatcherForMultipleQueryValues() {
    Request request = buildRequestWithQuery("foo", "bar", "baz");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "array".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestGlobMatcherForQueryValueWithWildcard() {
    Request request = buildRequestWithQuery("foo", "ba*");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "glob".equals(opt.getMatcher())));
  }

  @Test
  public void shouldSuggestRegexMatcherForQueryValueWithRegexPattern() {
    Request request = buildRequestWithQuery("foo", "[a-z]+$");
    List<MatcherSuggestion> suggestions = suggester.suggest(request);
    MatcherSuggestion ms = suggestions.get(0);
    assertTrue(ms.getOptions().stream().anyMatch(opt -> "regex".equals(opt.getMatcher())));
  }
}
