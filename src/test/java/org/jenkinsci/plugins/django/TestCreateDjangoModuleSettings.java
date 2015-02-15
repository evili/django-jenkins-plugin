package org.jenkinsci.plugins.django;

import static org.junit.Assert.assertSame;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.PrintStream;
import java.util.EnumSet;

import org.jenkinsci.remoting.RoleChecker;
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
	private CreateDjangoModuleSettings mockCDMS;
	@Mock
	private PrintStream logger;
	@Mock
	private RoleChecker checker;
	@Mock
	private File file;
	@Mock
	private VirtualChannel channel;

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreateDjangoModuleSettings() throws Exception {
		String projectApps = "items";
		EnumSet<Task> actualTasks = EnumSet.allOf(Task.class);
		String settingsModule = "items.settings";
		CreateDjangoModuleSettings cdms = new CreateDjangoModuleSettings(logger, settingsModule, actualTasks, projectApps);
		assertSame(CreateDjangoModuleSettings.class, cdms.getClass());
	}

	@Test
	public void testCheckRoles() throws Exception {
		mockCDMS.checkRoles(checker);
	}

	@Test
	public void testInvoke() throws Exception {
		mockCDMS.invoke(file, channel);
	}

}
