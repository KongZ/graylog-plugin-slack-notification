// eslint-disable-next-line no-unused-vars
import 'webpack-entry';

import packageJson from '../../package.json';
import { PluginManifest, PluginStore } from 'graylog-web-plugin/plugin';

import GraylogSlackNotificationSummary from './form/GraylogSlackNotificationSummary';
import GraylogSlackNotificationDetails from './form/GraylogSlackNotificationDetails';
import GraylogSlackNotificationForm from './form/GraylogSlackNotificationForm';

PluginStore.register(new PluginManifest({packageJson}, {
  eventNotificationTypes: [
    {
      type: 'graylog-plugin-slack-notification',
      displayName: 'Graylog Slack Notification',
      formComponent: GraylogSlackNotificationForm,
      summaryComponent: GraylogSlackNotificationSummary,
      detailsComponent: GraylogSlackNotificationDetails,
      defaultConfig: GraylogSlackNotificationForm.defaultConfig
    }
  ]
}));