package com.kongz.graylog.plugins.slack;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.concurrent.TimeUnit;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Cache;
import com.google.common.io.ByteStreams;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlackClient {

  private static final Logger LOG = LoggerFactory.getLogger(SlackClient.class);

  private final String webhookUrl;
  private final String proxyURL;
  private final String slackToken;
  private ObjectMapper objectMapper = new ObjectMapper();
  private static Cache<String, String> slackUserCache;

  static {
    slackUserCache = CacheBuilder.newBuilder()
      .maximumSize(1000)
      .expireAfterWrite(1, TimeUnit.HOURS)
      .build();
  }

  public SlackClient(SlackNotificationConfig configuration) {
    this.webhookUrl = configuration.webhookUrl();
    this.proxyURL = configuration.proxyAddress();
    this.slackToken = configuration.token();
  }

  private String postSlackApi(URL url, String payload) throws SlackClientException {
    HttpURLConnection conn = null;
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
      conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      if (!isNullOrEmpty(slackToken)) {
        conn.setRequestProperty("Authorization", "Bearer " + slackToken);
      }
    } catch (URISyntaxException | IOException e) {
      throw new SlackClientException("Could not open connection to Slack API", e);
    }
    try (final Writer writer = new OutputStreamWriter(conn.getOutputStream(), Charsets.UTF_8)) {
      if (LOG.isTraceEnabled()) LOG.trace("{}", payload);
      writer.write(payload);
      writer.flush();
    } catch (IOException e) {
      throw new SlackClientException("Could not POST to Slack API", e);
    }
    // Parse response from Slack
    try (final InputStream responseStream = conn.getInputStream()) {
      final byte[] responseBytes = ByteStreams.toByteArray(responseStream);
      String response = new String(responseBytes, Charsets.UTF_8);
      LOG.debug("Received HTTP response body:\n{}", response);
      int responseCode = conn.getResponseCode();
      if (responseCode != 200) {
        throw new SlackClientException("Unexpected HTTP response status " + responseCode);
      }
      return response;
    } catch (IOException e) {
      throw new SlackClientException("Could not POST to Slack API", e);
    }
  }

  public String getSlackUser(String key) throws SlackClientException {
    if (isNullOrEmpty(this.slackToken)) {
      return key;
    }
    String id = slackUserCache.getIfPresent(key);
    if (id == null) {
      try {
        String response = postSlackApi(new URL("https://slack.com/api/users.list"), "");
        SlackUserList userList = objectMapper.readValue(response, SlackUserList.class);
        for (SlackMember member : userList.members) {
          if (!member.isBot) {
            slackUserCache.put(member.profile.displayName, member.id);
          }
        }
      } catch (IOException e) {
        LOG.error(e.getMessage(), e);
        throw new SlackClientException("Error while reading Slack users list", e);
      }
      id = slackUserCache.getIfPresent(key);
    }
    return id == null ? key : id;
  }

  public void send(SlackMessage message) throws SlackClientException {
    final URL url;
    try {
      if (isNullOrEmpty(slackToken)) {
        url = new URL(webhookUrl);
      } else {
        url = new URL("https://slack.com/api/chat.postMessage");
      }
    } catch (MalformedURLException e) {
      throw new SlackClientException("Error while constructing webhook URL.", e);
    }
    try {
      postSlackApi(url, message.getJsonString());
    } catch (IOException e) {
      throw new SlackClientException("Error while sending message to Slack", e);
    }
  }

  public static class SlackClientException extends Exception {
    private static final long serialVersionUID = 4148723128396736l;

    public SlackClientException(String msg) {
      super(msg);
    }

    public SlackClientException(String msg, Throwable cause) {
      super(msg, cause);
    }
  }
}
