package com.hoverfly.mcp.suggestion.matcher;

public class MatcherOption {
  private String matcher;
  private String description;
  private String example;
  private String howToUse;
  private String documentationLink;

  public MatcherOption() {}

  public MatcherOption(String matcher, String example, String howToUse, String documentationLink) {
    this.matcher = matcher;
    this.example = example;
    this.howToUse = howToUse;
    this.documentationLink = documentationLink;
  }

  public MatcherOption(
      String matcher,
      String description,
      String example,
      String howToUse,
      String documentationLink) {
    this.matcher = matcher;
    this.description = description;
    this.example = example;
    this.howToUse = howToUse;
    this.documentationLink = documentationLink;
  }

  public String getMatcher() {
    return matcher;
  }

  public void setMatcher(String matcher) {
    this.matcher = matcher;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getExample() {
    return example;
  }

  public void setExample(String example) {
    this.example = example;
  }

  public String getHowToUse() {
    return howToUse;
  }

  public void setHowToUse(String howToUse) {
    this.howToUse = howToUse;
  }

  public String getDocumentationLink() {
    return documentationLink;
  }

  public void setDocumentationLink(String documentationLink) {
    this.documentationLink = documentationLink;
  }
}
