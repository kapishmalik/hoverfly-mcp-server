package com.hoverfly.mcp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hoverfly.mcp.response.HoverflyLogView;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoverflyLogEntry {
  private String level;
  private String msg;
  private String time;
  private String destination;
  private String mode;
  private String port;

  @JsonAlias({"ProxyPort"})
  private String proxyPort;

  @JsonAlias({"Mode"})
  private String modeAlt;

  public HoverflyLogView toView() {
    HoverflyLogView view = new HoverflyLogView();
    view.setLevel(this.level);
    view.setMsg(this.msg);
    view.setTime(this.time);
    view.setDestination(this.destination);
    view.setMode(this.mode != null ? this.mode : this.modeAlt);
    view.setPort(this.port);
    view.setProxyPort(this.proxyPort);
    return view;
  }
}
