package com.kongz.graylog.plugins.slack;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floreysoft.jmte.Engine;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.graylog.events.event.EventDto;
import org.graylog.events.notifications.EventNotification;
import org.graylog.events.notifications.EventNotificationContext;
import org.graylog.events.notifications.EventNotificationService;
import org.graylog.events.notifications.EventNotificationModelData;
import org.graylog.events.notifications.EventNotificationException;
import org.graylog.events.notifications.PermanentEventNotificationException;
import org.graylog.events.notifications.TemporaryEventNotificationException;
import org.graylog.events.processor.EventDefinitionDto;
import org.graylog2.jackson.TypeReferences;
import org.graylog2.notifications.Notification;
import org.graylog2.notifications.NotificationService;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.plugin.system.NodeId;
import org.graylog2.streams.StreamService;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlackNotification implements EventNotification {
	private static Logger LOG = LoggerFactory.getLogger(SlackNotification.class);

	public interface Factory extends EventNotification.Factory<SlackNotification> {
		@Override
		SlackNotification create();
	}

	private final EventNotificationService notificationCallbackService;
	private final StreamService streamService;
	private final NotificationService notificationService;
	private final NodeId nodeId;
	private final Engine templateEngine;
	private final ObjectMapper objectMapper;

	@Inject
	public SlackNotification(EventNotificationService notificationCallbackService, StreamService streamService,
			NotificationService notificationService, NodeId nodeId, Engine templateEngine, ObjectMapper objectMapper) {
		this.notificationCallbackService = notificationCallbackService;
		this.streamService = streamService;
		this.notificationService = notificationService;
		this.nodeId = nodeId;
		this.templateEngine = templateEngine;
		this.objectMapper = objectMapper;
	}

	@Override
	public void execute(EventNotificationContext ctx) throws EventNotificationException {
		try {
			send(ctx);
		} catch (TemporaryEventNotificationException e) {
			//scheduler needs to retry a TemporaryEventNotificationException
			throw e;
		}	catch (PermanentEventNotificationException e) {
			String errorMessage = String.format("Error sending the SlackNotification. %s", e.getMessage());
			final Notification systemNotification = notificationService.buildNow()
						.addNode(nodeId.toString())
						.addType(Notification.Type.GENERIC)
						.addSeverity(Notification.Severity.URGENT)
						.addDetail("title", "SlackNotification Failed")
						.addDetail("description", errorMessage);
			notificationService.publishIfFirst(systemNotification);
			throw e;
		} catch (Exception e) {
			throw new EventNotificationException("There was an exception triggering the SlackNotification", e);
		}
	}

	private void send(EventNotificationContext ctx) throws EventNotificationException{
		final SlackNotificationConfig configuration = (SlackNotificationConfig) ctx.notificationConfig();
		final SlackClient client = new SlackClient(configuration);
		final String color = configuration.color();
		final String footerIconUrl = configuration.footerIconUrl();
		final String footerText = configuration.footerText();
		final String tsField = configuration.footerTsField();
		final String customField = configuration.fields();
		final boolean isAcknowledge = configuration.acknowledge();
		final String graylogUri = configuration.graylogUrl();
		final boolean isPreFormat = configuration.preformat();
		// Create Message
		SlackMessage message = new SlackMessage(buildMessage(ctx, configuration), configuration.channel(),
				configuration.userName(), configuration.messageIcon(), configuration.linkNames());

		// Create Attachment for Backlog and Fields section
		final List<Message> backlogItems = getAlarmBacklog(ctx);
		int count = configuration.backlogItems();
		if (count > 0) {
			final int blSize = backlogItems.size();
			if (blSize < count) {
				count = blSize;
			}
			boolean shortMode = configuration.shortMode();
			final String[] customFields;
			if (!isNullOrEmpty(customField)) {
				customFields = customField.split(",");
			} else {
				customFields = new String[0];
			}
			Map<String, Object> backlogFields = getBacklogsFields(ctx, count);
			Map<String, Object> eventFields = java.util.stream.Stream.of(new Object[][] { 
				{ "event_definition_id", backlogFields.get("event_definition_id") }, 
				{ "event_definition_type", backlogFields.get("event_definition_type") }, 
				{ "event_definition_title", backlogFields.get("event_definition_title") }, 
				{ "event_definition_description", backlogFields.get("event_definition_description") }, 
				{ "job_definition_id", backlogFields.get("job_definition_id") }, 
				{ "job_trigger_id", backlogFields.get("job_trigger_id") }, 
				{ "event", backlogFields.get("event") }, 
		  }).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));
			for (int i = 0; i < count; i++) {
				Message backlogItem = backlogItems.get(i);
				String footer = null;
				Long ts = null;
				Map<String, Object> fields = new HashMap<String, Object>();
				fields.putAll(eventFields);
				fields.putAll(backlogItem.getFields());
				if (!isNullOrEmpty(footerText)) {
					try {
						footer = templateEngine.transform(footerText, ImmutableMap.copyOf(fields)).trim();
					} catch (Exception e) {
						footer = "Invalid footer template";
					}
					if (!isNullOrEmpty(graylogUri))
						footer = new StringBuilder("<").append(buildMessageLink(graylogUri, backlogItem)).append('|')
								.append(footer).append('>').toString();
					try {
						DateTime timestamp = null;
						if ("timestamp".equals(tsField)) { // timestamp is reserved field in org.graylog2.notifications.NotificationImpl
							timestamp = backlogItem.getTimestamp();
						} else {
							Object value = backlogItem.getField(tsField);
							if (value instanceof DateTime) {
								timestamp = (DateTime) value;
							} else {
								timestamp = new DateTime(value, DateTimeZone.UTC);
							}
						}
						ts = timestamp.getMillis() / 1000;
					} catch (NullPointerException | IllegalArgumentException e) {
						// ignore
					}
				}
				List<SlackMessage.Action> actionList = null;
				if (isAcknowledge) {
					actionList = Lists.newArrayList(new SlackMessage.Action("acknowledge", "Acknowledge", "true", "primary"),
							new SlackMessage.Action("decline", "It is not me!!", "true", "danger"));
				}
				StringBuilder backLogMessage = new StringBuilder(backlogItem.getMessage());
				if (isPreFormat)
					backLogMessage.insert(0, "```").append("```");
				final SlackMessage.Attachment attachment = message.addAttachment(backLogMessage.toString(), color, footer,
						footerIconUrl, ts, backlogItem.getId(), actionList);
				if (isPreFormat)
					attachment.setMarkdownIn("text");
				// Add custom fields from backlog list
				if (customFields.length > 0) {
					Arrays.stream(customFields).map(String::trim).forEach(f -> addField(fields, f, shortMode, attachment));
				}
			}
		}
		// Send message to Slack
		try {
			client.send(message);
		} catch (SlackClient.SlackClientException e) {
			throw new EventNotificationException("Could not send message to Slack.", e);
		}
	}

	/**
	 * Shortcut method to add a backlog field into Slack attachment.
	 *
	 * @param message    Graylog Message
	 * @param fieldName  field in backlog to be added
	 * @param shortMode  true to use Slack attachment short mode
	 * @param attachment a Slack attachment object
	 */
	private void addField(Message message, String fieldName, boolean shortMode, SlackMessage.Attachment attachment) {
		Object value = message.getField(fieldName);
		if (value != null) {
			attachment.addField(new SlackMessage.AttachmentField(fieldName, value.toString(), shortMode));
		}
	}

	/**
	 * Shortcut method to add a backlog field into Slack attachment.
	 *
	 * @param fields    fields
	 * @param fieldName  field in backlog to be added
	 * @param shortMode  true to use Slack attachment short mode
	 * @param attachment a Slack attachment object
	 */
	private void addField(Map<String, Object> fields, String fieldName, boolean shortMode, SlackMessage.Attachment attachment) {
		Object value = null;
		try {
			value = templateEngine.transform(fieldName, ImmutableMap.copyOf(fields)).trim();
			if (fieldName.equals(value)) {
				value = null;
			}
		} catch (Exception e) {
			value = null;
		}
		if (value == null) {
			value = fields.get(fieldName);
		}
		if (value != null) {
			attachment.addField(new SlackMessage.AttachmentField(fieldName, value.toString(), shortMode));
		}
	}

	/**
	 * Create a slack <code>text</code> message from alert condition result.
	 *
	 * @param streams a Graylog stream
	 * @param result  a Graylog alert condition result
	 * @return a text to be used in Slack message
	 */
	private String buildMessage(EventNotificationContext ctx, SlackNotificationConfig configuration) {
		final SlackClient client = new SlackClient(configuration);

		String graylogUri = configuration.graylogUrl();
		String notifyUsers = configuration.notifyUsers();

		StringBuilder message = new StringBuilder();
		if (!isNullOrEmpty(notifyUsers)) {
			List<MessageSummary> messageList = notificationCallbackService.getBacklogForEvent(ctx);
			if (!messageList.isEmpty()) {
				for (MessageSummary messageSummary : messageList) {
					notifyUsers = StringReplacement.replaceWithPrefix(notifyUsers, "@",
							messageSummary.getRawMessage().getFields());
				}
				try {
					if (notifyUsers.contains("@")) {
						StringBuilder usersAsId = new StringBuilder();
						String[] users = notifyUsers.split("@");
						for (String user : users) {
							user = user.trim();
							if (!"".equals(user)) {
								String id = client.getSlackUser(user);
								usersAsId.append("<@").append(id).append(">").append(" ");
							}
						}
						notifyUsers = usersAsId.toString();
					}
				} catch (SlackClient.SlackClientException e) {
					LOG.error(e.getMessage(), e);
				}
			} else {
				notifyUsers = StringReplacement.replace(notifyUsers, Collections.emptyMap());
			}
			message.append(notifyUsers.trim()).append(' ');
		}
		EventDto eventDto = ctx.event();
		Set<Stream> streams = streamService.loadByIds(eventDto.sourceStreams());
		for (Stream stream : streams) {
			if (!isNullOrEmpty(graylogUri)) {
				message.append(" <").append(buildStreamLink(graylogUri, stream)).append('|').append(stream.getTitle())
						.append("> ");
			} else {
				message.append(" _").append(stream.getTitle()).append("_ ");
			}
		}
		// Original Graylog message is too redundant. Try to make it short but it must
		// compatible with all 3 Alerts type
		String eventName = ctx.eventDefinition().map(EventDefinitionDto::title).orElse("Unknown");
		message.append(eventName);
		return message.toString();
	}

	private String buildMessageLink(String baseUrl, Message message) {
		StringBuilder builder = new StringBuilder(baseUrl);
		if (!baseUrl.endsWith("/")) {
			builder.append('/');
		}
		return builder.append("messages/").append(message.getField("gl2_document_index")).append('/')
				.append(message.getId()).toString();
	}

	private String buildStreamLink(String baseUrl, Stream stream) {
		StringBuilder builder = new StringBuilder(baseUrl);
		if (!baseUrl.endsWith("/")) {
			builder.append('/');
		}
		return builder.append("streams/").append(stream.getId()).append("/messages?q=*&rangetype=relative&relative=3600")
				.toString();
	}

	private List<Message> getAlarmBacklog(EventNotificationContext ctx) {
		final List<MessageSummary> matchingMessages = notificationCallbackService.getBacklogForEvent(ctx);
		return matchingMessages.stream().map(ms -> {
			Message m = ms.getRawMessage();
			m.addField("gl2_document_index", ms.getIndex());
			return m;
		}).collect(Collectors.toList());
	}

	private Map<String, Object> getBacklogsFields(EventNotificationContext ctx, int backlogItemsCount) {
		List<MessageSummary> backlog = notificationCallbackService.getBacklogForEvent(ctx);
		if (backlogItemsCount > 0 && backlog != null) {
			backlog = backlog.stream().limit(backlogItemsCount).collect(Collectors.toList());
		}
		EventNotificationModelData modelData = EventNotificationModelData.of(ctx, backlog);
		Map<String, Object> objectMap = objectMapper.convertValue(modelData, TypeReferences.MAP_STRING_OBJECT);
		return objectMap;
  }

}