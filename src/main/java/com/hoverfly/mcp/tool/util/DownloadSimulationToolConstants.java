package com.hoverfly.mcp.tool.util;

/** Utility class containing tool descriptions for Download Simulation tool. */
public final class DownloadSimulationToolConstants {

  private DownloadSimulationToolConstants() {
    // Utility class - prevent instantiation
  }

  public static final String DESCRIPTION =
      "Downloads the current Hoverfly simulation file to the persistent simulation directory. "
          + "The simulation is always saved to /opt/hoverfly-mcp/simulation-data inside the container. "
          + "Mount this directory to your host for persistence. "
          + "The file will be saved with a timestamp for easy identification. "
          + "Use this to persist your mock configurations for later use or sharing.";
  public static final String SUCCESS_MESSAGE = "Simulation file downloaded successfully";
  public static final String DOWNLOAD_FAILED_MESSAGE = "Failed to download simulation file";
  public static final String WRITE_FAILED_MESSAGE = "Failed to write simulation file to disk";

  public static final String SIMULATION_DATA_DIR = "/opt/hoverfly-mcp/simulation-data";
}
