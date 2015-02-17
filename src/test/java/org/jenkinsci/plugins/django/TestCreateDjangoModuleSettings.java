package org.jenkinsci.plugins.django;

import static org.junit.Assert.assertSame;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.PrintStream;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class TestCreateDjangoModuleSettings {
	@Rule
	public MockitoRule rule = MockitoJUnit.rule();
	@Mock
	private PrintStream logger;
	@Mock
	private VirtualChannel channel;

	private CreateDjangoModuleSettings cdms;


	@Before
	public void setUp() throws Exception {
		String projectApps = "items";
		EnumSet<Task> actualTasks = EnumSet.allOf(Task.class);
		String settingsModule = "items.settings";
		cdms = new CreateDjangoModuleSettings(logger, settingsModule, actualTasks, projectApps);
	}

	@Test
	public void testCreateDjangoModuleSettings() throws Exception {
		assertSame(CreateDjangoModuleSettings.class, cdms.getClass());
	}

	@Test
	public void testInvoke() throws Exception {
		File file = new File(System.getProperty("java.io.tmpdir"));
		cdms.invoke(file, channel);
	}
}
