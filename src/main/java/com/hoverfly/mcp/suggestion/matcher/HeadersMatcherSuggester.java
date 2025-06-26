package com.hoverfly.mcp.suggestion.matcher;

import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class HeadersMatcherSuggester implements FieldMatcherSuggester {
  @Override
  public List<MatcherSuggestion> suggest(Request request) {
    List<MatcherSuggestion> suggestions = new ArrayList<>();
    if (request == null || request.getHeaders() == null) return suggestions;
    for (Map.Entry<String, List<RequestFieldMatcher>> entry : request.getHeaders().entrySet()) {
      String header = entry.getKey();
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
                "Use for multiple header values",
                DOCS_BASE + "#array-matcher"));
        suggestions.add(new MatcherSuggestion("header:" + header, options));
      } else if (matchers.size() == 1) {
        String value = matchers.get(0) != null ? (String) matchers.get(0).getValue() : "";
        List<MatcherOption> options = new ArrayList<>();
        /**
         * Suggest an 'exact' matcher for the header value. Reason: Use when you want to match a
         * specific header value exactly (case-sensitive). This is the most precise matcher and is
         * suitable for static header values.
         */
        options.add(
            new MatcherOption(
                "exact", value, "Use for exact header match", DOCS_BASE + "#exact-matcher"));
        /**
         * Suggest a 'glob' matcher for Authorization headers with Bearer tokens or headers with
         * semicolon-separated values. Reason: Bearer tokens are often dynamic, so 'Bearer *'
         * matches any token. Semicolon-separated values (e.g., cookies) may have variable parts.
         * This allows wildcard matching for those segments.
         */
        if ("Authorization".equalsIgnoreCase(header) && value.startsWith("Bearer ")) {
          options.add(
              new MatcherOption(
                  "glob", "Bearer *", "Use for any Bearer token", DOCS_BASE + "#glob-matcher"));
          /**
           * Suggest a 'jwt' matcher for Authorization headers with Bearer tokens. Reason: Use when
           * you want to match a JWT token specifically. This matcher is designed for JWT structure.
           */
          options.add(
              new MatcherOption(
                  "jwt",
                  value.substring(7),
                  "Use for JWT token match",
                  DOCS_BASE + "#jwt-matcher"));
        } else if (value.contains(";")) {
          options.add(
              new MatcherOption(
                  "glob",
                  value.replaceAll(";.*", ";*"),
                  "Use for wildcard header match",
                  DOCS_BASE + "#glob-matcher"));
        }
        /**
         * Suggest a 'regex' matcher for advanced header value matching. Reason: Use when you need
         * to match complex patterns, variable tokens, or use regular expressions for flexibility.
         * This is the most flexible but also the most complex matcher type.
         */
        options.add(
            new MatcherOption(
                "regex", ".*", "Use for advanced header pattern", DOCS_BASE + "#regex-matcher"));
        suggestions.add(new MatcherSuggestion("header:" + header, options));
      }
    }
    return suggestions;
  }
}
