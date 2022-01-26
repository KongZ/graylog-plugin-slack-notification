// eslint-disable-next-line no-unused-vars
import 'webpack-entry';

import { PluginManifest, PluginStore } from 'graylog-web-plugin/plugin';
import packageJson from '../../package.json';

import SlackNotificationForm from 'form/SlackNotificationForm';
import SlackNotificationSummary from 'form/SlackNotificationSummary';
import SlackNotificationDetails from 'form/SlackNotificationDetails';

const manifest = new PluginManifest(packageJson, {
  eventNotificationTypes: [
    {
      type: 'graylog-plugin-slack-notification',
      displayName: 'Graylog Slack Notification',
      formComponent: SlackNotificationForm,
      summaryComponent: SlackNotificationSummary,
      detailsComponent: SlackNotificationDetails,
      defaultConfig: SlackNotificationForm.defaultConfig
    }
  ]
});
PluginStore.register(manifest);
