package com.hoverfly.mcp.tool.util;

/** Utility class containing tool descriptions for Remove All Mocked APIs tool. */
public final class RemoveAllMockedApisToolConstants {

  private RemoveAllMockedApisToolConstants() {
    // Utility class - prevent instantiation
  }

  public static final String DESCRIPTION = "Deletes all mock APIs from Hoverfly's simulation.";
  public static final String SUCCESS_MESSAGE = "All mocked APIs removed.";
  public static final String FAILED_TO_REMOVE_PREFIX = "Failed to remove mocks: ";
}
