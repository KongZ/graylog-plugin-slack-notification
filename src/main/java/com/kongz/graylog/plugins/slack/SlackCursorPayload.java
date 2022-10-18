package com.kongz.graylog.plugins.slack;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackCursorPayload {
  @JsonProperty(value = "limit")
  public Integer limit;

  @JsonProperty(value = "cursor")
  public String cursor;

  @JsonCreator
  public SlackCursorPayload(
      @JsonProperty(value = "limit") Integer limit,
      @JsonProperty(value = "cursor") String cursor) {
    this.limit = limit;
    this.cursor = cursor;
  }
}
