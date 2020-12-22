Slack Plugin for Graylog
========================
This plugin is for Graylog 3.1 and above. If you are looking for older Graylog plugin, please checkout https://github.com/KongZ/graylog-plugin-slack

Note: The plugin ownership was transferred from https://github.com/omise to https://github.com/KongZ
## Features

### Notification
Send notification messages to Slack when alert was raised. 

The screenshot below shows a sample of Slack notification.

![](https://raw.githubusercontent.com/KongZ/graylog-plugin-slack-notification/master/screenshot_acknowledged.png)

* Send message directly to user or channel
* Support Slack attachment short mode
* Mention users or channels when alert. Users can be mentioned by field variables
* Provide link back to event times
* Support event timestamp in footer text
* Support proxy
* Support custom fields in Slack attachment
* Support acknowledge buttons. Required Slack app's token
* Support pre-formatted text in backlog item
* Support event definition fields
* Support JMTE in backlog fields and footer

The screenshot below shows a pre-formatted text with acknowledgement buttons

![](https://raw.githubusercontent.com/KongZ/graylog-plugin-slack-notification/master/screenshot_preformat.png)

#### Mention users or channels when alert
This feature requires Slack Token. The webhook does not allow to tag or mention users. 
To setup a Slack App and get Token, please see https://api.slack.com/slack-apps
You need the following permissions
 - `users.read` for lookup Slack user ID from display name
 - `chat:write`,`chat:write:user`, `chat:write:bot` for sending message

#### Acknowledgment buttons
The acknowledgment buttons also requires Slack Token. You cannot use Slack Incoming Webhook to creates buttons. See [Slack Interactive Message](https://api.slack.com/interactive-messages) for detail of Slack API.

The acknowledge button requires to set the interactive request URL to `https://{graylog.uri}/api/plugins/com.kongz.graylog.plugins.slack/action`
Make sure you set the Action button to this URI correctly and no firewall blocking `POST` request

![](https://raw.githubusercontent.com/KongZ/graylog-plugin-slack-notification/master/screenshot_slack_callback_url.png)

The screenshot below shows an acknowledgement buttons

![](https://raw.githubusercontent.com/KongZ/graylog-plugin-slack-notification/master/screenshot_acknowledgement.png)

The screenshot below shows a result of acknowledged

![](https://raw.githubusercontent.com/KongZ/graylog-plugin-slack-notification/master/screenshot_acknowledged.png)


## Installation 
1. You can [Download the plugin](https://github.com/KongZ/graylog-plugin-slack-notification/releases) and place the `.jar` file in your Graylog plugin directory. The plugin directory
is the `plugins/` folder relative from your `graylog-server` directory by default and can be configured in your `graylog.conf` file.

2. Remove all previous version of Graylog plugin Slack `.jar` files. in `plugins` directory.

3. Restart a graylog server. Plugin will automatically migrate all your configured data to a new version.

## Usage

### For Slack:

#### Step 1: Create Slack Incoming Webhook (If you just want to send only message)

Create a new Slack Incoming Webhook (`https://<organization>.slack.com/services/new/incoming-webhook`) and copy the URL it will present to you. It will ask you to select a Slack channel but you can override it in the plugin configuration later.

#### Step 2: Create Slack App (If you want to mention someone when send notifications to Slack or use interactive buttons)
Create a new Slack App https://api.slack.com/apps?new_app=1 and copy the Slack Token into plugin configuration.

The screenshot below shows a sample configuration screen.
The sample here use extractor to extract and create fields for SSH login name (`ssh_login_from`) and IP (`ssh_login_ip`) from logs. 
The configure Notify User to mention people who SSH to the server with Acknowledge button.

![](https://raw.githubusercontent.com/KongZ/graylog-plugin-slack-notification/master/screenshot_configuration.png)

## Troubleshooting

### HTTPS connection fails

If the Java runtime environment and the included SSL certificate trust store is too old, HTTPS connections to Slack might fail with the following error message:

```text
Caused by: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```

In this case, add the Slack SSL certificate manually to Java's trust store similar to the process described in the [Graylog documentation](http://docs.graylog.org/en/2.1/pages/configuration/https.html#adding-a-self-signed-certificate-to-the-jvm-trust-store).

### Error occurred when clicks a Slack button.
Slack App required HTTPS connection between your Graylog server and Slack server. You need to configure your Graylog server with a valid certificate file. Make sure your Graylog server open a firewall allow Slack server to communicate with. 

Most common errors and describe will be send to you by Slack's bot when you click a button. If trouble persists, you can turn a `debug` log on Graylog server to see more detail on log files. 

Please see more information on Slack aps here https://api.slack.com/slack-apps

## Build

This project is using Maven and requires Java 8 or higher.

You can build a plugin (JAR) with `mvn package`.
