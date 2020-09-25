package co.omise.graylog.plugins.slack;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlackClient {

  private static final Logger LOG = LoggerFactory.getLogger(SlackClient.class);

  private final String webhookUrl;
  private final String proxyURL;
  private final String slackToken;

  public SlackClient(SlackNotificationConfig configuration) {
    this.webhookUrl = configuration.webhookUrl();
    this.proxyURL = configuration.proxyAddress();
    this.slackToken = configuration.token();
  }

  public void send(SlackMessage message) throws SlackClientException {
    final HttpURLConnection conn;

    // If `token` is provided, we will use Slack API methods; otherwise Slack's webhook will be used
    if (isNullOrEmpty(slackToken)) {
      // POST to Slack webhook
      final URL url;
      try {
        url = new URL(webhookUrl);
      } catch (MalformedURLException e) {
        throw new SlackClientException("Error while constructing webhook URL.", e);
      }
      try {
        if (!StringUtils.isEmpty(proxyURL)) {
          final URI proxyUri = new URI(proxyURL);
          InetSocketAddress sockAddress =
              new InetSocketAddress(proxyUri.getHost(), proxyUri.getPort());
          final Proxy proxy = new Proxy(Proxy.Type.HTTP, sockAddress);
          conn = (HttpURLConnection) url.openConnection(proxy);
        } else {
          conn = (HttpURLConnection) url.openConnection();
        }
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
      } catch (URISyntaxException | IOException e) {
        throw new SlackClientException("Could not open connection to Slack API", e);
      }
      try (final Writer writer = new OutputStreamWriter(conn.getOutputStream())) {
        String payload = message.getJsonString();
        if (LOG.isTraceEnabled()) LOG.trace("{}", payload);
        writer.write(payload);
        writer.flush();

        final int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
          if (LOG.isDebugEnabled()) {
            try (final InputStream responseStream = conn.getInputStream()) {
              final byte[] responseBytes = ByteStreams.toByteArray(responseStream);
              final String response = new String(responseBytes, Charsets.UTF_8);
              LOG.debug("Received HTTP response body:\n{}", response);
            }
          }
          throw new SlackClientException("Unexpected HTTP response status " + responseCode);
        }
      } catch (IOException e) {
        throw new SlackClientException("Could not POST to Slack API", e);
      }
    } else {
      // GET to Slack API
      final URL url;
      try {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder
            .append("https://slack.com/api/chat.postMessage?token=")
            .append(slackToken)
            .append('&')
            .append(message.getRequestString());
        if (LOG.isTraceEnabled()) LOG.trace("{}", urlBuilder);
        url = new URL(urlBuilder.toString());
      } catch (MalformedURLException e) {
        throw new SlackClientException("Error while constructing webhook URL.", e);
      }
      try {
        if (!StringUtils.isEmpty(proxyURL)) {
          final URI proxyUri = new URI(proxyURL);
          InetSocketAddress sockAddress =
              new InetSocketAddress(proxyUri.getHost(), proxyUri.getPort());
          final Proxy proxy = new Proxy(Proxy.Type.HTTP, sockAddress);
          conn = (HttpURLConnection) url.openConnection(proxy);
        } else {
          conn = (HttpURLConnection) url.openConnection();
        }
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
      } catch (URISyntaxException | IOException e) {
        throw new SlackClientException("Could not open connection to Slack API", e);
      }
    }
    // Parse response from Slack
    try {
      final int responseCode = conn.getResponseCode();
      if (responseCode != 200) {
        if (LOG.isDebugEnabled()) {
          try (final InputStream responseStream = conn.getInputStream()) {
            final byte[] responseBytes = ByteStreams.toByteArray(responseStream);
            final String response = new String(responseBytes, Charsets.UTF_8);
            LOG.debug("Received HTTP response body:\n{}", response);
          }
        }
        throw new SlackClientException("Unexpected HTTP response status " + responseCode);
      }
    } catch (IOException e) {
      throw new SlackClientException("Could not POST to Slack API", e);
    }
  }

  public class SlackClientException extends Exception {
    private static final long serialVersionUID = 4148723128396736l;

    public SlackClientException(String msg) {
      super(msg);
    }

    public SlackClientException(String msg, Throwable cause) {
      super(msg, cause);
    }
  }
}
