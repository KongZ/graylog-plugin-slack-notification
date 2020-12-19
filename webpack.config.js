const PluginWebpackConfig = require('graylog-web-plugin').PluginWebpackConfig;
const loadBuildConfig = require('graylog-web-plugin').loadBuildConfig;
const path = require('path');

module.exports = new PluginWebpackConfig('com.kongz.graylog.plugins.slack.SlackNotificationPlugin', loadBuildConfig(path.resolve(__dirname, './build.config')), {
  // Here goes your additional webpack configuration.
});
