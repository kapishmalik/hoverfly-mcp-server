package com.hoverfly.mcp.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HoverflyLogs {
  private List<HoverflyLogEntry> logs;
}
