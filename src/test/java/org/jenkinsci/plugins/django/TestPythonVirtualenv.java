package org.jenkinsci.plugins.django;

import static org.junit.Assert.assertNotNull;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.util.EnumSet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class TestPythonVirtualenv {
	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private AbstractBuild<?, ?> build;

	@Mock
	private Launcher launcher;

	@Mock
	private BuildListener listener;

	private PythonVirtualenv venv;


	@Before
	public void setUp() {
		venv = new PythonVirtualenv(build, launcher, listener);
	}

	@Test
	public void testPythonVirtualenv() throws Exception {
		assertNotNull("We've got a null PythonVirtualenv", venv);
	}

	@Test
	public void testPerform() throws Exception {
		String projectApps = "items";
		EnumSet<Task> actualTasks = EnumSet.allOf(Task.class);
		venv.perform(actualTasks, projectApps);
		assertNotNull("We've got a null PythonVirtualenv", venv);
	}

}
