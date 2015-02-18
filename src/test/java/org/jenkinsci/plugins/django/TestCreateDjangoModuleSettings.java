package org.jenkinsci.plugins.django;

import static org.junit.Assert.*;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.util.EnumSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class TestCreateDjangoModuleSettings {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private VirtualChannel channel;

	private CreateDjangoModuleSettings cdms;


	@Before
	public void setUp() throws Exception {
		String projectApps = "items";
		EnumSet<Task> actualTasks = EnumSet.allOf(Task.class);
		String settingsModule = "items.settings";
		cdms = new CreateDjangoModuleSettings(settingsModule, actualTasks, projectApps);
	}

	@Test
	public void testCreateDjangoModuleSettings() throws Exception {
		assertSame(CreateDjangoModuleSettings.class, cdms.getClass());
	}

	@Test
	public void testInvoke() throws Exception {
		File file = folder.newFolder();
		assertTrue("Django Settings Module not created.",cdms.invoke(file, channel));
		assertFalse("Django Settings Module should have failed",cdms.invoke(new File("/dev/null"), channel));
	}
}
