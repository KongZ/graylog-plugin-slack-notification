package co.omise.graylog.plugins.slack;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.graylog.events.contentpack.entities.EventNotificationConfigEntity;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog2.contentpacks.model.entities.EntityDescriptor;
import org.graylog2.contentpacks.model.entities.references.ValueReference;

@AutoValue
@JsonTypeName(SlackNotificationConfigEntity.TYPE_NAME)
@JsonDeserialize(builder = SlackNotificationConfigEntity.Builder.class)
public abstract class SlackNotificationConfigEntity implements EventNotificationConfigEntity {

	public static final String TYPE_NAME = "slack-notification-v1";

	@JsonProperty(SlackNotificationConfig.FIELD_WEBHOOK_URL)
	public abstract ValueReference webhookUrl();

	@JsonProperty(SlackNotificationConfig.FIELD_CHANNEL)
	public abstract ValueReference channel();

	@JsonProperty(SlackNotificationConfig.FIELD_USER_NAME)
	public abstract ValueReference userName();

	@JsonProperty(SlackNotificationConfig.FIELD_COLOR)
	public abstract ValueReference color();

	@JsonProperty(SlackNotificationConfig.FIELD_BACKLOG_ITEMS)
	public abstract ValueReference backlogItems();

	@JsonProperty(SlackNotificationConfig.FIELD_SHORT_MODE)
	public abstract ValueReference shortMode();

	@JsonProperty(SlackNotificationConfig.FIELD_NOTIFY_USERS
	public abstract ValueReference notifyUser();

	@JsonProperty(SlackNotificationConfig.FIELD_LINK_NAMES)
	public abstract ValueReference linkNames();

	@JsonProperty(SlackNotificationConfig.FIELD_MESSAGE_ICON)
	public abstract ValueReference messageIcon();

	@JsonProperty(SlackNotificationConfig.FIELD_FOOTER_TEXT)
	public abstract ValueReference footerText();

	@JsonProperty(SlackNotificationConfig.FIELD_FOOTER_ICON_URL)
	public abstract ValueReference footerIconUrl();

	@JsonProperty(SlackNotificationConfig.FIELD_FOOTER_TS_FIELD)
	public abstract ValueReference footerTsField();

	@JsonProperty(SlackNotificationConfig.FIELD_GRAYLOG_URL)
	public abstract ValueReference graylogUrl();

	@JsonProperty(SlackNotificationConfig.FIELD_PROXY_ADDRESS)
	public abstract ValueReference proxyAddress();

	@JsonProperty(SlackNotificationConfig.FIELD_FIELDS)
	public abstract ValueReference fields();

	@JsonProperty(SlackNotificationConfig.FIELD_ACKNOWLEDGE)
	public abstract ValueReference acknowledge();

	@JsonProperty(SlackNotificationConfig.FIELD_PREFORMAT)
	public abstract ValueReference preformat();

	@JsonProperty(SlackNotificationConfig.FIELD_TOKEN)
	public abstract ValueReference token();

	public static Builder builder() {
		return Builder.create();
	}

	public abstract Builder toBuilder();

	@AutoValue.Builder
	public static abstract class Builder implements EventNotificationConfigEntity.Builder<Builder> {

		@JsonCreator
		public static Builder create() {
			return new AutoValue_SlackNotificationConfigEntity.Builder().type(TYPE_NAME);
		}

		@JsonProperty(SlackNotificationConfig.FIELD_WEBHOOK_URL)
		public abstract Builder webhookUrl(ValueReference webhookUrl);

		@JsonProperty(SlackNotificationConfig.FIELD_CHANNEL)
		public abstract Builder channel(ValueReference channel);

		@JsonProperty(SlackNotificationConfig.FIELD_USER_NAME)
		public abstract Builder userName(ValueReference userName);

		@JsonProperty(SlackNotificationConfig.FIELD_COLOR)
		public abstract Builder color(ValueReference color);

		@JsonProperty(SlackNotificationConfig.FIELD_BACKLOG_ITEMS)
		public abstract Builder backlogItems(ValueReference backlogItems);

		@JsonProperty(SlackNotificationConfig.FIELD_SHORT_MODE)
		public abstract Builder shortMode(ValueReference shortMode);

		@JsonProperty(SlackNotificationConfig.FIELD_NOTIFY_USERS)
		public abstract Builder notifyUsers(ValueReference notifyUsers);

		@JsonProperty(SlackNotificationConfig.FIELD_LINK_NAMES)
		public abstract Builder linkNames(ValueReference linkNames);

		@JsonProperty(SlackNotificationConfig.FIELD_MESSAGE_ICON)
		public abstract Builder messageIcon(ValueReference messageIcon);

		@JsonProperty(SlackNotificationConfig.FIELD_FOOTER_TEXT)
		public abstract Builder footerText(ValueReference footerText);

		@JsonProperty(SlackNotificationConfig.FIELD_FOOTER_ICON_URL)
		public abstract Builder footerIconUrl(ValueReference footerIconUrl);

		@JsonProperty(SlackNotificationConfig.FIELD_FOOTER_TS_FIELD)
		public abstract Builder footerTsField(ValueReference footerTsField);

		@JsonProperty(SlackNotificationConfig.FIELD_GRAYLOG_URL)
		public abstract Builder graylogUrl(ValueReference graylogUrl);

		@JsonProperty(SlackNotificationConfig.FIELD_PROXY_ADDRESS)
		public abstract Builder proxyAddress(ValueReference proxyAddress);

		@JsonProperty(SlackNotificationConfig.FIELD_FIELDS)
		public abstract Builder fields(ValueReference fields);

		@JsonProperty(SlackNotificationConfig.FIELD_ACKNOWLEDGE)
		public abstract Builder acknowledge(ValueReference acknowledge);

		@JsonProperty(SlackNotificationConfig.FIELD_PREFORMAT)
		public abstract Builder preformat(ValueReference preformat);

		@JsonProperty(SlackNotificationConfig.FIELD_TOKEN)
		public abstract Builder token(ValueReference token);

		public abstract SlackNotificationConfigEntity build();
	}

	@Override
	public EventNotificationConfig toNativeEntity(Map<String, ValueReference> parameters,
			Map<EntityDescriptor, Object> nativeEntities) {
		return SlackNotificationConfig.builder()
			.webhookUrl(webhookUrl().asString(parameters))
			.channel(channel().asString(parameters))
			.userName(channel().asString(parameters))
			.color(channel().asString(parameters))
			.backlogItems(channel().asInteger(parameters))
			.notifyUsers(channel().asString(parameters))
			.shortMode(channel().asBoolean(parameters))
			.linkNames(channel().asBoolean(parameters))
			.messageIcon(channel().asString(parameters))
			.messageIcon(channel().asString(parameters))
			.footerText(channel().asString(parameters))
			.footerIconUrl(channel().asString(parameters))
			.footerTsField(channel().asString(parameters))
			.graylogUrl(channel().asString(parameters))
			.proxyAddress(channel().asString(parameters))
			.fields(channel().asString(parameters))
			.acknowledge(channel().asBoolean(parameters))
			.preformat(channel().asBoolean(parameters))
			.token(channel().asString(parameters)).build();
	}
}