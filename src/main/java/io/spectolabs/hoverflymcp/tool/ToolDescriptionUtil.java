package io.spectolabs.hoverflymcp.tool;

/**
 * Utility class containing tool descriptions for Hoverfly MCP tools. This class centralizes all
 * tool descriptions to improve maintainability.
 */
public final class ToolDescriptionUtil {

  private ToolDescriptionUtil() {
    // Utility class - prevent instantiation
  }

  // ============================================================================
  // HOVERFLY STATUS TOOL
  // ============================================================================
  public static final class HoverflyStatus {
    public static final String DESCRIPTION = "Returns the current status of the Hoverfly server.";
    public static final String RUNNING_MESSAGE = "Hoverfly is running as webserver mode";
    public static final String NOT_RUNNING_MESSAGE = "Hoverfly is not running";
  }

  // ============================================================================
  // START HOVERFLY AS WEBSERVER TOOL
  // ============================================================================
  public static final class StartHoverflyAsWebServer {
    public static final String DESCRIPTION =
        """
    Starts the Hoverfly mock server in simulate mode and exposes it on port 8500 for mock server and port 8888 for admin endpoint.

    **Access URLs:**
    - **Admin UI:** http://localhost:8888
    - **Mock Server Endpoint:** http://localhost:8500
    - **View or download the current simulation at:** http://localhost:8500/api/v2/simulation

    You can:
    - Point your API clients to `http://localhost:8500`
    """;
    public static final String STARTED_MESSAGE =
        "Hoverfly started as WebServer on port 8500 with no mocked APIs. Use http://localhost:8500  as mock server endpoint, http://localhost:8888 for admin UI.";
    public static final String ALREADY_RUNNING_MESSAGE = "Hoverfly already running";
  }

  // ============================================================================
  // STOP HOVERFLY TOOL
  // ============================================================================
  public static final class StopHoverfly {
    public static final String DESCRIPTION = "Stops the Hoverfly mock server and clears its state.";
    public static final String STOPPED_MESSAGE = "Hoverfly stopped and all mocked APIs are removed";
    public static final String NOT_RUNNING_STATUS_MESSAGE = "Hoverfly not running";
  }

  // ============================================================================
  // GET HOVERFLY VERSION TOOL
  // ============================================================================
  public static final class GetHoverflyVersion {
    public static final String DESCRIPTION =
        "Returns the current version of the Hoverfly instance.";
    public static final String VERSION_PREFIX = "Hoverfly version: ";
    public static final String NOT_RUNNING_ERROR_MESSAGE = "Hoverfly is not running";
  }

  // ============================================================================
  // LIST ALL MOCK APIS TOOL
  // ============================================================================
  public static final class ListAllMockApis {
    public static final String DESCRIPTION =
        "Lists all request-response pairs (mock APIs) currently active in Hoverfly.";
  }

  // ============================================================================
  // CREATE MOCK API TOOL
  // ============================================================================
  public static final class CreateMockApi {
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
    - View or download the current simulation at: http://localhost:8500/api/v2/simulation
    """;
    public static final String VALIDATION_FAILED_MESSAGE =
        "Validation failed for request response pair passed";
    public static final String INVALID_JSON_MESSAGE = "Invalid JSON for RequestResponsePair";
    public static final String FAILED_TO_CREATE_PREFIX = "Failed to create mock API: ";
  }

  // ============================================================================
  // REMOVE ALL MOCKED APIS TOOL
  // ============================================================================
  public static final class RemoveAllMockedApis {
    public static final String DESCRIPTION = "Deletes all mock APIs from Hoverfly's simulation.";
    public static final String SUCCESS_MESSAGE = "All mocked APIs removed.";
    public static final String FAILED_TO_REMOVE_PREFIX = "Failed to remove mocks: ";
  }

  // ============================================================================
  // COMMON ERROR MESSAGES
  // ============================================================================
  public static final class CommonErrors {
    public static final String HOVERFLY_NOT_HEALTHY_MESSAGE =
        "Hoverfly is not running or not in healthy statue";
  }
}
