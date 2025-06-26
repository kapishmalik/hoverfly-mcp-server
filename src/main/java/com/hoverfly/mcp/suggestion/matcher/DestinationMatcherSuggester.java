package com.hoverfly.mcp.suggestion.matcher;

import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DestinationMatcherSuggester implements FieldMatcherSuggester {
  @Override
  public List<MatcherSuggestion> suggest(Request request) {
    List<MatcherSuggestion> suggestions = new ArrayList<>();
    if (request == null || request.getDestination() == null) return suggestions;
    List<RequestFieldMatcher<String>> destMatchers = request.getDestination();
    for (int i = 0; i < destMatchers.size(); i++) {
      String value = destMatchers.get(i) != null ? destMatchers.get(i).getValue() : "";
      List<MatcherOption> options = new ArrayList<>();
      /**
       * Suggest an 'exact' matcher for the destination field. Reason: Use when you want to match a
       * specific host or domain exactly (case-sensitive). This is the most precise matcher and is
       * suitable for static hostnames.
       */
      options.add(
          new MatcherOption(
              "exact", value, "Use for exact host match", DOCS_BASE + "#exact-matcher"));
      /**
       * Suggest a 'glob' matcher if the value contains a dot ('.'), indicating a domain/hostname.
       * Reason: Hostnames often contain variable numeric segments (e.g., api1.example.com,
       * api2.example.com). This replaces all digit sequences with '*' to allow wildcard matching
       * for those segments. Useful for matching a range of similar hostnames with dynamic parts.
       */
      if (value.contains(".")) {
        options.add(
            new MatcherOption(
                "glob",
                value.replaceAll("\\d+", "*"),
                "Use for wildcard host match",
                DOCS_BASE + "#glob-matcher"));
      }
      /**
       * Suggest a 'regex' matcher for advanced host/domain matching. Reason: Use when you need to
       * match complex patterns, multiple domains, or use regular expressions for flexibility. This
       * is the most flexible but also the most complex matcher type.
       */
      options.add(
          new MatcherOption(
              "regex", ".*", "Use for advanced host pattern", DOCS_BASE + "#regex-matcher"));
      String fieldName = destMatchers.size() > 1 ? "destination[" + i + "]" : "destination";
      suggestions.add(new MatcherSuggestion(fieldName, options));
    }
    return suggestions;
  }
}
