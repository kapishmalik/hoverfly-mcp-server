package com.hoverfly.mcp.suggestion.matcher;

import io.specto.hoverfly.junit.core.model.Request;
import java.util.List;

public interface FieldMatcherSuggester {
  List<MatcherSuggestion> suggest(Request request);

  /** Base documentation URL for Hoverfly matcher types. */
  String DOCS_BASE = "https://docs.hoverfly.io/en/latest/pages/keyconcepts/matching/matching.html";
}
