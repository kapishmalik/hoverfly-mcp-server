package com.hoverfly.mcp.tool.util;

import java.util.Map;

/** Utility constants for the LookupDocumentationTool. */
public final class LookupDocumentationToolConstants {

  private LookupDocumentationToolConstants() {}

  public static final String DESCRIPTION =
      """
            Returns Hoverfly documentation for a specific topic.
            Allowed values: `matchers`, `templating`.

            Helpful for developers or LLMs building or validating simulation JSON.
            You can also call this tool to get guidance when facing issues with matchers or templating in your simulation definitions.
            """;

  public static final String TOOL_PARAM_DESCRIPTION =
      "Documentation topic to look up. Allowed values: matchers, templating";
  private static final Map<String, String> DOCUMENTATION_MAP =
      Map.of(
          "matchers", LookupDocumentationMarkdown.MATCHERS_DOC,
          "templating", LookupDocumentationMarkdown.TEMPLATING_DOC);

  public static String getDocumentation(String topic) {
    return DOCUMENTATION_MAP.getOrDefault(
        topic.toLowerCase(),
        """
                Invalid topic: "%s"

                Allowed values: `matchers`, `templating`
                """
            .formatted(topic));
  }
}
