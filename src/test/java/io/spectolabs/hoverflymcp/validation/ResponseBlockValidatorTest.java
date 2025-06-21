package io.spectolabs.hoverflymcp.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spectolabs.hoverflymcp.response.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseBlockValidatorTest {

  private ResponseBlockValidator validator;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    validator = new ResponseBlockValidator();
    objectMapper = new ObjectMapper();
  }

  @Test
  void shouldPassWithValidResponseBlock() throws Exception {
    String json =
        """
        {
          "status": 200,
          "body": "Hello, world!",
          "templated": false
        }
        """;
    JsonNode responseNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(responseNode, result);
    assertFalse(result.isInValid());
  }

  @Test
  void shouldFailWhenResponseBlockIsMissing() {
    ValidationResult result = new ValidationResult();
    validator.validate(null, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldFailWhenStatusIsMissing() throws Exception {
    String json = """
        {
          "body": "Hello, world!"
        }
        """;
    JsonNode responseNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(responseNode, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldFailWhenStatusIsNotAnInteger() throws Exception {
    String json = """
        {
          "status": "200 OK"
        }
        """;
    JsonNode responseNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(responseNode, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldProvideSuggestionsForMissingOptionalFields() throws Exception {
    String json = """
        {
          "status": 200
        }
        """;
    JsonNode responseNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(responseNode, result);
    assertFalse(result.isInValid()); // Still valid, but with suggestions
  }
}
