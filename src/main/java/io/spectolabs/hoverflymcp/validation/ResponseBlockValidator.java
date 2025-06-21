package io.spectolabs.hoverflymcp.validation;

import com.fasterxml.jackson.databind.JsonNode;
import io.spectolabs.hoverflymcp.response.ValidationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for the "response" block of a Hoverfly request-response pair.
 *
 * <p>Ensures that: - Required fields (e.g., "status") are present and valid. - Optional fields
 * (e.g., "body", "templated") are suggested if missing. - Any unknown fields not in the allowed
 * list are reported.
 *
 * <p>The allowed fields can be configured via application properties: {@code
 * hoverfly.validation.response-fields}
 */
@Slf4j
@Component
public class ResponseBlockValidator {

  /**
   * Performs validation on the response block of a simulation pair.
   *
   * @param response the response JsonNode
   * @param result the validation result to which issues and suggestions are added
   */
  public void validate(JsonNode response, ValidationResult result) {
    if (response == null || response.isMissingNode()) {
      result.getMissingFields().add("Missing required field: response");
      result.getSuggestedFixes().add("Add 'response' block with at least 'status' field.");
      return;
    }

    validateStatus(response, result);
    validateOptionalSuggestions(response, result);
  }

  /**
   * Validates that "status" field exists and is an integer.
   *
   * @param response the response JsonNode
   * @param result the validation result to update
   */
  private void validateStatus(JsonNode response, ValidationResult result) {
    if (!response.has("status")) {
      result.getMissingFields().add("Missing required field: response.status");
      result.getSuggestedFixes().add("Add 'status': 200 in response");
    } else if (!response.get("status").isInt()) {
      result.getTypeMismatches().add("'status' in response must be an integer");
      result.getSuggestedFixes().add("Change 'status' to an integer value like 200");
    }
  }

  /**
   * Suggests optional fields like "body" or "templated" if not present.
   *
   * @param response the response JsonNode
   * @param result the validation result to update
   */
  private void validateOptionalSuggestions(JsonNode response, ValidationResult result) {
    if (!response.has("body")) {
      result.getSuggestedFixes().add("Add a 'body' to response to simulate returned data");
    }

    if (!response.has("templated")) {
      result
          .getSuggestedFixes()
          .add(
              "Add 'templated': true to use dynamic response content if needed. See https://docs.hoverfly.io/en/latest/pages/keyconcepts/templates/templates.html");
    }
  }
}
