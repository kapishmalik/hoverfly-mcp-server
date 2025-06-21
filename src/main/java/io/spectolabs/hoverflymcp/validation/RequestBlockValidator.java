package io.spectolabs.hoverflymcp.validation;

import com.fasterxml.jackson.databind.JsonNode;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import io.spectolabs.hoverflymcp.response.ValidationResult;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for the "request" block of a Hoverfly request-response pair.
 *
 * <p>Ensures that required fields ("method" and "destination") are present and correctly formatted.
 * Optionally checks other fields like "path", "body", and nested "query" matchers. Suggests
 * improvements for common omissions or format errors.
 */
@Slf4j
@Component
public class RequestBlockValidator {

  private static final Set<String> VALID_MATCHER_TYPES =
      Stream.of(RequestFieldMatcher.MatcherType.values())
          .map(Enum::name)
          .map(String::toLowerCase)
          .collect(Collectors.toSet());

  /**
   * Validates the "request" section of the pair.
   *
   * @param request the request JsonNode
   * @param result the validation result to populate
   */
  public void validate(JsonNode request, ValidationResult result) {
    if (request == null || request.isMissingNode()) {
      result.getMissingFields().add("Missing required field: request");
      result
          .getSuggestedFixes()
          .add("Add 'request' block with at least 'method' and 'destination' fields.");
      return;
    }

    validateRequiredMatcher("method", request, result);

    // Validate all matcher arrays for value presence
    Iterator<String> fieldNames = request.fieldNames();
    while (fieldNames.hasNext()) {
      String field = fieldNames.next();
      JsonNode fieldNode = request.get(field);

      if (fieldNode.isArray()) {
        validateMatcherArray(field, fieldNode, result);
      }
    }

    if (request.has("headers") && !request.get("headers").isObject()) {
      result
          .getTypeMismatches()
          .add("'headers' in request must be an object of key → matcher arrays");
    }
  }

  /** Validates required matcher array field exists and has values. */
  private void validateRequiredMatcher(String field, JsonNode node, ValidationResult result) {
    if (!node.has(field)) {
      result.getMissingFields().add("Missing required field: request." + field);
      result.getSuggestedFixes().add("Add '" + field + "' in matcher");
    }
  }

  /** Validates matchers inside an array field and ensures each matcher has a "value". */
  private void validateMatcherArray(String fieldPath, JsonNode array, ValidationResult result) {
    if (array.isArray()) {
      for (int i = 0; i < array.size(); i++) {
        JsonNode matcher = array.get(i);
        if (!matcher.has("value")) {
          result
              .getMissingFields()
              .add("Matcher in 'request." + fieldPath + "' " + " missing 'value'");
          result
              .getSuggestedFixes()
              .add("Add 'value' in matcher object at request." + fieldPath + "[" + i + "]");
        }

        if (matcher.has("matcher")) {
          String matcherType = matcher.get("matcher").asText("").toLowerCase();
          if (!VALID_MATCHER_TYPES.contains(matcherType)) {
            result
                .getTypeMismatches()
                .add("Invalid matcher type in 'request." + fieldPath + "' " + matcherType + "'");
            result
                .getSuggestedFixes()
                .add("Use one of the supported matcher types: " + VALID_MATCHER_TYPES);
          }
        }
      }
    }
  }
}
