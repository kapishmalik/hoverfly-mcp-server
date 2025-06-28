package com.hoverfly.mcp.service;

import com.hoverfly.mcp.dto.HoverflyLogs;
import io.specto.hoverfly.junit.api.HoverflyClient;

public interface ExtendedHoverflyClient extends HoverflyClient {
  HoverflyLogs getLogs(int limit);
}
