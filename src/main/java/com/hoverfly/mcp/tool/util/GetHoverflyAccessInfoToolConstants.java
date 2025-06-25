package com.hoverfly.mcp.tool.util;

/** Utility class containing tool descriptions for Get Hoverfly Access Info tool. */
public final class GetHoverflyAccessInfoToolConstants {

  private GetHoverflyAccessInfoToolConstants() {
    // Utility class - prevent instantiation
  }

  public static final String DESCRIPTION =
      """
            Returns a canonical list of all key Hoverfly endpoints, including:

            - Mock server base URL (for intercepted API calls)
            - Admin API and UI (for managing simulations and viewing logs)
            - Simulation file endpoint (for viewing or downloading the current simulation)
            - Example curl commands for quick testing

            This helps developers, tools, or LLMs understand how to interact with Hoverfly after startup.
            """;

  public static final String INFO =
      """
            Hoverfly Access Information:

            -  Mock Server Endpoint: http://localhost:8500
            -  Admin API / UI: http://localhost:8888
            -  Simulation File (View/Download): http://localhost:8888/api/v2/simulation

            📦 Example usage:
            - Call a mock API: curl http://localhost:8500/api/your-mock
            - View simulation file: curl http://localhost:8888/api/v2/simulation
            - Admin UI in browser: http://localhost:8888

            Tip: You can persist mocks by saving the simulation file.
            """;
}
