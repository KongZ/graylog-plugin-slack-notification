package com.kongz.graylog.plugins.slack;

import javax.validation.constraints.NotBlank;

import org.graylog.events.contentpack.entities.EventNotificationConfigEntity;
import org.graylog.events.event.EventDto;
import org.graylog.events.notifications.EventNotificationConfig;
import org.graylog.events.notifications.EventNotificationExecutionJob;
import org.graylog.scheduler.JobTriggerData;
import org.graylog2.contentpacks.EntityDescriptorIds;
import org.graylog2.contentpacks.model.entities.references.ValueReference;
import org.graylog2.plugin.rest.ValidationResult;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonTypeName(SlackNotificationConfig.TYPE_NAME)
@JsonDeserialize(builder = SlackNotificationConfig.Builder.class)
public abstract class SlackNotificationConfig implements EventNotificationConfig {
	public static final String TYPE_NAME = "slack-notification-v1";
	public static final String FIELD_WEBHOOK_URL = "webhookUrl";
	public static final String FIELD_CHANNEL = "channel";
	public static final String FIELD_USER_NAME = "userName";
	public static final String FIELD_COLOR = "color";
	public static final String FIELD_BACKLOG_ITEMS = "backlogItems";
	public static final String FIELD_SHORT_MODE = "shortMode";
	public static final String FIELD_NOTIFY_USERS = "notifyUsers";
	public static final String FIELD_LINK_NAMES = "linkNames";
	public static final String FIELD_MESSAGE_ICON = "messageIcon";
	public static final String FIELD_FOOTER_TEXT = "footerText";
	public static final String FIELD_FOOTER_ICON_URL = "footerIconUrl";
	public static final String FIELD_FOOTER_TS_FIELD = "footerTsField";
	public static final String FIELD_GRAYLOG_URL = "graylogUrl";
	public static final String FIELD_PROXY_ADDRESS = "proxyAddress";
	public static final String FIELD_FIELDS = "fields";
	public static final String FIELD_ACKNOWLEDGE = "acknowledge";
	public static final String FIELD_PREFORMAT = "preformat";
	public static final String FIELD_TOKEN = "token";

	@JsonProperty(FIELD_WEBHOOK_URL)
	public abstract String webhookUrl();

	@JsonProperty(FIELD_CHANNEL)
	@NotBlank
	public abstract String channel();

	@JsonProperty(FIELD_USER_NAME)
	public abstract String userName();

	@JsonProperty(FIELD_COLOR)
	public abstract String color();

	@JsonProperty(FIELD_BACKLOG_ITEMS)
	public abstract int backlogItems();

	@JsonProperty(FIELD_SHORT_MODE)
	@NotBlank
	public abstract boolean shortMode();

	@JsonProperty(FIELD_NOTIFY_USERS)
	public abstract String notifyUsers();

	@JsonProperty(FIELD_LINK_NAMES)
	public abstract boolean linkNames();

	@JsonProperty(FIELD_MESSAGE_ICON)
	public abstract String messageIcon();

	@JsonProperty(FIELD_FOOTER_TEXT)
	public abstract String footerText();

	@JsonProperty(FIELD_FOOTER_ICON_URL)
	public abstract String footerIconUrl();

	@JsonProperty(FIELD_FOOTER_TS_FIELD)
	public abstract String footerTsField();

	@JsonProperty(FIELD_GRAYLOG_URL)
	@NotBlank
	public abstract String graylogUrl();

	@JsonProperty(FIELD_PROXY_ADDRESS)
	public abstract String proxyAddress();

	@JsonProperty(FIELD_FIELDS)
	public abstract String fields();

	@JsonProperty(FIELD_ACKNOWLEDGE)
	public abstract boolean acknowledge();

	@JsonProperty(FIELD_PREFORMAT)
	public abstract boolean preformat();

	@JsonProperty(FIELD_TOKEN)
	public abstract String token();

	@Override
	@JsonIgnore
	public JobTriggerData toJobTriggerData(EventDto dto) {
		return EventNotificationExecutionJob.Data.builder().eventDto(dto).build();
	}

	public static SlackNotificationConfig.Builder builder() {
		return SlackNotificationConfig.Builder.create();
	}

