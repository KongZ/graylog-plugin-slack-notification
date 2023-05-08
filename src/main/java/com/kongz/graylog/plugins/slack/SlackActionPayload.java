package com.kongz.graylog.plugins.slack;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackActionPayload {
  @JsonProperty(value = "actions")
  public List<SlackMessage.Action> actions;

  @JsonProperty(value = "callback_id")
  public String callbackId;

  @JsonProperty(value = "team")
  public Team team;

  @JsonProperty(value = "channel")
  public Channel channel;

  @JsonProperty(value = "user")
  public User user;

  @JsonProperty(value = "action_ts")
  public String actionTs;

  @JsonProperty(value = "message_ts")
  public String messageTs;

  @JsonProperty(value = "attachment_id")
  public String attachmentId;

  @JsonProperty(value = "token")
  public String token;

  @JsonProperty(value = "original_message")
  public SlackMessage originalMessage;

  @JsonProperty(value = "response_url")
  public String responseUrl;

  @JsonCreator
  public SlackActionPayload(
      @JsonProperty(value = "actions") List<SlackMessage.Action> actions,
      @JsonProperty(value = "callback_id") String callbackId,
      @JsonProperty(value = "team") Team team,
      @JsonProperty(value = "channel") Channel channel,
      @JsonProperty(value = "user") User user,
      @JsonProperty(value = "action_ts") String actionTs,
      @JsonProperty(value = "message_ts") String messageTs,
      @JsonProperty(value = "attachment_id") String attachmentId,
      @JsonProperty(value = "token") String token,
      @JsonProperty(value = "original_message") SlackMessage originalMessage,
      @JsonProperty(value = "response_url") String responseUrl) {
    this.actions = actions;
    this.callbackId = callbackId;
    this.team = team;
    this.channel = channel;
    this.user = user;
    this.actionTs = actionTs;
    this.messageTs = messageTs;
    this.attachmentId = attachmentId;
    this.token = token;
    this.originalMessage = originalMessage;
    this.responseUrl = responseUrl;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Team {
    @JsonProperty public String id;
    @JsonProperty public String domain;

    @JsonCreator
    public Team(
        @JsonProperty(value = "id") String id, @JsonProperty(value = "domain") String domain) {
      this.id = id;
      this.domain = domain;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Channel {
    @JsonProperty public String id;
    @JsonProperty public String name;

    @JsonCreator
    public Channel(
        @JsonProperty(value = "id") String id, @JsonProperty(value = "name") String name) {
      this.id = id;
      this.name = name;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class User {
    @JsonProperty public String id;
    @JsonProperty public String name;

    @JsonCreator
    public User(@JsonProperty(value = "id") String id, @JsonProperty(value = "name") String name) {
      this.id = id;
      this.name = name;
    }
  }
}
