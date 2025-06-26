package com.hoverfly.mcp.suggestion;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoverfly.mcp.suggestion.matcher.BodyMatcherSuggester;
import com.hoverfly.mcp.suggestion.matcher.MatcherSuggestion;
import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.List;
import org.junit.jupiter.api.Test;

public class BodyMatcherSuggesterTest {

  private final BodyMatcherSuggester suggester = new BodyMatcherSuggester(new ObjectMapper());

  @Test
  void shouldSuggestExactMatcherForSimpleString() {
    Request request = new Request();
    request.setBody(List.of(RequestFieldMatcher.newExactMatcher("hello world")));

    List<MatcherSuggestion> suggestions = suggester.suggest(request);

    assertFalse(suggestions.isEmpty());
    assertTrue(
        suggestions.stream()
            .anyMatch(
                s ->
                    s.getField().equals("body")
                        && s.getOptions().stream()
                            .anyMatch(opt -> opt.getMatcher().equals("exact"))));
  }

  @Test
  void shouldSuggestJsonMatcherForValidJson() {
    Request request = new Request();
    request.setBody(List.of(RequestFieldMatcher.newExactMatcher("{\"name\":\"John\",\"age\":30}")));

    List<MatcherSuggestion> suggestions = suggester.suggest(request);

    assertFalse(suggestions.isEmpty());
    assertTrue(
        suggestions.stream()
            .anyMatch(
                s ->
                    s.getField().equals("body")
                        && s.getOptions().stream()
                            .anyMatch(opt -> opt.getMatcher().equals("json"))));
  }

  @Test
  void shouldSuggestXmlMatcherForValidXml() {
    Request request = new Request();
    request.setBody(List.of(RequestFieldMatcher.newExactMatcher("<root><name>John</name></root>")));

    List<MatcherSuggestion> suggestions = suggester.suggest(request);

    assertFalse(suggestions.isEmpty());
    assertTrue(
        suggestions.stream()
            .anyMatch(
                s ->
                    s.getField().equals("body")
                        && s.getOptions().stream()
                            .anyMatch(opt -> opt.getMatcher().equals("xml"))));
  }

  @Test
  void shouldSuggestRegexMatcherForPatternLikeString() {
    Request request = new Request();
    request.setBody(List.of(RequestFieldMatcher.newExactMatcher("user-\\d+")));

    List<MatcherSuggestion> suggestions = suggester.suggest(request);

    assertFalse(suggestions.isEmpty());
    assertTrue(
        suggestions.stream()
            .anyMatch(
                s ->
                    s.getField().equals("body")
                        && s.getOptions().stream()
                            .anyMatch(opt -> opt.getMatcher().equals("regex"))));
  }

  @Test
  void shouldSuggestXmlPartialMatcherForValidXml() {
    Request request = new Request();
    request.setBody(List.of(RequestFieldMatcher.newExactMatcher("<root><name>John</name></root>")));

    List<MatcherSuggestion> suggestions = suggester.suggest(request);

    assertFalse(suggestions.isEmpty());
    assertTrue(
        suggestions.stream()
            .anyMatch(
                s ->
                    s.getField().equals("body")
                        && s.getOptions().stream()
                            .anyMatch(opt -> opt.getMatcher().equals("xmlpartial"))));
  }
}
