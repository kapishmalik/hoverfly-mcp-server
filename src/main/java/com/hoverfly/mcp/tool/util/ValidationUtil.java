package com.hoverfly.mcp.tool.util;

import io.specto.hoverfly.junit.api.HoverflyClientException;
import io.specto.hoverfly.junit.core.Hoverfly;

public class ValidationUtil {
  /**
   * Validates if Hoverfly is running and healthy.
   *
   * @param hoverfly The Hoverfly instance
   * @throws HoverflyClientException if not running or unhealthy
   */
  public static void validateIfHoverflyIsRunning(Hoverfly hoverfly) {
    if (hoverfly == null || !hoverfly.isHealthy()) {
      throw new HoverflyClientException("Hoverfly is not running or healthy.");
    }
  }
}
