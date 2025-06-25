package com.hoverfly.mcp.tool.util;

/** Utility class containing tool descriptions for Start Hoverfly As WebServer tool. */
public final class StartHoverflyAsWebServerToolConstants {

  private StartHoverflyAsWebServerToolConstants() {
    // Utility class - prevent instantiation
  }

  public static final String DESCRIPTION =
      """
    Starts the Hoverfly mock server in simulate mode and exposes it on port 8500 for mock server and port 8888 for admin endpoint.

    **Access URLs:**
    - **Admin UI:** http://localhost:8888
    - **Mock Server Endpoint:** http://localhost:8500
    - **View or download the current simulation at:** http://localhost:8888/api/v2/simulation

    You can:
    - Point your API clients to `http://localhost:8500`
    """;

  public static final String STARTED_MESSAGE =
      "Hoverfly started as WebServer on port 8500 with no mocked APIs. Use http://localhost:8500  as mock server endpoint, http://localhost:8888 for admin UI.";
  public static final String ALREADY_RUNNING_MESSAGE = "Hoverfly already running";
}
