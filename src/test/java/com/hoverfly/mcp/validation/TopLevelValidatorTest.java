package com.hoverfly.mcp.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoverfly.mcp.response.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TopLevelValidatorTest {

  private TopLevelValidator validator;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    validator = new TopLevelValidator();
    objectMapper = new ObjectMapper();
  }

  @Test
  void shouldPassWithValidTopLevelFields() throws Exception {
    String json =
        """
        {
          "request": {},
          "response": {}
        }
        """;
    JsonNode rootNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(rootNode, result);
    assertFalse(result.isInValid());
  }

  @Test
  void shouldFailWhenRootIsNull() {
    ValidationResult result = new ValidationResult();
    validator.validate(null, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldFailWhenRequestIsMissing() throws Exception {
    String json = """
        {
          "response": {}
        }
        """;
    JsonNode rootNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(rootNode, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldFailWhenResponseIsMissing() throws Exception {
    String json = """
        {
          "request": {}
        }
        """;
    JsonNode rootNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(rootNode, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldFailWithUnknownTopLevelFields() throws Exception {
    String json =
        """
        {
          "request": {},
          "response": {},
          "unknown": "field"
        }
        """;
    JsonNode rootNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(rootNode, result);
    assertTrue(result.isInValid());
  }
}
