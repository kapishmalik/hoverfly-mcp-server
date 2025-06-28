package com.hoverfly.mcp.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoverfly.mcp.dto.HoverflyLogEntry;
import com.hoverfly.mcp.response.HoverflyLogView;
import com.hoverfly.mcp.response.HoverflyLogsResponse;
import com.hoverfly.mcp.response.HoverflyResponse;
import com.hoverfly.mcp.response.ValidationResult;
import com.hoverfly.mcp.service.ExtendedHoverflyClient;
import com.hoverfly.mcp.suggestion.matcher.MatcherSuggestionCollector;
import com.hoverfly.mcp.tool.util.*;
import com.hoverfly.mcp.validation.RequestResponsePairValidator;
import io.specto.hoverfly.junit.api.HoverflyClientException;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.specto.hoverfly.junit.core.model.Simulation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * Service class providing operations for managing the Hoverfly mock server and its simulation
 * state.
 *
 * <p>This service exposes methods to start/stop Hoverfly, manage mock APIs, fetch logs, and provide
 * documentation for Hoverfly features such as matchers and templating. Each method is annotated as
 * a Spring AI tool for integration with AI-driven workflows.
 */
@Service
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class HoverflyService {
  /** The Hoverfly instance managed by this service. */
  private Hoverfly hoverfly;

  /** Client for interacting with Hoverfly's simulation and logs. */
  private final ExtendedHoverflyClient hoverflyClient;

  /** Jackson ObjectMapper for JSON serialization/deserialization. */
  private final ObjectMapper objectMapper;

  /** Validator for request-response pair JSON. */
  private final RequestResponsePairValidator requestResponsePairValidator;

  /** Collector for matcher suggestions based on request fields. */
  private final MatcherSuggestionCollector matcherSuggestionCollector;

  /**
   * Returns the current status of the Hoverfly server (running or not running).
   *
   * @return HoverflyResponse indicating the status of the Hoverfly server.
   */
  @Tool(name = "get_hoverfly_status", description = HoverflyStatusToolConstants.DESCRIPTION)
  public HoverflyResponse hoverflyStatus() {
    boolean isRunning = hoverfly != null && hoverfly.isHealthy();
    return HoverflyResponse.ok(
        isRunning
            ? HoverflyStatusToolConstants.RUNNING_MESSAGE
            : HoverflyStatusToolConstants.NOT_RUNNING_MESSAGE);
  }

  /**
   * Starts the Hoverfly mock server in simulate mode as a web server.
   *
   * @return HoverflyResponse indicating whether Hoverfly was started or already running.
   */
  @Tool(
      name = "start_hoverfly_web_server",
      description = StartHoverflyAsWebServerToolConstants.DESCRIPTION)
  public HoverflyResponse startHoverflyAsWebServer() {
    if (hoverfly == null) {
      hoverfly =
          new Hoverfly(
              HoverflyConfig.localConfigs()
                  .addCommands("-listen-on-host", "0.0.0.0")
                  .proxyLocalHost()
                  .adminPort(8888)
                  .proxyPort(8500)
                  .asWebServer(),
              HoverflyMode.SIMULATE);
      hoverfly.start();
      return HoverflyResponse.ok(StartHoverflyAsWebServerToolConstants.STARTED_MESSAGE);
    }
    return HoverflyResponse.ok(StartHoverflyAsWebServerToolConstants.ALREADY_RUNNING_MESSAGE);
  }

  /**
   * Stops the Hoverfly mock server and clears its state.
   *
   * @return HoverflyResponse indicating whether Hoverfly was stopped or not running.
   */
  @Tool(name = "stop_hoverfly_server", description = StopHoverflyToolConstants.DESCRIPTION)
  public HoverflyResponse stopHoverfly() {
    if (hoverfly != null) {
      hoverfly.close();
      hoverfly = null;
      return HoverflyResponse.ok(StopHoverflyToolConstants.STOPPED_MESSAGE);
    }
    return HoverflyResponse.ok(StopHoverflyToolConstants.NOT_RUNNING_STATUS_MESSAGE);
  }

  /**
   * Returns the current version of the Hoverfly instance.
   *
   * @return HoverflyResponse containing the Hoverfly version, or an error if not running.
   */
  @Tool(name = "fetch_hoverfly_version", description = GetHoverflyVersionToolConstants.DESCRIPTION)
  public HoverflyResponse getHoverflyVersion() {
    if (hoverfly != null) {
      return HoverflyResponse.ok(
          GetHoverflyVersionToolConstants.VERSION_PREFIX + hoverfly.getHoverflyInfo().getVersion());
    }
    return HoverflyResponse.error(GetHoverflyVersionToolConstants.NOT_RUNNING_ERROR_MESSAGE);
  }

  /**
   * Lists all request-response pairs (mock APIs) currently active in Hoverfly.
   *
   * @return Simulation object representing the current Hoverfly simulation.
   * @throws HoverflyClientException if Hoverfly is not running or healthy.
   */
  @Tool(name = "list_hoverfly_mocks", description = ListAllMockApisToolConstants.DESCRIPTION)
  public Simulation listAllMockAPIs() {
    validateIfHoverflyIsRunning();
    return hoverflyClient.getSimulation();
  }

  /**
   * Creates a new mock API by adding a request-response pair to Hoverfly's simulation.
   *
   * @param requestResponseJson JSON string representing a valid RequestResponsePair object.
   * @return HoverflyResponse indicating success or validation errors.
   * @throws HoverflyClientException if Hoverfly is not running or healthy.
   */
  @Tool(name = "add_hoverfly_mock", description = CreateMockApiToolConstants.DESCRIPTION)
  public HoverflyResponse createMockAPI(
      @ToolParam(description = CreateMockApiToolConstants.PARAM_DESCRIPTION)
          String requestResponseJson) {

    validateIfHoverflyIsRunning();
    try {
      ValidationResult validationResult =
          requestResponsePairValidator.validate(requestResponseJson);
      if (validationResult.isInValid()) {
        validationResult.addDocumentationSuggestion();
        return HoverflyResponse.error(
            CreateMockApiToolConstants.VALIDATION_FAILED_MESSAGE, validationResult);
      }
      RequestResponsePair pair =
          objectMapper.readValue(requestResponseJson, RequestResponsePair.class);
      Simulation simulation = hoverflyClient.getSimulation();
      simulation.getHoverflyData().getPairs().add(pair);

      hoverflyClient.setSimulation(simulation);
      return HoverflyResponse.ok(CreateMockApiToolConstants.SUCCESS_MESSAGE);
    } catch (JsonProcessingException exception) {
      ValidationResult validationResult = new ValidationResult();
      validationResult.addDocumentationSuggestion();
      return HoverflyResponse.error(
          CreateMockApiToolConstants.INVALID_JSON_MESSAGE, validationResult);
    } catch (HoverflyClientException e) {
      return HoverflyResponse.error(
          CreateMockApiToolConstants.FAILED_TO_CREATE_PREFIX + e.getMessage());
    }
  }

  /**
   * Deletes all mock APIs from Hoverfly's simulation.
   *
   * @return HoverflyResponse indicating success or failure.
   * @throws HoverflyClientException if Hoverfly is not running or healthy.
   */
  @Tool(name = "clear_hoverfly_mocks", description = RemoveAllMockedApisToolConstants.DESCRIPTION)
  public HoverflyResponse removeAllMockedAPIs() {
    validateIfHoverflyIsRunning();
    try {
      hoverflyClient.deleteSimulation();
      return HoverflyResponse.ok(RemoveAllMockedApisToolConstants.SUCCESS_MESSAGE);
    } catch (HoverflyClientException e) {
      return HoverflyResponse.error(
          RemoveAllMockedApisToolConstants.FAILED_TO_REMOVE_PREFIX + e.getMessage());
    }
  }

  /**
   * Returns a canonical list of all key Hoverfly endpoints and example usage.
   *
   * @return HoverflyResponse containing endpoint information and usage tips.
   */
  @Tool(
      name = "show_hoverfly_endpoints_info",
      description = GetHoverflyAccessInfoToolConstants.DESCRIPTION)
  public HoverflyResponse getHoverflyAccessInfo() {
    return HoverflyResponse.ok(GetHoverflyAccessInfoToolConstants.INFO);
  }

  /**
   * Returns Hoverfly documentation for a specific topic (e.g., matchers, templating).
   *
   * @param topic The documentation topic to look up. Allowed values: "matchers", "templating".
   * @return HoverflyResponse containing the requested documentation or an error message.
   */
  @Tool(
      name = "get_hoverfly_documentation",
      description = LookupDocumentationToolConstants.DESCRIPTION)
  public HoverflyResponse lookupDocumentation(
      @ToolParam(description = LookupDocumentationToolConstants.TOOL_PARAM_DESCRIPTION)
          String topic) {
    return HoverflyResponse.ok(LookupDocumentationToolConstants.getDocumentation(topic));
  }

  /**
   * Analyzes a single Hoverfly RequestResponsePair JSON and returns matcher suggestions for each
   * request field.
   *
   * @param pairJson JSON string representing a valid Hoverfly RequestResponsePair.
   * @return HoverflyResponse containing matcher suggestions or validation errors.
   */
  @Tool(
      name = "suggest_hoverfly_matchers",
      description = GetMatcherSuggestionsToolConstants.DESCRIPTION)
  public HoverflyResponse matcherSuggestionsForPair(
      @ToolParam(description = GetMatcherSuggestionsToolConstants.PARAM_DESCRIPTION)
          String pairJson) {
    try {
      ValidationResult validationResult = requestResponsePairValidator.validate(pairJson);
      if (validationResult.isInValid()) {
        validationResult.addDocumentationSuggestion();
        return HoverflyResponse.error("Invalid request-response pair.", validationResult);
      }
      RequestResponsePair pair = objectMapper.readValue(pairJson, RequestResponsePair.class);
      var request = pair.getRequest();
      if (request == null) {
        return HoverflyResponse.error("Request block missing in pair JSON.");
      }
      var suggestions = matcherSuggestionCollector.collectSuggestions(request);
      return HoverflyResponse.ok(objectMapper.writeValueAsString(suggestions));
    } catch (Exception e) {
      return HoverflyResponse.error("Failed to analyze matcher suggestions: " + e.getMessage());
    }
  }

  /**
   * Fetches recent Hoverfly logs for debugging when mock creation fails or unexpected mock
   * responses occur. Useful for diagnosing templating, matcher, or simulation issues.
   *
   * @param limit Maximum number of log entries to fetch (optional, default 500).
   * @return HoverflyLogsResponse containing the latest log entries, or an empty list if Hoverfly is
   *     not running.
   */
  @Tool(
      name = "get_hoverfly_debug_logs",
      description = GetHoverflyLogsToolConstants.DEBUG_LOGS_TOOL_DESCRIPTION)
  public HoverflyLogsResponse debugHoverflyWithLogs(
      @ToolParam(description = GetHoverflyLogsToolConstants.DEBUG_LOGS_TOOL_PARAM_DESCRIPTION)
          Integer limit) {
    HoverflyLogsResponse logsResponse = new HoverflyLogsResponse();
    if (hoverfly == null || !hoverfly.isHealthy()) {
      logsResponse.setLogs(List.of());
      return logsResponse;
    }
    try {
      int logLimit = (limit != null && limit > 0) ? limit : 500;
      var hoverflyLogs = hoverflyClient.getLogs(logLimit);
      List<HoverflyLogView> logViews =
          hoverflyLogs.getLogs().stream().map(HoverflyLogEntry::toView).toList();
      logsResponse.setLogs(logViews);
      return logsResponse;
    } catch (Exception e) {
      logsResponse.setLogs(List.of());
      return logsResponse;
    }
  }

  /**
   * Throws an exception if Hoverfly is not running or not healthy.
   *
   * @throws HoverflyClientException if Hoverfly is not running or healthy.
   */
  private void validateIfHoverflyIsRunning() {
    if (hoverfly == null || !hoverfly.isHealthy()) {
      throw new HoverflyClientException(CommonErrorToolConstants.HOVERFLY_NOT_HEALTHY_MESSAGE);
    }
  }
}
