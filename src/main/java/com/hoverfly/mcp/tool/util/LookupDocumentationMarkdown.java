package com.hoverfly.mcp.tool.util;

/** Holds long Markdown documentation constants for tools like lookup_documentation. */
public final class LookupDocumentationMarkdown {

  private LookupDocumentationMarkdown() {}

  public static final String TEMPLATING_DOC =
      """
          # 📘 Templating in Hoverfly

          Hoverfly supports dynamic response templating using double curly braces (`{{ ... }}`) in your simulation JSON.

          ---

          ## ✨ Basic Usage

          - You can insert values from the request (headers, query, body, etc.) into your response.
          - **Enable templating** by setting `"templated": true` in your response block.

          ---

          ## 🔥 Extracting Data from the Request

          ### From Headers
          ```
          {{ Request.Header "X-My-Header" }}
          ```

          ### From Query Parameters
          ```
          {{ Request.QueryParam "userId" }}
          ```

          ### From JSON Body (PATCH/POST/PUT)
          **Correct syntax:**
          ```
          {{ Request.Body 'jsonpath' '$.field' }}
          ```
          - This extracts the value of `field` from the JSON body.

          **Example:**
          ```json
          {
            "request": {
              "method": [{"matcher": "exact", "value": "PATCH"}],
              "path": [{"matcher": "regex", "value": "/api/v1/domains/.+/secret"}],
              "body": [{"matcher": "regex", "value": ".*"}]
            },
            "response": {
              "status": 200,
              "body": "{\\"authinfo\\": \\"{{ Request.Body 'jsonpath' '$.domsecret' }}\\"}",
              "templated": true
            }
          }
          ```

          ---

          ## ⚠️ Common Pitfalls

          - ❌ **Do not use:** `{{ Request.Body.field }}` or `{{ Request.Body | jsonPath "$.field" }}`
          - ✅ **Use only:** `{{ Request.Body 'jsonpath' '$.field' }}`
          - Always set `"templated": true` in your response.
          - Use a broad body matcher (e.g., `".*"`) for PATCH/POST/PUT with JSON.

          ---

          ## 🛠️ Troubleshooting

          - If the template is not rendered, check your Hoverfly logs for errors like:
            - `template cannot be nil`
            - `Helper 'Body' called with wrong number of arguments`
          - Ensure your request body is valid JSON and matches the JSONPath.
          - Double-check the field name and path in your JSONPath expression.

          ---

          ## 🧩 Supported Helpers

          - `Request.Body 'jsonpath' '$.field'` — Extract from JSON body
          - `Request.Header "Header-Name"` — Extract from headers
          - `Request.QueryParam "param"` — Extract from query string
          - `Request.Path` — The request path

          ---

          ## 📝 Version Compatibility

          - The `'jsonpath'` syntax for extracting from JSON bodies is supported in Hoverfly v1.3.0 and above.

          ---

          ## 🧠 Advanced Tips & Gotchas

          - **Nested JSONPath:** `{{ Request.Body 'jsonpath' '$.user.details[0].email' }}`
          - **Multiple fields:** Use several templates in one response.
          - **No default values:** If a field is missing, the output is empty.
          - **Escaping:** Remember to escape quotes and backslashes in JSON strings.
          - **Debugging:** Use the Hoverfly admin UI (`http://localhost:8888`) and logs for troubleshooting.
          - **Limitations:** No conditionals, loops, or string manipulation in templates.
          - **Validate JSON:** Use a linter to check your simulation file before loading.

          ---

          ## 🔗 References

          - [Hoverfly Templating Documentation](https://docs.hoverfly.io/en/latest/pages/keyconcepts/templating/templating.html#getting-data-from-the-request)
          - [Hoverfly Matching Documentation](https://docs.hoverfly.io/en/latest/pages/keyconcepts/matching/matching.html)
          - [Hoverfly GitHub Issues](https://github.com/SpectoLabs/hoverfly/issues)
          """;

  public static final String MATCHERS_DOC =
      """
                ## 📘 Matchers in Hoverfly

                Matchers define how request fields are compared.

                ---

                ### ✅ Supported Matcher Types

                - `exact` — exact string match
                - `glob` — wildcard match (`*`, `?`)
                - `regex` — pattern-based matching
                - `array` — matches if any element matches
                - `json`, `jsonpartial`, `jsonpath` — for JSON bodies
                - `xml`, `xpath` — for XML bodies
                - `jwt` — for verifying claims in JWT tokens
                - `form` — for `application/x-www-form-urlencoded` payloads

                ---

                ### 🔍 When to Use Which Matcher

                | Matcher       | Use When...                                                                 |
                |---------------|------------------------------------------------------------------------------|
                | `exact`       | You know the full fixed value (e.g., `GET`, `/api/data`)                    |
                | `glob`        | You need simple wildcards (`/users/*/profile`)                              |
                | `regex`       | You want fine-grained matching (e.g., numeric patterns, dates, emails)      |
                | `array`       | You want to match if **any** of multiple values are correct                 |
                | `json`        | Full exact JSON body match (order & formatting must be same)                |
                | `jsonpartial` | You want to match on a **subset** of the JSON body                          |
                | `jsonpath`    | You want to extract and match values using JSONPath                         |
                | `xml`         | You want full exact XML body match                                          |
                | `xpath`       | You want to match specific XML nodes or attributes                          |
                | `jwt`         | You want to match decoded fields of a JWT token                             |
                | `form`        | You want to match form URL-encoded fields (e.g., `name=kapish`)             |

                ---

                ### 🔹 Matcher Structure

                ```json
                "path": [
                  {
                    "matcher": "exact",
                    "value": "/api/v1/data"
                  }
                ]
                ```

                ---

                ### ⚙️ Matching Strategies

                - `strongest` (default): Picks the most specific match
                - `first`: Returns the first match found in order

                ---

                ### 🔶 Required Fields

                - Must-have: `request.method`
                - Recommended: `path`, `destination`, `scheme`, `headers`, `body`, `query`

                ---

                ### ❗ Common Mistakes

                - Missing `"matcher"` or `"value"` keys
                - Using unsupported matcher types
                - Providing flat headers instead of object-of-arrays
                - Omitting required `request.method`

                ---

                🔗 [Matchers Documentation](https://docs.hoverfly.io/en/latest/pages/keyconcepts/matching/matching.html)
                """;
}
