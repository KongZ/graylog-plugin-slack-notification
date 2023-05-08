package com.kongz.graylog.plugins.slack;

import java.util.Collection;
import java.util.Collections;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

public class SlackNotificationPlugin implements Plugin {
	@Override
	public Collection<PluginModule> modules () {
		return Collections.<PluginModule>singletonList(new SlackNotificationPluginModule());
	}

	@Override
	public PluginMetaData metadata() {
		return new SlackNotificationPluginMetaData();
	}

}