package com.hoverfly.mcp.suggestion.matcher;

import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PathMatcherSuggester implements FieldMatcherSuggester {
  @Override
  public List<MatcherSuggestion> suggest(Request request) {
    List<MatcherSuggestion> suggestions = new ArrayList<>();
    if (request == null || request.getPath() == null) return suggestions;
    List<RequestFieldMatcher<String>> pathMatchers = request.getPath();
    for (int i = 0; i < pathMatchers.size(); i++) {
      String value = pathMatchers.get(i) != null ? pathMatchers.get(i).getValue() : "";
      List<MatcherOption> options = new ArrayList<>();
      /**
       * Suggest an 'exact' matcher for the path field. Reason: Use when you want to match a
       * specific path exactly (case-sensitive). This is the most precise matcher and is suitable
       * for static paths.
       */
      options.add(
          new MatcherOption(
              "exact", value, "Use for exact path match", DOCS_BASE + "#exact-matcher"));
      /**
       * Suggest a 'glob' matcher if the value looks like a path (contains '/'). Reason: Paths often
       * contain variable segments (e.g., IDs, versions). This replaces digit sequences with '*' for
       * wildcard matching. Useful for matching a range of similar paths with dynamic parts.
       */
      if (value.contains("/")) {
        options.add(
            new MatcherOption(
                "glob",
                value.replaceAll("\\d+", "*"),
                "Use for wildcard path match",
                DOCS_BASE + "#glob-matcher"));
      }
      /**
       * Suggest a 'regex' matcher for advanced path matching. Reason: Use when you need to match
       * complex patterns, variable segments, or use regular expressions for flexibility. This is
       * the most flexible but also the most complex matcher type.
       */
      options.add(
          new MatcherOption(
              "regex", ".*", "Use for advanced pattern matching", DOCS_BASE + "#regex-matcher"));
      String fieldName = pathMatchers.size() > 1 ? "path[" + i + "]" : "path";
      suggestions.add(new MatcherSuggestion(fieldName, options));
    }
    return suggestions;
  }
}
