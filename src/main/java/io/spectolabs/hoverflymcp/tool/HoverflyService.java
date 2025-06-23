package io.spectolabs.hoverflymcp.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.specto.hoverfly.junit.api.HoverflyClient;
import io.specto.hoverfly.junit.api.HoverflyClientException;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.specto.hoverfly.junit.core.model.Simulation;
import io.spectolabs.hoverflymcp.response.HoverflyResponse;
import io.spectolabs.hoverflymcp.response.ValidationResult;
import io.spectolabs.hoverflymcp.validation.RequestResponsePairValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class HoverflyService {
  private Hoverfly hoverfly;
  private final HoverflyClient hoverflyClient;
  private final ObjectMapper objectMapper;
  private final RequestResponsePairValidator requestResponsePairValidator;

  @Tool(description = "Returns the current status of the Hoverfly server.")
  public HoverflyResponse hoverflyStatus() {
    boolean isRunning = hoverfly != null && hoverfly.isHealthy();
    return HoverflyResponse.ok(
        isRunning ? "Hoverfly is running as webserver mode" : "Hoverfly is not running");
  }

  @Tool(description = "Starts the Hoverfly mock server in simulate mode as a WebServer.")
  public HoverflyResponse startHoverflyAsWebServer() {
    if (hoverfly == null) {
      hoverfly =
          new Hoverfly(
              HoverflyConfig.localConfigs()
                  .addCommands("-listen-on-host", "0.0.0.0")
                  .asWebServer()
                  .proxyLocalHost()
                  .adminPort(8888)
                  .proxyPort(8500),
              HoverflyMode.SIMULATE);
      hoverfly.start();
      return HoverflyResponse.ok("Hoverfly started as WebServer on port 8500 with no mocked APIs");
    }
    return HoverflyResponse.ok("Hoverfly already running");
  }

  @Tool(description = "Stops the Hoverfly mock server and clears its state.")
  public HoverflyResponse stopHoverfly() {
    if (hoverfly != null) {
      hoverfly.close();
      hoverfly = null;
      return HoverflyResponse.ok("Hoverfly stopped and all mocked APIs are removed");
    }
    return HoverflyResponse.ok("Hoverfly not running");
  }

  @Tool(description = "Returns the current version of the Hoverfly instance.")
  public HoverflyResponse getHoverflyVersion() {
    if (hoverfly != null) {
      return HoverflyResponse.ok("Hoverfly version: " + hoverfly.getHoverflyInfo().getVersion());
    }
    return HoverflyResponse.error("Hoverfly is not running");
  }

  @Tool(description = "Lists all request-response pairs (mock APIs) currently active in Hoverfly.")
  public Simulation listAllMockAPIs() {
    validateIfHoverflyIsRunning();
    return hoverflyClient.getSimulation();
  }

  @Tool(
      description =
          """
            Creates a new mock API by adding a request-response pair to Hoverfly's simulation.

            Make sure to call startHoverfly() first as WebServer. If Hoverfly is not running, this operation will fail.

            The input must be a valid RequestResponsePair object with request matchers
            (like path, method, destination, etc.) and a response containing status, body, and headers.
            """)
  public HoverflyResponse createMockAPI(
      @ToolParam(
              description =
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
                            """)
          String requestResponseJson) {

    validateIfHoverflyIsRunning();
    try {
      ValidationResult validationResult =
          requestResponsePairValidator.validate(requestResponseJson);
      if (validationResult.isInValid()) {
        validationResult.addDocumentationSuggestion();
        return HoverflyResponse.error(
            "Validation failed for request response pair passed", validationResult);
      }
      RequestResponsePair pair =
          objectMapper.readValue(requestResponseJson, RequestResponsePair.class);
      Simulation simulation = hoverflyClient.getSimulation();
      simulation.getHoverflyData().getPairs().add(pair);

      hoverflyClient.setSimulation(simulation);
      return HoverflyResponse.ok("Mock API added. Available on http://0.0.0.0:8500");
    } catch (JsonProcessingException exception) {
      ValidationResult validationResult = new ValidationResult();
      validationResult.addDocumentationSuggestion();
      return HoverflyResponse.error("Invalid JSON for RequestResponsePair", validationResult);
    } catch (HoverflyClientException e) {
      return HoverflyResponse.error("Failed to create mock API: " + e.getMessage());
    }
  }

  @Tool(description = "Deletes all mock APIs from Hoverfly’s simulation.")
  public HoverflyResponse removeAllMockedAPIs() {
    validateIfHoverflyIsRunning();
    try {
      hoverflyClient.deleteSimulation();
      return HoverflyResponse.ok("All mocked APIs removed.");
    } catch (HoverflyClientException e) {
      return HoverflyResponse.error("Failed to remove mocks: " + e.getMessage());
    }
  }

  private void validateIfHoverflyIsRunning() {
    if (hoverfly == null || !hoverfly.isHealthy()) {
      throw new HoverflyClientException("Hoverfly is not running or not in healthy statue");
    }
  }
}
