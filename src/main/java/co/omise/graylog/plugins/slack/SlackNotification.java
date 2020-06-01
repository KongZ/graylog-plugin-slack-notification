package co.omise.graylog.plugins.slack;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;

import org.graylog.events.event.EventDto;
import org.graylog.events.notifications.EventNotification;
import org.graylog.events.notifications.EventNotificationContext;
import org.graylog.events.notifications.EventNotificationService;
import org.graylog.events.notifications.PermanentEventNotificationException;
import org.graylog.events.processor.EventDefinitionDto;
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
	private static Logger logger = LoggerFactory.getLogger(SlackNotification.class);

	public interface Factory extends EventNotification.Factory<SlackNotification> {
		@Override
		SlackNotification create();
	}

	private final EventNotificationService notificationCallbackService;
	private final StreamService streamService;
	private final NotificationService notificationService;
	private final NodeId nodeId;

	@Inject
	public SlackNotification(EventNotificationService notificationCallbackService, StreamService streamService,
			NotificationService notificationService, NodeId nodeId) {
		this.notificationCallbackService = notificationCallbackService;
		this.streamService = streamService;
		this.notificationService = notificationService;
		this.nodeId = nodeId;
	}

	@Override
	public void execute(EventNotificationContext ctx) throws PermanentEventNotificationException {
		try {
			send(ctx);
		} catch (Exception e) {
			final Notification systemNotification = notificationService.buildNow().addNode(nodeId.toString())
					.addType(Notification.Type.GENERIC).addSeverity(Notification.Severity.NORMAL)
					.addDetail("errors", e.getMessage());
			notificationService.publishIfFirst(systemNotification);
			throw new PermanentEventNotificationException(e.getMessage(), e);
		}
	}

	private void send(EventNotificationContext ctx) {
		final SlackNotificationConfig configuration = (SlackNotificationConfig) ctx.notificationConfig();
		final SlackClient client = new SlackClient(configuration);
		final String color = configuration.color();
		final String footerIconUrl = configuration.footerIconUrl();
		final String footerText = configuration.footerText();
		final String tsField = configuration.footerTsField();
		final String customFields = configuration.fields();
		final boolean isAcknowledge = configuration.acknowledge();
		final String graylogUri = configuration.graylogUrl();
		final boolean isPreFormat = configuration.preformat();
		// Create Message
		SlackMessage message = new SlackMessage(buildMessage(ctx, configuration), configuration.channel(),
				configuration.userName(), configuration.messageIcon(), configuration.linkNames());

		// Create Attachment for Backlog and Fields section
		final List<Message> backlogItems = getAlarmBacklog(ctx, configuration);
		int count = configuration.backlogItems();
		if (count > 0) {
			final int blSize = backlogItems.size();
			if (blSize < count) {
				count = blSize;
			}
			boolean shortMode = configuration.shortMode();
			final String[] fields;
			if (!isNullOrEmpty(customFields)) {
				fields = customFields.split(",");
			} else {
				fields = new String[0];
			}
			for (int i = 0; i < count; i++) {
				Message backlogItem = backlogItems.get(i);
				String footer = null;
				Long ts = null;
				if (!isNullOrEmpty(footerText)) {
					footer = StringReplacement.replace(footerText, backlogItem.getFields()).trim();
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
				if (fields.length > 0) {
					Arrays.stream(fields).map(String::trim).forEach(f -> addField(backlogItem, f, shortMode, attachment));
				}
			}
		}
		// Send message to Slack
		try {
			client.send(message);
		} catch (SlackClient.SlackClientException e) {
			throw new RuntimeException("Could not send message to Slack.", e);
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
	 * Create a slack <code>text</code> message from alert condition result.
	 *
	 * @param streams a Graylog stream
	 * @param result  a Graylog alert condition result
	 * @return a text to be used in Slack message
	 */
	private String buildMessage(EventNotificationContext ctx, SlackNotificationConfig configuration) {

		String graylogUri = configuration.graylogUrl();
		String notifyUsers = configuration.notifyUsers();

		StringBuilder message = new StringBuilder();
		if (!isNullOrEmpty(notifyUsers)) {
			List<MessageSummary> messageList = notificationCallbackService.getBacklogForEvent(ctx);
			if (messageList.size() > 0) {
				for (MessageSummary messageSummary : messageList) {
					notifyUsers = StringReplacement.replaceWithPrefix(notifyUsers, "@",
							messageSummary.getRawMessage().getFields());
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
		// message.append(result.getResultDescription());
		String eventName = ctx.eventDefinition().map(EventDefinitionDto::title).orElse("Unknown");
		message.append(eventName);

		// String description = result.getResultDescription();
		// if (description != null) {
		// message.append(description.replaceFirst("Stream", "").trim());
		// }
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

	private List<Message> getAlarmBacklog(EventNotificationContext ctx, SlackNotificationConfig config) {
		final List<MessageSummary> matchingMessages = notificationCallbackService.getBacklogForEvent(ctx);
		List<Message> messages = matchingMessages.stream().map(ms -> {
			Message m = ms.getRawMessage();
			m.addField("gl2_document_index", ms.getIndex());
			return m;
		}).collect(Collectors.toList());
		return messages;
	}

}