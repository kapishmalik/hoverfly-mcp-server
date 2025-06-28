package com.hoverfly.mcp.response;

import lombok.Data;

@Data
public class HoverflyLogView {
  private String level;
  private String msg;
  private String time;
  private String destination;
  private String mode;
  private String port;
  private String proxyPort;
}