	@Override
	@JsonIgnore
	public ValidationResult validate() {
		return new ValidationResult();
	}

	@AutoValue.Builder
	public static abstract class Builder implements EventNotificationConfig.Builder<SlackNotificationConfig.Builder> {
		@JsonCreator
		public static SlackNotificationConfig.Builder create() {
			return new AutoValue_SlackNotificationConfig.Builder().type(TYPE_NAME);
		}

		@JsonProperty(FIELD_WEBHOOK_URL)
		public abstract SlackNotificationConfig.Builder webhookUrl(String webhookUrl);

		@JsonProperty(FIELD_CHANNEL)
		public abstract SlackNotificationConfig.Builder channel(String channel);

		@JsonProperty(FIELD_USER_NAME)
		public abstract SlackNotificationConfig.Builder userName(String userName);

		@JsonProperty(FIELD_COLOR)
		public abstract SlackNotificationConfig.Builder color(String color);

		@JsonProperty(FIELD_BACKLOG_ITEMS)
		public abstract SlackNotificationConfig.Builder backlogItems(int backlogItems);

		@JsonProperty(FIELD_NOTIFY_USERS)
		public abstract SlackNotificationConfig.Builder notifyUsers(String notifyUsers);

		@JsonProperty(FIELD_SHORT_MODE)
		public abstract SlackNotificationConfig.Builder shortMode(boolean shortMode);

		@JsonProperty(FIELD_LINK_NAMES)
		public abstract SlackNotificationConfig.Builder linkNames(boolean linkNames);

		@JsonProperty(FIELD_MESSAGE_ICON)
		public abstract SlackNotificationConfig.Builder messageIcon(String messageIcon);

		@JsonProperty(FIELD_FOOTER_TEXT)
		public abstract SlackNotificationConfig.Builder footerText(String footerText);

		@JsonProperty(FIELD_FOOTER_ICON_URL)
		public abstract SlackNotificationConfig.Builder footerIconUrl(String footerIconUrl);

		@JsonProperty(FIELD_FOOTER_TS_FIELD)
		public abstract SlackNotificationConfig.Builder footerTsField(String footerTsField);

		@JsonProperty(FIELD_GRAYLOG_URL)
		public abstract SlackNotificationConfig.Builder graylogUrl(String graylogUrl);

		@JsonProperty(FIELD_PROXY_ADDRESS)
		public abstract SlackNotificationConfig.Builder proxyAddress(String proxyAddress);

		@JsonProperty(FIELD_FIELDS)
		public abstract SlackNotificationConfig.Builder fields(String fields);

		@JsonProperty(FIELD_ACKNOWLEDGE)
		public abstract SlackNotificationConfig.Builder acknowledge(boolean acknowledge);

		@JsonProperty(FIELD_PREFORMAT)
		public abstract SlackNotificationConfig.Builder preformat(boolean preformat);

		@JsonProperty(FIELD_TOKEN)
		public abstract SlackNotificationConfig.Builder token(String token);

		public abstract SlackNotificationConfig build();
	}

	@Override
	public EventNotificationConfigEntity toContentPackEntity(EntityDescriptorIds entityDescriptorIds) {
		return SlackNotificationConfigEntity.builder()
			.webhookUrl(ValueReference.of(webhookUrl()))
			.channel(ValueReference.of(channel()))
			.userName(ValueReference.of(userName()))
			.color(ValueReference.of(color()))
			.backlogItems(ValueReference.of(backlogItems()))
			.notifyUsers(ValueReference.of(notifyUsers()))
			.shortMode(ValueReference.of(shortMode()))
			.linkNames(ValueReference.of(linkNames()))
			.messageIcon(ValueReference.of(messageIcon()))
			.footerText(ValueReference.of(footerText()))
			.footerIconUrl(ValueReference.of(footerIconUrl()))
			.footerTsField(ValueReference.of(footerTsField()))
			.graylogUrl(ValueReference.of(graylogUrl()))
			.proxyAddress(ValueReference.of(proxyAddress()))
			.fields(ValueReference.of(fields()))
			.acknowledge(ValueReference.of(acknowledge()))
			.preformat(ValueReference.of(preformat()))
			.token(ValueReference.of(token()))
			.build();
	}
}