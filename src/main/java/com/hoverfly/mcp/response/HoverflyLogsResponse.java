package com.hoverfly.mcp.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HoverflyLogsResponse {
  private List<HoverflyLogView> logs;
}
