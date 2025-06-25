package com.hoverfly.mcp.response;

import lombok.Getter;

@Getter
public class HoverflyResponse {
  private final boolean success;
  private final String message;
  private final ValidationResult validation;

  public HoverflyResponse(boolean success, String message) {
    this.success = success;
    this.message = message;
    this.validation = null;
  }

  public HoverflyResponse(boolean success, String message, ValidationResult validation) {
    this.success = success;
    this.message = message;
    this.validation = validation;
  }

  public static HoverflyResponse ok(String message) {
    return new HoverflyResponse(true, message);
  }

  public static HoverflyResponse error(String message) {
    return new HoverflyResponse(false, message);
  }

  public static HoverflyResponse error(String message, ValidationResult validation) {
    return new HoverflyResponse(false, message, validation);
  }
}
