const { PluginWebpackConfig } = require('graylog-web-plugin');
const { loadBuildConfig } = require('graylog-web-plugin');
const path = require('path');
const buildConfig = loadBuildConfig(path.resolve(__dirname, './build.config'));

// Remember to use the same name here and in `getUniqueId()` in the java MetaData class
module.exports = new PluginWebpackConfig(__dirname, 'com.kongz.graylog.plugins.slack.SlackNotificationPlugin', buildConfig, {
  // Here goes your additional webpack configuration.
});