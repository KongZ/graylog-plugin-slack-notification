import React from 'react';
import PropTypes from 'prop-types';

import CommonNotificationSummary from './CommonNotificationSummary';

class SlackNotificationSummary extends React.Component {
  static propTypes = {
    type: PropTypes.string.isRequired,
    notification: PropTypes.object,
    definitionNotification: PropTypes.object.isRequired,
  };

  static defaultProps = {
    notification: {},
  };

  render() {
    const { notification } = this.props;
    return (
      <CommonNotificationSummary {...this.props}>
        <React.Fragment>
          <tr>
            <td>Webhook URL</td>
            <td>{notification.config.webhookUrl}</td>
          </tr>
          <tr>
            <td>Slack Token</td>
            <td>{notification.config.token}</td>
          </tr>
          <tr>
            <td>Sender Name</td>
            <td>{notification.config.userName}</td>
          </tr>
          <tr>
            <td>Channel</td>
            <td>{notification.config.channel}</td>
          </tr>
          <tr>
            <td>Backlog Items</td>
            <td>{notification.config.backlogItems}</td>
          </tr>
          <tr>
            <td>Backlog Fields</td>
            <td>{notification.config.fields}</td>
          </tr>
          <tr>
            <td>Notify Users</td>
            <td>{notification.config.notifyUsers}</td>
          </tr>
          <tr>
            <td>Color</td>
            <td>{notification.config.color}</td>
          </tr>
          <tr>
            <td>Short Mode</td>
            <td>{notification.config.shortMode}</td>
          </tr>
          <tr>
            <td>Link Names</td>
            <td>{notification.config.linkNames}</td>
          </tr>
          <tr>
            <td>Message Icon</td>
            <td>{notification.config.messageIcon}</td>
          </tr>
          <tr>
            <td>Footer Text</td>
            <td>{notification.config.footerText}</td>
          </tr>
          <tr>
            <td>Footer Icon</td>
            <td>{notification.config.footerIconUrl}</td>
          </tr>
          <tr>
            <td>Timestamp Field</td>
            <td>{notification.config.footerTsField}</td>
          </tr>
          <tr>
            <td>Graylog URL</td>
            <td>{notification.config.graylogUrl}</td>
          </tr>
          <tr>
            <td>Proxy Address</td>
            <td>{notification.config.proxyAddress}</td>
          </tr>
          <tr>
            <td>Add acknowledge button</td>
            <td>{notification.config.acknowledge}</td>
          </tr>
          <tr>
            <td>Use pre-formatted text</td>
            <td>{notification.config.preformat}</td>
          </tr>
        </React.Fragment>
      </CommonNotificationSummary>
    );
  }
}

export default SlackNotificationSummary;