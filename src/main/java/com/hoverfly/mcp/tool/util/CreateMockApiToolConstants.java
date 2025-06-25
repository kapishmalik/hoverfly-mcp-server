package com.hoverfly.mcp.tool.util;

/** Utility class containing tool descriptions for Create Mock API tool. */
public final class CreateMockApiToolConstants {

  private CreateMockApiToolConstants() {
    // Utility class - prevent instantiation
  }

  public static final String DESCRIPTION =
      """
            Creates a new mock API by adding a request-response pair to Hoverfly's simulation.

            Make sure to call startHoverfly() first as WebServer. If Hoverfly is not running, this operation will fail.

            The input must be a valid RequestResponsePair object with request matchers
            (like path, method, destination, etc.) and a response containing status, body, and headers.
            """;

  public static final String PARAM_DESCRIPTION =
      """
            Contains the expected request and the mock response.

            Example Input:
            {
              "request": {
                "path": [
                  { "matcher": "exact", "value": "/pages/keyconcepts/templates.html" }
                ],
                "method": [
                  { "matcher": "exact", "value": "GET" }
                ],
                "destination": [
                  { "matcher": "exact", "value": "docs.hoverfly.io" }
                ],
                "scheme": [
                  { "matcher": "exact", "value": "http" }
                ],
                "body": [
                  { "matcher": "exact", "value": "" }
                ],
                "query": {
                  "query": [
                    { "matcher": "exact", "value": "true" }
                  ]
                }
              },
              "response": {
                "status": 200,
                "body": "Response from docs.hoverfly.io/pages/keyconcepts/templates.html",
                "encodedBody": false,
                "headers": {
                  "Hoverfly": [ "Was-Here" ]
                },
                "templated": false
              }
            }

            Input RequestResponsePair Schema:
            {
              "type": "object",
              "properties": {
                "request": {
                  "type": "object",
                  "properties": {
                    "path": {
                      "type": "array",
                      "items": { "$ref": "#/definitions/matcher" }
                    },
                    "method": {
                      "type": "array",
                      "items": { "$ref": "#/definitions/matcher" }
                    },
                    "destination": {
                      "type": "array",
                      "items": { "$ref": "#/definitions/matcher" }
                    },
                    "scheme": {
                      "type": "array",
                      "items": { "$ref": "#/definitions/matcher" }
                    },
                    "body": {
                      "type": "array",
                      "items": { "$ref": "#/definitions/matcher" }
                    },
                    "query": {
                      "type": "object",
                      "properties": {
                        "query": {
                          "type": "array",
                          "items": { "$ref": "#/definitions/matcher" }
                        }
                      }
                    }
                  },
                  "required": ["path", "method", "destination", "scheme", "body"]
                },
                "response": {
                  "type": "object",
                  "properties": {
                    "status": { "type": "integer" },
                    "body": { "type": "string" },
                    "encodedBody": { "type": "boolean" },
                    "headers": {
                      "type": "object",
                      "additionalProperties": {
                        "type": "array",
                        "items": { "type": "string" }
                      }
                    },
                    "templated": { "type": "boolean" }
                  },
                  "required": ["status", "body", "encodedBody", "headers", "templated"]
                }
              },
              "required": ["request", "response"],
              "definitions": {
                "matcher": {
                  "type": "object",
                  "properties": {
                    "matcher": { "type": "string" },
                    "value": { "type": "string" }
                  },
                  "required": ["matcher", "value"]
                }
              }
            }
            """;

  public static final String SUCCESS_MESSAGE =
      """
    Mock API added successfully.

    You can:
    - Access the mock server at: http://localhost:8500
    - View or download the current simulation at: http://localhost:8888/api/v2/simulation
    """;
  public static final String VALIDATION_FAILED_MESSAGE =
      "Validation failed for request response pair passed";
  public static final String INVALID_JSON_MESSAGE = "Invalid JSON for RequestResponsePair";
  public static final String FAILED_TO_CREATE_PREFIX = "Failed to create mock API: ";
}
