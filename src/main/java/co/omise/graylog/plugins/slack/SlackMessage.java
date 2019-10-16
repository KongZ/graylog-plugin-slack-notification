package co.omise.graylog.plugins.slack;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackMessage {
  private static ObjectWriter objectWriter = new ObjectMapper().writer();

  @JsonProperty(value = "text")
  public String text;

  @JsonProperty(value = "channel")
  public String channel;

  @JsonProperty(value = "username")
  public String username;

  @JsonProperty(value = "icon_url")
  public String iconUrl;

  @JsonProperty(value = "icon_emoji")
  public String iconEmoji;

  @JsonProperty(value = "parse")
  public String parse = "none";

  @JsonProperty(value = "link_names")
  public boolean linkNames;

  @JsonProperty(value = "attachments")
  public List<Attachment> attachments;

  public SlackMessage(
      String text, String channel, String username, String messageIcon, boolean linkNames) {
    this(text, channel, username, null, null, linkNames, Lists.newArrayList());
    addMessageIcon(messageIcon);
  }

  public SlackMessage(
      @JsonProperty(value = "text") String text,
      @JsonProperty(value = "channel") String channel,
      @JsonProperty(value = "username") String username,
      @JsonProperty(value = "icon_url") String iconUrl,
      @JsonProperty(value = "icon_emoji") String iconEmoji,
      @JsonProperty(value = "link_names") boolean linkNames,
      @JsonProperty(value = "attachments") List<Attachment> attachments) {
    this.text = text;
    this.username = username;
    this.channel = channel;
    this.iconUrl = iconUrl;
    this.iconEmoji = iconEmoji;
    this.linkNames = linkNames;
    this.attachments = attachments;
  }

  public void addMessageIcon(String messageIcon) {
    if (!isNullOrEmpty(messageIcon)) {
      try {
        final URI uri = new URI(messageIcon);
        if (isValidUriScheme(uri, "http", "https")) {
          this.iconUrl = messageIcon;
        } else {
          this.iconEmoji = messageIcon;
        }
      } catch (URISyntaxException e) {
        this.iconEmoji = messageIcon;
      }
    }
  }

  public String getJsonString() throws JsonProcessingException {
    final Map<String, Object> params =
        new HashMap<String, Object>() {
          private static final long serialVersionUID = 5555331305145971889L;

          {
            put("channel", channel);
            put("text", text);
            put("icon_url", iconUrl);
            put("icon_emoji", iconEmoji);
            put("link_names", linkNames);
            put("parse", "none");
          }
        };
    if (!attachments.isEmpty()) {
      params.put("attachments", attachments);
    }
    try {
      return objectWriter.writeValueAsString(params);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Could not build payload JSON.", e);
    }
  }

  public String getRequestString() {
    final Map<String, Object> params =
        new HashMap<String, Object>() {
          private static final long serialVersionUID = 8683918581659326967L;
          {
            put("channel", channel);
            put("text", text);
            put("icon_url", iconUrl);
            put("icon_emoji", iconEmoji);
            put("link_names", linkNames);
            put("parse", "none");
          }
        };
    if (!attachments.isEmpty()) {
      try {
        String attachmentString = objectWriter.writeValueAsString(attachments);
        params.put("attachments", attachmentString);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Could not build payload JSON.", e);
      }
    }
    StringBuilder sb = new StringBuilder();
    try {
      for (HashMap.Entry<String, Object> entry : params.entrySet()) {
        Object value = entry.getValue();
        if (value != null) {
          if (sb.length() > 0) sb.append('&');
          sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
              .append('=')
              .append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
      }
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("Unable to encode URL", e);
    }
    return sb.toString();
  }

  public Attachment addAttachment(Attachment attachment) {
    this.attachments.add(attachment);
    return attachment;
  }

  public Attachment addAttachment(
      String text, String color, String footerText, String footerIconUrl, Long ts) {
    Attachment attachment =
        new Attachment(
            text,
            text,
            null,
            color,
            footerText,
            footerIconUrl,
            ts,
            Lists.newArrayList(),
            null,
            null,
            null);
    this.attachments.add(attachment);
    return attachment;
  }

  public Attachment addAttachment(
      String text,
      String color,
      String footerText,
      String footerIconUrl,
      Long ts,
      String callbackId,
      List<Action> actions) {
    Attachment attachment =
        new Attachment(
            text,
            text,
            null,
            color,
            footerText,
            footerIconUrl,
            ts,
            Lists.newArrayList(),
            callbackId,
            actions,
            null);
    this.attachments.add(attachment);
    return attachment;
  }

  public Attachment insertAttachment(
      int afterIndex, String text, String color, String footerText, String footerIconUrl, Long ts) {
    Attachment attachment =
        new Attachment(
            text,
            text,
            null,
            color,
            footerText,
            footerIconUrl,
            ts,
            Lists.newArrayList(),
            null,
            null,
            null);
    if (afterIndex < 0) this.attachments.add(0, attachment);
    else if (afterIndex > this.attachments.size()) this.attachments.add(attachment);
    else this.attachments.add(afterIndex, attachment);
    return attachment;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Attachment {
    @JsonProperty public String fallback;
    @JsonProperty public String text;
    @JsonProperty public String pretext;
    @JsonProperty public String color = "good";

    @JsonProperty(value = "footer")
    public String footerText = "Graylog";

    @JsonProperty(value = "footer_icon")
    public String footerIconUrl = "";

    @JsonProperty("ts")
    public Long ts;

    @JsonProperty public List<AttachmentField> fields;

    @JsonProperty(value = "callback_id")
    public String callbackId = "";

    @JsonProperty public List<Action> actions;

    @JsonProperty(value = "mrkdwn_in")
    public List<String> mrkdwnIn;

    @JsonCreator
    public Attachment(
        @JsonProperty(value = "fallback") String fallback,
        @JsonProperty(value = "text") String text,
        @JsonProperty(value = "pretext") String pretext,
        @JsonProperty(value = "color") String color,
        @JsonProperty(value = "footer") String footerText,
        @JsonProperty(value = "footer_icon") String footerIconUrl,
        @JsonProperty(value = "ts") Long ts,
        @JsonProperty(value = "fields") List<AttachmentField> fields,
        @JsonProperty(value = "callback_id") String callbackId,
        @JsonProperty(value = "actions") List<Action> actions,
        @JsonProperty(value = "mrkdwn_in") List<String> mrkdwnIn) {
      this.fallback = fallback;
      this.text = text;
      this.pretext = pretext;
      this.color = color;
      this.footerText = footerText;
      this.footerIconUrl = footerIconUrl;
      this.ts = ts;
      this.fields = fields;
      this.callbackId = callbackId;
      this.actions = actions;
      this.mrkdwnIn = mrkdwnIn;
    }

    public Attachment setMarkdownIn(String... in) {
      this.mrkdwnIn = Lists.newArrayList(in);
      return this;
    }

    public Attachment addField(AttachmentField field) {
      this.fields.add(field);
      return this;
    }

    public Attachment addAction(Action action) {
      this.actions.add(action);
      return this;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class AttachmentField {
    @JsonProperty public String title;
    @JsonProperty public String value;

    @JsonProperty(value = "short")
    public boolean isShort = false;

    @JsonCreator
    public AttachmentField(
        @JsonProperty(value = "title") String title,
        @JsonProperty(value = "value") String value,
        @JsonProperty(value = "short") boolean isShort) {
      this.title = title;
      this.value = value;
      this.isShort = isShort;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Action {
    @JsonProperty public String name;
    @JsonProperty public String text;
    @JsonProperty public String type = "button";
    @JsonProperty public String value;
    @JsonProperty public String style = "default";

    public Action(String name, String text, String value) {
      this(name, text, "button", value, "default");
    }

    public Action(String name, String text, String value, String style) {
      this(name, text, "button", value, style);
    }

    @JsonCreator
    public Action(
        @JsonProperty(value = "name") String name,
        @JsonProperty(value = "text") String text,
        @JsonProperty(value = "type") String type,
        @JsonProperty(value = "value") String value,
        @JsonProperty(value = "style") String style) {
      this.name = name;
      this.text = text;
      this.type = type;
      this.value = value;
      this.style = style;
    }
  }

  private static boolean isValidUriScheme(URI uri, String... validSchemes) {
    return uri.getScheme() != null && Arrays.binarySearch(validSchemes, uri.getScheme(), null) >= 0;
  }

}
