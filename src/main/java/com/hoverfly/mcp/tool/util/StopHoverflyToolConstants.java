package com.hoverfly.mcp.tool.util;

/** Utility class containing tool descriptions for Stop Hoverfly tool. */
public final class StopHoverflyToolConstants {

  private StopHoverflyToolConstants() {
    // Utility class - prevent instantiation
  }

  public static final String DESCRIPTION = "Stops the Hoverfly mock server and clears its state.";
  public static final String STOPPED_MESSAGE = "Hoverfly stopped and all mocked APIs are removed";
  public static final String NOT_RUNNING_STATUS_MESSAGE = "Hoverfly not running";
}
