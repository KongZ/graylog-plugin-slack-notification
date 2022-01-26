package com.kongz.graylog.plugins.slack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.graylog2.plugin.rest.PluginRestResource;
import org.graylog2.shared.rest.resources.RestResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.graylog2.audit.AuditEventTypes;
import org.graylog2.audit.jersey.AuditEvent;

/**
 * Call by Slack when user click any buttons. You need to set this URL <code>
 * https://{host}/api/plugins/org.graylog2.plugins.slack/action</code> in Slack's App Interactive
 * Messages request URL.
 *
 * @author Siri C.
 */
@Path("/action")
public class SlackActionCallback extends RestResource implements PluginRestResource {
  private static final Logger LOG = LoggerFactory.getLogger(SlackActionCallback.class);
  private static ObjectReader objectReader = new ObjectMapper().reader();

  /**
   * Receive POST request from Slack when user click any buttons.
   *
   * @param payload a Slack payload
   * @return a response to Slack
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @AuditEvent(type = AuditEventTypes.ALARM_CALLBACK_UPDATE)
  public Response slackAction(@FormParam("payload") String payload) {
    try {
      if (LOG.isTraceEnabled()) LOG.trace("{}", payload);
      SlackActionPayload slackPayload =
          objectReader.forType(SlackActionPayload.class).readValue(payload);
      List<SlackMessage.Action> actions = slackPayload.actions;
      if (actions != null) {
        for (SlackMessage.Action action : actions) {
          // If user click `acknowledge` button
          if ("acknowledge".equals(action.name) && "true".equals(action.value)) {
            SlackMessage message = slackPayload.originalMessage;
            int attachmentId = Integer.parseInt(slackPayload.attachmentId);
            SlackMessage.Attachment attachment = message.attachments.get(attachmentId - 1);
            attachment.actions = null; // remove action buttons
            StringBuilder builder = new StringBuilder();
            builder
                .append(":white_check_mark: <")
                .append('@')
                .append(slackPayload.user.id)
                .append('|')
                .append(slackPayload.user.name)
                .append("> *acknowledged*");
            message
                .insertAttachment(
                    attachmentId,
                    builder.toString(),
                    "good",
                    null,
                    null,
                    System.currentTimeMillis() / 1000)
                .setMarkdownIn("text");
            final StreamingOutput stream =
                os -> {
                  try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
                      final Writer writer = new BufferedWriter(outputStreamWriter)) {
                    writer.append(message.getJsonString());
                    writer.flush();
                  }
                };
            return Response.ok(stream).type(MediaType.APPLICATION_JSON_TYPE).build();
            // If user click other buttons
          } else {
            SlackMessage message = slackPayload.originalMessage;
            int attachmentId = Integer.parseInt(slackPayload.attachmentId);
            SlackMessage.Attachment attachment = message.attachments.get(attachmentId - 1);
            attachment.actions = null; // remove action buttons
            StringBuilder builder = new StringBuilder();
            builder
                .append(":x: <")
                .append('@')
                .append(slackPayload.user.id)
                .append('|')
                .append(slackPayload.user.name)
                .append("> *It is not me!!* <!here>");
            message
                .insertAttachment(
                    attachmentId,
                    builder.toString(),
                    "danger",
                    null,
                    null,
                    System.currentTimeMillis() / 1000)
                .setMarkdownIn("text");
            final StreamingOutput stream =
                os -> {
                  try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
                      final Writer writer = new BufferedWriter(outputStreamWriter)) {
                    writer.append(message.getJsonString());
                    writer.flush();
                  }
                };
            return Response.ok(stream).type(MediaType.APPLICATION_JSON_TYPE).build();
          }
        }
      }
    } catch (RuntimeException | IOException e) {
      LOG.error("{}", e);
    }
    // If invalid request was sent or something wrong, we just response error message to user private text
    final StreamingOutput stream =
        os -> {
          try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
              final Writer writer = new BufferedWriter(outputStreamWriter)) {
            writer.append(
                "{\"response_type\": \"ephemeral\",\"replace_original\": false,\"text\": \"Sorry, that didn't work. Please try again.\"}");
            writer.flush();
          }
        };
    return Response.ok(stream).type(MediaType.APPLICATION_JSON_TYPE).build();
  }
}
