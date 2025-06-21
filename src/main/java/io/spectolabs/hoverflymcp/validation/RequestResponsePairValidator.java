package io.spectolabs.hoverflymcp.validation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.spectolabs.hoverflymcp.response.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Main validator to process and validate a RequestResponsePair JSON string. Delegates field-level
 * checks to component validators and returns a structured result.
 */
@Component
@RequiredArgsConstructor
public class RequestResponsePairValidator {

  private final ObjectMapper objectMapper;
  private final TopLevelValidator topLevelValidator;
  private final RequestBlockValidator requestValidator;
  private final ResponseBlockValidator responseValidator;

  /**
   * Quick check to verify if a given string is syntactically valid JSON.
   *
   * @param json input string
   * @return true if valid JSON, false otherwise
   */
  public boolean isValidJson(String json) {
    try {
      objectMapper.readTree(json);
      return true;
    } catch (JsonProcessingException e) {
      return false;
    }
  }

  /**
   * Validates a full RequestResponsePair JSON string. Aggregates missing fields, type mismatches,
   * extra fields, and fix suggestions.
   *
   * @param json raw JSON string
   * @return validation result object
   */
  public ValidationResult validate(String json) {
    ValidationResult result = new ValidationResult();

    JsonNode root;
    try {
      root = objectMapper.readTree(json);
    } catch (JsonProcessingException e) {
      result.getMissingFields().add("Entire JSON is invalid: " + e.getOriginalMessage());
      result
          .getSuggestedFixes()
          .add("Fix JSON syntax errors like trailing commas or unquoted keys.");
      return result;
    }

    topLevelValidator.validate(root, result);
    requestValidator.validate(root.path("request"), result);
    responseValidator.validate(root.path("response"), result);

    try {
      ObjectMapper checker = new ObjectMapper();
      checker.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true);
      checker.readValue(json, RequestResponsePair.class);
    } catch (Exception ignored) {
      // Optional strict parse check
    }

    return result;
  }
}
