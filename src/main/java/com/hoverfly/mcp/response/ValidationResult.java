package com.hoverfly.mcp.response;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ValidationResult {
  private final List<String> missingFields;
  private final List<String> extraFields;
  private final List<String> typeMismatches;
  private final List<String> suggestedFixes;

  public ValidationResult() {
    missingFields = new ArrayList<>();
    extraFields = new ArrayList<>();
    typeMismatches = new ArrayList<>();
    suggestedFixes = new ArrayList<>();
  }

  public boolean isInValid() {
    return !missingFields.isEmpty() || !typeMismatches.isEmpty() || !extraFields.isEmpty();
  }

  public void addDocumentationSuggestion() {
    suggestedFixes.add(
        "Refer to the Hoverfly simulation schema for more information: https://docs.hoverfly.io/en/latest/pages/keyconcepts/simulations/pairs.html");
  }
}
