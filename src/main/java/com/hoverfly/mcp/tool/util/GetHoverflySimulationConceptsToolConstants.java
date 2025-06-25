package com.hoverfly.mcp.tool.util;

/** Utility class containing tool descriptions for Get Hoverfly Simulation Concepts tool. */
public final class GetHoverflySimulationConceptsToolConstants {

  private GetHoverflySimulationConceptsToolConstants() {
    // Utility class - prevent instantiation
  }

  public static final String DESCRIPTION =
      """
            Explains Hoverfly simulation concepts including matchers, templating, required fields, and common mistakes.
            Useful for LLMs and developers generating or validating simulation JSON.
            """;

  public static final String CONCEPTS =
      """
            Hoverfly Simulation Concepts

            Matchers
            - Each request field (e.g., `method`, `path`, `destination`) uses an array of matcher objects:
              Example: { "matcher": "exact", "value": "GET" }

            Supported matcher types:
            - exact, glob, regex, array
            - json, jsonpath, jsonpartial
            - xml, xpath
            - jwt, form

            Matching Strategies:
            - **Strongest Match** (default): Returns the most specific match
            - **First Match**: Returns the first match found in order

            More info: https://docs.hoverfly.io/en/latest/pages/keyconcepts/matching/matching.html#matching

            Required Fields in Request
            - Must-have: `request.method`
            - Recommended: `path`, `destination`, `scheme`, `headers`, `body`, `query`

            Response Block
            - Must include:
              - `status`: Integer (e.g., 200)
              - `body`: String
              - `encodedBody`: Boolean (true/false)
              - `headers`: Object (String → Array of Strings)
              - `templated`: Boolean (enables template evaluation)

            Templating (Dynamic Responses)
            - Enable with `"templated": true` in the response
            - Use expressions like:
              - `{{ Request.Method }}`
              - `{{ Request.QueryParam.name }}`
              - `{{ Request.Header.X-Correlation-Id }}`
            - Supports:
              - Date/time: `{{ now }}`, `{{ dateFormat now "yyyy-MM-dd" }}`
              - Random: `{{ randomInt 1000 9999 }}`
              - String ops: `{{ toUpper "text" }}`
              - Logic: `{{#if condition}} ... {{/if}}`

            Docs: https://docs.hoverfly.io/en/latest/pages/keyconcepts/templating/templating.html

            Common Mistakes
            - Missing `"matcher"` or `"value"` keys in request matchers
            - Omitting required `request.method`
            - Using unsupported matcher types
            - Incorrect response headers format (should be object of arrays)

            📎 Additional References:
            - Matchers: https://docs.hoverfly.io/en/latest/pages/keyconcepts/matching/matching.html
            - Templating: https://docs.hoverfly.io/en/latest/pages/keyconcepts/templating/templating.html
            """;
}
