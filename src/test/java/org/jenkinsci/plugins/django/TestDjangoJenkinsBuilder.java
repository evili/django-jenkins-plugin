package org.jenkinsci.plugins.django;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import hudson.Plugin;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;
import org.mockito.Mock;


public class TestDjangoJenkinsBuilder {

	@Rule
	public JenkinsRule jRule = new JenkinsRule();

	private String projectApps;

	@Mock
	private StaplerRequest staplerRequest;

	public DjangoJenkinsBuilder getBuilder() {
		return new DjangoJenkinsBuilder(DjangoJenkinsBuilder.DEFAULT_TASKS, projectApps);
	}

	public void checkDefaultTasks(DjangoJenkinsBuilder builder, EnumSet<Task> defaults) throws Exception {
		DjangoJenkinsBuilder.DescriptorImpl desc = (DjangoJenkinsBuilder.DescriptorImpl)builder.getDescriptor();
		EnumSet<Task> tasks = desc.getDefaultTasks();
		assertEquals("Default tasks should be equal.", defaults, tasks);
	}

	@Before
	public void setUp() {
		projectApps = "items";
	}

	@Test
	public void testGlobalConfig() throws Exception {
		Plugin djPlugin = jRule.getPluginManager().getPlugin("django").getPlugin();
		assertThat("Django Plugin sould not be null.", djPlugin, notNullValue());
	}

	@Test
	public void testRoundTrip() throws Exception {
		DjangoJenkinsBuilder before = getBuilder();
		DjangoJenkinsBuilder after = jRule.configRoundtrip(before);
		jRule.assertEqualBeans(before, after, "tasks,projectApps");
	}

	@Test
	public void testConfigure() throws Exception {
		Map<String, Boolean> taskMap = new HashMap<String, Boolean>();
		EnumSet<Task> changedDefaults = EnumSet.complementOf(DjangoJenkinsBuilder.DEFAULT_TASKS);
		for(Task t: changedDefaults) {
			taskMap.put(t.getName(), true);
		}
		JSONObject json = new JSONObject();
		json.put("defaultTasks", JSONObject.fromObject(taskMap));
		DjangoJenkinsBuilder builder = getBuilder();

		assertTrue("Global config has failed", builder.getDescriptor().configure(staplerRequest, json));
		checkDefaultTasks(builder , changedDefaults);
	}

	@Test
	public void testGetDefaultTasks() throws Exception {
		checkDefaultTasks(getBuilder(), DjangoJenkinsBuilder.DEFAULT_TASKS);
	}
}
