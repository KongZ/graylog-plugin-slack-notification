import React from "react";
import PropTypes from "prop-types";
import lodash from "lodash";

import { Input } from "components/bootstrap";
import FormsUtils from "util/FormsUtils";

class SlackNotificationForm extends React.Component {
  static propTypes = {
    config: PropTypes.object.isRequired,
    validation: PropTypes.object.isRequired,
    onChange: PropTypes.func.isRequired
  };

  static defaultConfig = {
    webhookUrl: "",
    color: "#FF0000",
    channel: "#channel",
    userName: "Graylog",
    backlogItems: 1,
    notifyUsers: "",
    shortMode: true,
    linkNames: true,
    messageIcon: "",
    footerText: "${source}",
    footerIconUrl: "",
    footerTsField: "timestamp",
    graylogUrl: "",
    proxyAddress: "",
    fields: "",
    acknowledge: false,
    preformat: false,
    token: ""
  };

  propagateChange = (key, value) => {
    const { config, onChange } = this.props;
    const nextConfig = lodash.cloneDeep(config);
    nextConfig[key] = value;
    onChange(nextConfig);
  };

  handleChange = event => {
    const { name } = event.target;
    this.propagateChange(name, FormsUtils.getValueFromInput(event.target));
  };

  render() {
    const { config, validation } = this.props;

    return (
      <React.Fragment>
        <Input
          id="notification-webhookUrl"
          name="webhookUrl"
          label="Webhook URL"
          type="text"
          bsStyle={validation.errors.webhookUrl ? "error" : null}
          help={lodash.get(
            validation,
            "errors.webhookUrl[0]",
            'Slack "Incoming Webhook" URL. This field is not required when using Slack token.'
          )}
          value={config.webhookUrl || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-token"
          name="token"
          label="Slack Token"
          type="text"
          bsStyle={validation.errors.token ? "error" : null}
          help={lodash.get(
            validation,
            "errors.token[0]",
            'Require if you want to use acknowledge buttons with user notification. Slack do not allow to mention user while using webhook URL.'
          )}
          value={config.token || ""}
          onChange={this.handleChange}
          required
       />
        <Input
          id="notification-userName"
          name="userName"
          label="Sender Name"
          type="text"
          bsStyle={validation.errors.userName ? "error" : null}
          help={lodash.get(
            validation,
            "errors.userName[0]",
            'Set your bot\'s user name in Slack'
          )}
          value={config.userName || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-channel"
          name="channel"
          label="Channel"
          type="text"
          bsStyle={validation.errors.channel ? "error" : null}
          help={lodash.get(
            validation,
            "errors.channel[0]",
            "Name of Slack #channel or @user for a direct message."
          )}
          value={config.channel || ""}
          onChange={this.handleChange}
          required
        />
        <Input
          id="notification-backlogItems"
          name="backlogItems"
          label="Backlog Items"
          type="number"
          bsStyle={validation.errors.backlogItems ? "error" : null}
          help={lodash.get(
            validation,
            "errors.backlogItems[0]",
            "Number of backlog item descriptions to attach. If value is 0, no backlog will be included"
          )}
          value={config.backlogItems || ""}
          inputmode="numeric"
          pattern="[0-9]*" 
          onInput={this.handleChange.bind(this)}
          onChange={this.handleChange}
          required
        />
        <Input
          id="notification-fields"
          name="fields"
          label="Backlog Fields"
          type="text"
          bsStyle={validation.errors.fields ? "error" : null}
          help={lodash.get(
            validation,
            "errors.fields[0]",
            "Add fields from backlog item(s) into alert (field1, field2...)."
          )}
          value={config.fields || ""}
          onChange={this.handleChange}
        />
       <Input
          id="notification-notifyUsers"
          name="notifyUsers"
          label="Notify Users"
          type="text"
          bsStyle={validation.errors.notifyUsers ? "error" : null}
          help={lodash.get(
            validation,
            "errors.notifyUsers[0]",
            "Also notify user in channel by adding @user to the message. You can also use ${field[:-default]} in this text. If acknowledgement is enabled, you need to provide Slack's token too."
          )}
          value={config.notifyUsers || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-color"
          name="color"
          label="Color"
          type="text"
          bsStyle={validation.errors.color ? "error" : null}
          help={lodash.get(
            validation,
            "errors.color[0]",
            "Color to use for Slack message"
          )}
          value={config.color || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-shortMode"
          name="shortMode"
          label="Short Mode"
          type="checkbox"
          bsStyle={validation.errors.shortMode ? "error" : null}
          help={lodash.get(
            validation,
            "errors.shortMode[0]",
            "Enable short mode? This strips down the Slack message to the bare minimum to take less space in the chat room."
          )}
          checked={config.shortMode || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-linkNames"
          name="linkNames"
          label="Link Names"
          type="checkbox"
          bsStyle={validation.errors.linkNames ? "error" : null}
          help={lodash.get(
            validation,
            "errors.linkNames[0]",
            "Find and create links for channel names and user names."
          )}
          checked={config.linkNames || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-messageIcon"
          name="messageIcon"
          label="Message Icon"
          type="text"
          bsStyle={validation.errors.messageIcon ? "error" : null}
          help={lodash.get(
            validation,
            "errors.messageIcon[0]",
            "Set a Slack emoji or an image URL to use as an icon."
          )}
          value={config.messageIcon || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-footerText"
          name="footerText"
          label="Footer Text"
          type="text"
          bsStyle={validation.errors.footerText ? "error" : null}
          help={lodash.get(
            validation,
            "errors.footerText[0]",
            "Add some brief text to help contextualize and identify an attachment. You can also use ${field[:-default]} in this text."
          )}
          value={config.footerText || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-footerIconUrl"
          name="footerIconUrl"
          label="Footer Icon"
          type="text"
          bsStyle={validation.errors.footerIconUrl ? "error" : null}
          help={lodash.get(
            validation,
            "errors.footerIconUrl[0]",
            "Set an image URL to use as a small icon beside your footer text."
          )}
          value={config.footerIconUrl || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-footerTsField"
          name="footerTsField"
          label="Timestamp Field"
          type="text"
          bsStyle={validation.errors.footerTsField ? "error" : null}
          help={lodash.get(
            validation,
            "errors.footerTsField[0]",
            "A timestamp field for displaying a timestamp value as part of the attachment's footer."
          )}
          value={config.footerTsField || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-graylogUrl"
          name="graylogUrl"
          label="Graylog URL"
          type="text"
          bsStyle={validation.errors.graylogUrl ? "error" : null}
          help={lodash.get(
            validation,
            "errors.graylogUrl[0]",
            "URL to your Graylog web interface. Used to build links in alarm notification."
          )}
          value={config.graylogUrl || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-proxyAddress"
          name="proxyAddress"
          label="Proxy Address"
          type="text"
          bsStyle={validation.errors.proxyAddress ? "error" : null}
          help={lodash.get(
            validation,
            "errors.proxyAddress[0]",
            "If proxy requires to access SlackAPI, insert the proxy information in the following format: <ProxyAddress>:<Port>"
          )}
          value={config.proxyAddress || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-acknowledge"
          name="acknowledge"
          label="Add acknowledge button"
          type="checkbox"
          bsStyle={validation.errors.acknowledge ? "error" : null}
          help={lodash.get(
            validation,
            "errors.acknowledge[0]",
            "Include acknowledge buttons in alert message. This feature require either webhook URL from Slack app or Slack token. (Recommend Slack token)"
          )}
          checked={config.acknowledge || ""}
          onChange={this.handleChange}
        />
        <Input
          id="notification-preformat"
          name="preformat"
          label="Use pre-formatted text"
          type="checkbox"
          bsStyle={validation.errors.preformat ? "error" : null}
          help={lodash.get(
            validation,
            "Use pre-formatted text",
            "Create a block of pre-formatted, fixed-width text on backlog items"
          )}
          checked={config.preformat || ""}
          onChange={this.handleChange}
        />
      </React.Fragment>
    );
  }
}

export default SlackNotificationForm;
