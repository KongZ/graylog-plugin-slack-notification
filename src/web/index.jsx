// eslint-disable-next-line no-unused-vars
import webpackEntry from 'webpack-entry';
import { PluginManifest, PluginStore } from 'graylog-web-plugin/plugin';
import packageJson from '../../package.json';

import SlackNotificationForm from 'form/SlackNotificationForm';
import SlackNotificationSummary from 'form/SlackNotificationSummary';

const manifest = new PluginManifest(packageJson, {
  eventNotificationTypes: [
    {
      type: 'slack-notification-v1',
      displayName: 'Slack Notification',
      formComponent: SlackNotificationForm,
      summaryComponent: SlackNotificationSummary,
      defaultConfig: SlackNotificationForm.defaultConfig
    }
  ]
});
PluginStore.register(manifest);
