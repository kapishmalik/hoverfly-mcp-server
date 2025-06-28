package com.hoverfly.mcp.tool.util;

public final class GetHoverflyLogsToolConstants {
  private GetHoverflyLogsToolConstants() {}

  public static final String DEBUG_LOGS_TOOL_DESCRIPTION =
      """
# Hoverfly Debug Logs Tool

Use this tool to fetch recent logs from the running Hoverfly instance for advanced debugging and troubleshooting. This is especially useful when:

- You encounter errors while creating a mock API.
- You receive unexpected or incorrect mock responses.
- You suspect issues with templating, matcher configuration, or simulation logic.
- You want to understand why a request was not matched as expected.

## How to Use
- **When:** Use this tool immediately after a failed mock creation, or when you get an unexpected response from a mock API.
- **Why:** The logs provide detailed insight into Hoverfly's internal processing, including matcher evaluation, templating errors, and simulation execution.
- **What:** The tool returns the latest log entries from the Hoverfly admin API, which can be used to diagnose and resolve issues.

## Parameters
- `limit` (optional, default: 500): The maximum number of log entries to fetch. Increase this if you need a longer history.

## Example
```json
{
  "limit": 500
}
```

## Output
- Returns a JSON object containing a list of log entries, each with timestamp, level, and message fields.

---

**Tip:** Use this tool as your first step in debugging any Hoverfly simulation or templating issue!
""";

  public static final String DEBUG_LOGS_TOOL_PARAM_DESCRIPTION =
      """
- `limit` (optional, default: 500): The maximum number of log entries to fetch from Hoverfly logs. Increase this value if you need a longer log history for debugging, or decrease it for a more concise output.
""";
}
