package com.hoverfly.mcp.tool.util;

/** Utility class containing tool descriptions for the matcher suggestion tool. */
public final class GetMatcherSuggestionsToolConstants {
  private GetMatcherSuggestionsToolConstants() {
    // Utility class - prevent instantiation
  }

  public static final String DESCRIPTION =
      "Analyzes a single Hoverfly RequestResponsePair JSON and returns matcher suggestions for each request field. "
          + "The input must be a valid RequestResponsePair object. Each field in the request block will be analyzed and matcher suggestions will be provided. "
          + "These suggestions can be used to make your matchers more robust and reliable. "
          + "See the documentation for the expected input format and matcher details.";

  public static final String PARAM_DESCRIPTION =
      "Request-response pair JSON string. Must be a valid Hoverfly RequestResponsePair. "
          + "Use the suggestions to improve your matcher configuration for better simulation accuracy.\n"
          + "Example:\n"
          + "{\n"
          + "  \"request\": {\n"
          + "    \"method\": [{ \"matcher\": \"exact\", \"value\": \"GET\" }],\n"
          + "    \"destination\": [{ \"matcher\": \"exact\", \"value\": \"api.example.com\" }],\n"
          + "    \"path\": [{ \"matcher\": \"glob\", \"value\": \"/api/*\" }]\n"
          + "  },\n"
          + "  \"response\": {\n"
          + "    \"status\": 200,\n"
          + "    \"body\": \"{\\\"result\\\":\\\"ok\\\"}\",\n"
          + "    \"encodedBody\": false,\n"
          + "    \"headers\": { \"Content-Type\": [\"application/json\"] },\n"
          + "    \"templated\": false\n"
          + "  }\n"
          + "}";
}
