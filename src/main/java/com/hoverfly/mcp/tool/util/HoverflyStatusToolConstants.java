package com.hoverfly.mcp.tool.util;

/** Utility class containing tool descriptions for Hoverfly Status tool. */
public final class HoverflyStatusToolConstants {

  private HoverflyStatusToolConstants() {
    // Utility class - prevent instantiation
  }

  public static final String DESCRIPTION = "Returns the current status of the Hoverfly server.";
  public static final String RUNNING_MESSAGE = "Hoverfly is running as webserver mode";
  public static final String NOT_RUNNING_MESSAGE = "Hoverfly is not running";
}
