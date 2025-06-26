package com.hoverfly.mcp.suggestion.matcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.specto.hoverfly.junit.core.model.Request;
import io.specto.hoverfly.junit.core.model.RequestFieldMatcher;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

@Component
@RequiredArgsConstructor
public class BodyMatcherSuggester implements FieldMatcherSuggester {

  private final ObjectMapper objectMapper;

  @Override
  public List<MatcherSuggestion> suggest(Request request) {
    List<MatcherSuggestion> suggestions = new ArrayList<>();
    if (request == null || request.getBody() == null) return suggestions;
    List<RequestFieldMatcher> bodyMatchers = request.getBody();
    for (int i = 0; i < bodyMatchers.size(); i++) {
      String value = bodyMatchers.get(i) != null ? (String) bodyMatchers.get(i).getValue() : "";
      List<MatcherOption> options = new ArrayList<>();
      /**
       * Suggest 'json' and 'jsonpartial' matchers if the value is valid JSON. Reason: Use 'json'
       * for strict JSON structure matching, and 'jsonpartial' for partial matching (ignoring extra
       * fields). These are the best fit for structured JSON request bodies.
       */
      if (isValidJson(value)) {
        options.add(
            new MatcherOption(
                "json", value, "Use for JSON body match", DOCS_BASE + "#json-matcher"));
        options.add(
            new MatcherOption(
                "jsonpartial",
                value,
                "Use for partial JSON match",
                DOCS_BASE + "#jsonpartial-matcher"));
        /**
         * Suggest an 'xml' matcher if the value is valid XML. Reason: Use when the request body is
         * structured XML. This matcher is designed for XML structure.
         */
      } else if (isValidXml(value)) {
        options.add(
            new MatcherOption("xml", value, "Use for XML body match", DOCS_BASE + "#xml-matcher"));
        options.add(
            new MatcherOption(
                "xmlpartial",
                value,
                "Use for partial XML match",
                DOCS_BASE + "#xmlpartial-matcher"));
        /**
         * Suggest a 'regex' matcher for XML body matching. Reason: Use when you need to match XML
         * patterns or validate XML structure with regular expressions. This provides flexibility
         * for complex XML matching scenarios.
         */
        options.add(
            new MatcherOption(
                "regex", ".*", "Use for XML pattern matching", DOCS_BASE + "#regex-matcher"));
      } else {
        /**
         * Suggest an 'exact' matcher for the body value. Reason: Use when you want to match the
         * body exactly (case-sensitive). Suitable for static or plain text bodies.
         */
        options.add(
            new MatcherOption(
                "exact", value, "Use for exact body match", DOCS_BASE + "#exact-matcher"));
        /**
         * Suggest a 'form' matcher if the value looks like form-encoded data. Reason: Use when the
         * body is URL-encoded form data (e.g., key1=value1&key2=value2).
         */
        if (value.contains("&") && value.contains("=")) {
          options.add(
              new MatcherOption(
                  "form", value, "Use for form-encoded body match", DOCS_BASE + "#form-matcher"));
        }
        /**
         * Suggest a 'regex' matcher for advanced body matching. Reason: Use when you need to match
         * complex patterns or validate the body format. This is the most flexible matcher.
         */
        options.add(
            new MatcherOption(
                "regex", ".*", "Use for advanced body pattern", DOCS_BASE + "#regex-matcher"));
      }
      String fieldName = bodyMatchers.size() > 1 ? "body[" + i + "]" : "body";
      suggestions.add(new MatcherSuggestion(fieldName, options));
    }
    return suggestions;
  }

  private boolean isValidJson(String value) {
    try {
      objectMapper.readTree(value);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isValidXml(String value) {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      // Security configurations to prevent XXE attacks
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      factory.setXIncludeAware(false);
      factory.setExpandEntityReferences(false);
      factory.setNamespaceAware(true);
      DocumentBuilder builder = factory.newDocumentBuilder();
      builder.parse(new InputSource(new StringReader(value)));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
