package com.hoverfly.mcp.suggestion.matcher;

import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class QueryMatcherSuggester implements FieldMatcherSuggester {
  @Override
  public List<MatcherSuggestion> suggest(Request request) {
    List<MatcherSuggestion> suggestions = new ArrayList<>();
    if (request == null || request.getQuery() == null) return suggestions;
    for (Map.Entry<String, List<RequestFieldMatcher>> entry : request.getQuery().entrySet()) {
      String param = entry.getKey();
      List<RequestFieldMatcher> matchers = entry.getValue();
      if (matchers.size() > 1) {
        // If multiple values, suggest only the array matcher
        StringBuilder allValues = new StringBuilder();
        for (int i = 0; i < matchers.size(); i++) {
          if (i > 0) allValues.append(",");
          allValues.append(matchers.get(i) != null ? matchers.get(i).getValue() : "");
        }
        List<MatcherOption> options = new ArrayList<>();
        options.add(
            new MatcherOption(
                "array",
                allValues.toString(),
                "Use for multiple query values",
                DOCS_BASE + "#array-matcher"));
        suggestions.add(new MatcherSuggestion("query:" + param, options));
      } else if (matchers.size() == 1) {
        String value = matchers.get(0) != null ? (String) matchers.get(0).getValue() : "";
        List<MatcherOption> options = new ArrayList<>();
        /**
         * Suggest an 'exact' matcher for the query value. Reason: Use when you want to match a
         * specific query value exactly (case-sensitive). This is the most precise matcher and is
         * suitable for static query values.
         */
        options.add(
            new MatcherOption(
                "exact", value, "Use for exact query match", DOCS_BASE + "#exact-matcher"));
        /**
         * Suggest an 'array' matcher for a single value (for completeness, but usually only needed
         * for multiple values). Reason: Use when you want to match a single-value array query
         * parameter.
         */
        options.add(
            new MatcherOption(
                "array", value, "Use for single-value array query", DOCS_BASE + "#array-matcher"));
        /**
         * Suggest a 'glob' matcher for wildcards in query values. Reason: Use when the query value
         * contains wildcards or variable segments.
         */
        if (value.contains("*")) {
          options.add(
              new MatcherOption(
                  "glob", value, "Use for wildcard query match", DOCS_BASE + "#glob-matcher"));
        }
        /**
         * Suggest a 'regex' matcher for advanced query value matching. Reason: Use when you need to
         * match complex patterns, variable tokens, or use regular expressions for flexibility. This
         * is the most flexible but also the most complex matcher type.
         */
        options.add(
            new MatcherOption(
                "regex", ".*", "Use for advanced query pattern", DOCS_BASE + "#regex-matcher"));
        suggestions.add(new MatcherSuggestion("query:" + param, options));
      }
    }
    return suggestions;
  }
}
