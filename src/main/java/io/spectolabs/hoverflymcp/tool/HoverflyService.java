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

  @Tool(description = ToolDescriptionUtil.HoverflyStatus.DESCRIPTION)
  public HoverflyResponse hoverflyStatus() {
    boolean isRunning = hoverfly != null && hoverfly.isHealthy();
    return HoverflyResponse.ok(
        isRunning
            ? ToolDescriptionUtil.HoverflyStatus.RUNNING_MESSAGE
            : ToolDescriptionUtil.HoverflyStatus.NOT_RUNNING_MESSAGE);
  }

  @Tool(description = ToolDescriptionUtil.StartHoverflyAsWebServer.DESCRIPTION)
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
      return HoverflyResponse.ok(ToolDescriptionUtil.StartHoverflyAsWebServer.STARTED_MESSAGE);
    }
    return HoverflyResponse.ok(
        ToolDescriptionUtil.StartHoverflyAsWebServer.ALREADY_RUNNING_MESSAGE);
  }

  @Tool(description = ToolDescriptionUtil.StopHoverfly.DESCRIPTION)
  public HoverflyResponse stopHoverfly() {
    if (hoverfly != null) {
      hoverfly.close();
      hoverfly = null;
      return HoverflyResponse.ok(ToolDescriptionUtil.StopHoverfly.STOPPED_MESSAGE);
    }
    return HoverflyResponse.ok(ToolDescriptionUtil.StopHoverfly.NOT_RUNNING_STATUS_MESSAGE);
  }

  @Tool(description = ToolDescriptionUtil.GetHoverflyVersion.DESCRIPTION)
  public HoverflyResponse getHoverflyVersion() {
    if (hoverfly != null) {
      return HoverflyResponse.ok(
          ToolDescriptionUtil.GetHoverflyVersion.VERSION_PREFIX
              + hoverfly.getHoverflyInfo().getVersion());
    }
    return HoverflyResponse.error(ToolDescriptionUtil.GetHoverflyVersion.NOT_RUNNING_ERROR_MESSAGE);
  }

  @Tool(description = ToolDescriptionUtil.ListAllMockApis.DESCRIPTION)
  public Simulation listAllMockAPIs() {
    validateIfHoverflyIsRunning();
    return hoverflyClient.getSimulation();
  }

  @Tool(description = ToolDescriptionUtil.CreateMockApi.DESCRIPTION)
  public HoverflyResponse createMockAPI(
      @ToolParam(description = ToolDescriptionUtil.CreateMockApi.PARAM_DESCRIPTION)
          String requestResponseJson) {

    validateIfHoverflyIsRunning();
    try {
      ValidationResult validationResult =
          requestResponsePairValidator.validate(requestResponseJson);
      if (validationResult.isInValid()) {
        validationResult.addDocumentationSuggestion();
        return HoverflyResponse.error(
            ToolDescriptionUtil.CreateMockApi.VALIDATION_FAILED_MESSAGE, validationResult);
      }
      RequestResponsePair pair =
          objectMapper.readValue(requestResponseJson, RequestResponsePair.class);
      Simulation simulation = hoverflyClient.getSimulation();
      simulation.getHoverflyData().getPairs().add(pair);

      hoverflyClient.setSimulation(simulation);
      return HoverflyResponse.ok(ToolDescriptionUtil.CreateMockApi.SUCCESS_MESSAGE);
    } catch (JsonProcessingException exception) {
      ValidationResult validationResult = new ValidationResult();
      validationResult.addDocumentationSuggestion();
      return HoverflyResponse.error(
          ToolDescriptionUtil.CreateMockApi.INVALID_JSON_MESSAGE, validationResult);
    } catch (HoverflyClientException e) {
      return HoverflyResponse.error(
          ToolDescriptionUtil.CreateMockApi.FAILED_TO_CREATE_PREFIX + e.getMessage());
    }
  }

  @Tool(description = ToolDescriptionUtil.RemoveAllMockedApis.DESCRIPTION)
  public HoverflyResponse removeAllMockedAPIs() {
    validateIfHoverflyIsRunning();
    try {
      hoverflyClient.deleteSimulation();
      return HoverflyResponse.ok(ToolDescriptionUtil.RemoveAllMockedApis.SUCCESS_MESSAGE);
    } catch (HoverflyClientException e) {
      return HoverflyResponse.error(
          ToolDescriptionUtil.RemoveAllMockedApis.FAILED_TO_REMOVE_PREFIX + e.getMessage());
    }
  }

  @Tool(description = ToolDescriptionUtil.GetHoverflyAccessInfo.DESCRIPTION)
  public HoverflyResponse getHoverflyAccessInfo() {
    return HoverflyResponse.ok(ToolDescriptionUtil.GetHoverflyAccessInfo.INFO);
  }

  @Tool(description = ToolDescriptionUtil.GetHoverflySimulationConcepts.DESCRIPTION)
  public HoverflyResponse getHoverflySimulationConcepts() {
    return HoverflyResponse.ok(ToolDescriptionUtil.GetHoverflySimulationConcepts.CONCEPTS);
  }

  private void validateIfHoverflyIsRunning() {
    if (hoverfly == null || !hoverfly.isHealthy()) {
      throw new HoverflyClientException(
          ToolDescriptionUtil.CommonErrors.HOVERFLY_NOT_HEALTHY_MESSAGE);
    }
  }
}
