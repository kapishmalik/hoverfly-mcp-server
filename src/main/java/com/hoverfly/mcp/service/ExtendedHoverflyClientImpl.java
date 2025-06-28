package com.hoverfly.mcp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoverfly.mcp.dto.HoverflyLogs;
import com.hoverfly.mcp.tool.util.HoverflyPortConstants;
import io.specto.hoverfly.junit.api.HoverflyClient;
import io.specto.hoverfly.junit.api.HoverflyClientException;
import io.specto.hoverfly.junit.api.command.JournalIndexCommand;
import io.specto.hoverfly.junit.api.command.SortParams;
import io.specto.hoverfly.junit.api.model.CsvDataSource;
import io.specto.hoverfly.junit.api.model.ModeArguments;
import io.specto.hoverfly.junit.api.model.PostServeAction;
import io.specto.hoverfly.junit.api.view.DiffView;
import io.specto.hoverfly.junit.api.view.HoverflyInfoView;
import io.specto.hoverfly.junit.api.view.JournalIndexView;
import io.specto.hoverfly.junit.api.view.PostServeActions;
import io.specto.hoverfly.junit.api.view.StateView;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit.core.model.Journal;
import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.Simulation;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExtendedHoverflyClientImpl implements ExtendedHoverflyClient {
  private final HoverflyClient hoverflyClient;

  private final int connectTimeoutMillis;

  private final int readTimeoutMillis;

  private final String adminHost;

  private final String adminScheme;

  public ExtendedHoverflyClientImpl(
      HoverflyClient hoverflyClient,
      @Value("${hoverfly.admin.connectTimeoutMillis:2000}") int connectTimeoutMillis,
      @Value("${hoverfly.admin.readTimeoutMillis:5000}") int readTimeoutMillis,
      @Value("${hoverfly.admin.host:localhost}") String adminHost,
      @Value("${hoverfly.admin.scheme:http}") String adminScheme) {
    this.hoverflyClient = hoverflyClient;
    this.connectTimeoutMillis = connectTimeoutMillis;
    this.readTimeoutMillis = readTimeoutMillis;
    this.adminHost = adminHost;
    this.adminScheme = adminScheme;
  }

  @Override
  public HoverflyLogs getLogs(int limit) throws HoverflyClientException {
    String urlStr =
        String.format(
            "%s://%s:%d/api/v2/logs?limit=%d",
            adminScheme, adminHost, HoverflyPortConstants.ADMIN_PORT, limit);
    try {
      URL url = new URL(urlStr);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setConnectTimeout(connectTimeoutMillis);
      conn.setReadTimeout(readTimeoutMillis);
      int responseCode = conn.getResponseCode();
      if (responseCode != 200) {
        throw new HoverflyClientException("Failed to fetch logs: HTTP " + responseCode);
      }
      try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
        String responseBody = in.lines().collect(Collectors.joining());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseBody, HoverflyLogs.class);
      }
    } catch (Exception e) {
      throw new HoverflyClientException("Failed to fetch logs: " + e.getMessage());
    }
  }

  @Override
  public void setSimulation(Simulation simulation) {
    hoverflyClient.setSimulation(simulation);
  }

  @Override
  public void setSimulation(String simulation) {
    hoverflyClient.setSimulation(simulation);
  }

  @Override
  public void addSimulation(Simulation simulation) {
    hoverflyClient.addSimulation(simulation);
  }

  @Override
  public Simulation getSimulation() {
    return hoverflyClient.getSimulation();
  }

  @Override
  public com.fasterxml.jackson.databind.JsonNode getSimulationJson() {
    return hoverflyClient.getSimulationJson();
  }

  @Override
  public void deleteSimulation() {
    hoverflyClient.deleteSimulation();
  }

  @Override
  public Journal getJournal(int var1, int var2) {
    return hoverflyClient.getJournal(var1, var2);
  }

  @Override
  public Journal getJournal(int var1, int var2, SortParams sortParams) {
    return hoverflyClient.getJournal(var1, var2, sortParams);
  }

  @Override
  public Journal searchJournal(Request request) {
    return hoverflyClient.searchJournal(request);
  }

  @Override
  public void deleteJournal() {
    hoverflyClient.deleteJournal();
  }

  @Override
  public List<JournalIndexView> getJournalIndex() {
    return hoverflyClient.getJournalIndex();
  }

  @Override
  public void addJournalIndex(JournalIndexCommand journalIndexCommand) {
    hoverflyClient.addJournalIndex(journalIndexCommand);
  }

  @Override
  public void deleteJournalIndex(String s) {
    hoverflyClient.deleteJournalIndex(s);
  }

  @Override
  public List<CsvDataSource> getCsvDataSources() {
    return hoverflyClient.getCsvDataSources();
  }

  @Override
  public void addCsvDataSource(CsvDataSource csvDataSource) {
    hoverflyClient.addCsvDataSource(csvDataSource);
  }

  @Override
  public void deleteCsvDataSource(String s) {
    hoverflyClient.deleteCsvDataSource(s);
  }

  @Override
  public PostServeActions getPostServeActions() {
    return hoverflyClient.getPostServeActions();
  }

  @Override
  public void updatePostServeAction(PostServeAction postServeAction) {
    hoverflyClient.updatePostServeAction(postServeAction);
  }

  @Override
  public void deletePostServeAction(String s) {
    hoverflyClient.deletePostServeAction(s);
  }

  @Override
  public void deleteState() {
    hoverflyClient.deleteState();
  }

  @Override
  public StateView getState() {
    return hoverflyClient.getState();
  }

  @Override
  public void setState(StateView stateView) {
    hoverflyClient.setState(stateView);
  }

  @Override
  public void updateState(StateView stateView) {
    hoverflyClient.updateState(stateView);
  }

  @Override
  public DiffView getDiffs() {
    return hoverflyClient.getDiffs();
  }

  @Override
  public void cleanDiffs() {
    hoverflyClient.cleanDiffs();
  }

  @Override
  public HoverflyInfoView getConfigInfo() {
    return hoverflyClient.getConfigInfo();
  }

  @Override
  public void setDestination(String destination) {
    hoverflyClient.setDestination(destination);
  }

  @Override
  public void setMode(HoverflyMode hoverflyMode) {
    hoverflyClient.setMode(hoverflyMode);
  }

  @Override
  public void setMode(HoverflyMode hoverflyMode, ModeArguments modeArguments) {
    hoverflyClient.setMode(hoverflyMode, modeArguments);
  }

  @Override
  public boolean getHealth() {
    return hoverflyClient.getHealth();
  }
}
