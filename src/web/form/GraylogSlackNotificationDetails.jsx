import * as React from 'react';
import PropTypes from 'prop-types';

import { ReadOnlyFormGroup } from 'components/common';

const GraylogSlackNotificationDetails = ({ notification }) => {
  return (
    <>
      <ReadOnlyFormGroup label="Sender Name" value={notification.config.userName} />
      <ReadOnlyFormGroup label="Channel" value={notification.config.channel} />
      <ReadOnlyFormGroup label="Notify Users" value={notification.config.notifyUsers} />
      <ReadOnlyFormGroup label="Acknowledge" value={notification.config.acknowledge} />
    </>
  );
};

GraylogSlackNotificationDetails.propTypes = {
  notification: PropTypes.object.isRequired,
};

export default GraylogSlackNotificationDetails;