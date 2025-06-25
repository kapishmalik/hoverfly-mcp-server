package com.hoverfly.mcp.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.hoverfly.mcp.response.ValidationResult;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validates the top-level structure of a Hoverfly request-response pair JSON. Ensures presence of
 * "request" and "response" blocks and checks for unknown fields.
 */
@Slf4j
@Component
public class TopLevelValidator {

  private static final Set<String> ALLOWED_FIELDS = Set.of("request", "response");

  /**
   * Validates the top-level JSON object.
   *
   * @param root the root node of the JSON
   * @param result the validation result container
   */
  public void validate(JsonNode root, ValidationResult result) {
    if (root == null || root.isMissingNode()) {
      result.getMissingFields().add("Missing root object");
      result.getSuggestedFixes().add("Ensure the input is a valid JSON object");
      return;
    }

    if (!root.has("request")) {
      result.getMissingFields().add("Missing required field: 'request'");
    }

    if (!root.has("response")) {
      result.getMissingFields().add("Missing required field: 'response'");
    }

    root.fieldNames()
        .forEachRemaining(
            field -> {
              if (!ALLOWED_FIELDS.contains(field)) {
                result.getExtraFields().add("Unknown top-level field: '" + field + "'");
                result.getSuggestedFixes().add("Remove unknown field '" + field + "'");
              }
            });
  }
}
