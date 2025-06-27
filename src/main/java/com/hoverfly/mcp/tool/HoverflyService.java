package com.hoverfly.mcp.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoverfly.mcp.response.HoverflyResponse;
import com.hoverfly.mcp.response.ValidationResult;
import com.hoverfly.mcp.suggestion.matcher.MatcherSuggestionCollector;
import com.hoverfly.mcp.tool.util.*;
import com.hoverfly.mcp.tool.util.GetMatcherSuggestionsToolConstants;
import com.hoverfly.mcp.validation.RequestResponsePairValidator;
import io.specto.hoverfly.junit.api.HoverflyClient;
import io.specto.hoverfly.junit.api.HoverflyClientException;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.specto.hoverfly.junit.core.model.Simulation;
import jakarta.annotation.PostConstruct;
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
  private final MatcherSuggestionCollector matcherSuggestionCollector;

  @Tool(description = HoverflyStatusToolConstants.DESCRIPTION)
  public HoverflyResponse hoverflyStatus() {
    boolean isRunning = hoverfly != null && hoverfly.isHealthy();
    return HoverflyResponse.ok(
        isRunning
            ? HoverflyStatusToolConstants.RUNNING_MESSAGE
            : HoverflyStatusToolConstants.NOT_RUNNING_MESSAGE);
  }

  @Tool(description = StartHoverflyAsWebServerToolConstants.DESCRIPTION)
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

  @Tool(description = StopHoverflyToolConstants.DESCRIPTION)
  public HoverflyResponse stopHoverfly() {
    if (hoverfly != null) {
      hoverfly.close();
      hoverfly = null;
      return HoverflyResponse.ok(StopHoverflyToolConstants.STOPPED_MESSAGE);
    }
    return HoverflyResponse.ok(StopHoverflyToolConstants.NOT_RUNNING_STATUS_MESSAGE);
  }

  @Tool(description = GetHoverflyVersionToolConstants.DESCRIPTION)
  public HoverflyResponse getHoverflyVersion() {
    if (hoverfly != null) {
      return HoverflyResponse.ok(
          GetHoverflyVersionToolConstants.VERSION_PREFIX + hoverfly.getHoverflyInfo().getVersion());
    }
    return HoverflyResponse.error(GetHoverflyVersionToolConstants.NOT_RUNNING_ERROR_MESSAGE);
  }

  @Tool(description = ListAllMockApisToolConstants.DESCRIPTION)
  public Simulation listAllMockAPIs() {
    validateIfHoverflyIsRunning();
    return hoverflyClient.getSimulation();
  }

  @Tool(description = CreateMockApiToolConstants.DESCRIPTION)
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

  @Tool(description = RemoveAllMockedApisToolConstants.DESCRIPTION)
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

  @Tool(description = GetHoverflyAccessInfoToolConstants.DESCRIPTION)
  public HoverflyResponse getHoverflyAccessInfo() {
    return HoverflyResponse.ok(GetHoverflyAccessInfoToolConstants.INFO);
  }

  @Tool(description = GetHoverflySimulationConceptsToolConstants.DESCRIPTION)
  public HoverflyResponse getHoverflySimulationConcepts() {
    return HoverflyResponse.ok(GetHoverflySimulationConceptsToolConstants.CONCEPTS);
  }

  @Tool(description = GetMatcherSuggestionsToolConstants.DESCRIPTION)
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

  private void validateIfHoverflyIsRunning() {
    if (hoverfly == null || !hoverfly.isHealthy()) {
      throw new HoverflyClientException(CommonErrorToolConstants.HOVERFLY_NOT_HEALTHY_MESSAGE);
    }
  }
}
