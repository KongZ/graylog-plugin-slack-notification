package com.kongz.graylog.plugins.slack;

import org.graylog2.plugin.PluginModule;

public class SlackNotificationPluginModule extends PluginModule {

   @Override
   protected void configure() {
      addNotificationType(
         SlackNotificationConfig.TYPE_NAME, 
         SlackNotificationConfig.class, 
         SlackNotification.class,
         SlackNotification.Factory.class,
         SlackNotificationConfigEntity.TYPE_NAME,
         SlackNotificationConfigEntity.class);
      addRestResource(SlackActionCallback.class);
   }
}