package com.hoverfly.mcp.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoverfly.mcp.response.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestBlockValidatorTest {

  private RequestBlockValidator validator;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    validator = new RequestBlockValidator();
    objectMapper = new ObjectMapper();
  }

  @Test
  void shouldPassWithValidRequestBlock() throws Exception {
    String json =
        """
        {
          "method": [{ "matcher": "exact", "value": "GET" }],
          "destination": [{ "matcher": "exact", "value": "example.com" }],
          "path": [{ "matcher": "glob", "value": "/api/*" }],
          "query": { "query": [{ "matcher": "exact", "value": "param=value" }] },
          "headers": { "Content-Type": [{ "matcher": "exact", "value": "application/json" }] }
        }
        """;
    JsonNode requestNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(requestNode, result);
    assertFalse(result.isInValid());
  }

  @Test
  void shouldFailWhenRequestBlockIsMissing() {
    ValidationResult result = new ValidationResult();
    validator.validate(null, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldFailWhenMethodIsMissing() throws Exception {
    String json =
        """
        {
          "destination": [{ "matcher": "exact", "value": "example.com" }]
        }
        """;
    JsonNode requestNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(requestNode, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldFailWhenMatcherInMethodIsMissingValue() throws Exception {
    String json =
        """
        {
          "method": [{ "matcher": "exact" }],
          "destination": [{ "matcher": "exact", "value": "example.com" }]
        }
        """;
    JsonNode requestNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(requestNode, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldFailWithInvalidMatcherType() throws Exception {
    String json =
        """
        {
          "method": [{ "matcher": "invalid-matcher", "value": "GET" }],
          "destination": [{ "matcher": "exact", "value": "example.com" }]
        }
        """;
    JsonNode requestNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(requestNode, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldFailWhenHeadersIsNotAnObject() throws Exception {
    String json =
        """
        {
          "method": [{ "matcher": "exact", "value": "GET" }],
          "destination": [{ "matcher": "exact", "value": "example.com" }],
          "headers": "Content-Type: application/json"
        }
        """;
    JsonNode requestNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(requestNode, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldFailWhenInvalidMatcherIsPassed() throws Exception {
    String json =
        """
        {
          "method": "GET",
          "destination": [{ "matcher": "hello", "value": "example.com" }]
        }
        """;
    JsonNode requestNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(requestNode, result);
    assertTrue(result.isInValid());
  }

  @Test
  void shouldPassEvenWhenOptionalFieldsAreMissing() throws Exception {
    String json =
        """
        {
          "method": [{ "matcher": "exact", "value": "GET" }],
          "destination": [{ "matcher": "exact", "value": "example.com" }]
        }
        """;
    JsonNode requestNode = objectMapper.readTree(json);
    ValidationResult result = new ValidationResult();
    validator.validate(requestNode, result);
    assertFalse(result.isInValid());
  }
}
