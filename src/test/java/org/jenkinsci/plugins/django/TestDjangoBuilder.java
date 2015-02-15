package org.jenkinsci.plugins.django;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import hudson.Plugin;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class TestDjangoBuilder {
	@Rule
	public JenkinsRule jRule = new JenkinsRule();

	@Test
	public void testGlobalConfig() throws Exception {
		Plugin djPlugin = jRule.getPluginManager().getPlugin("django").getPlugin();
		assertThat("Django Plugin sould not be null.", djPlugin, notNullValue());
	}

	@Test
	public void testRoundTrip() throws Exception {
		String projectApps = "items";
		DjangoJenkinsBuilder before = new DjangoJenkinsBuilder(DjangoJenkinsBuilder.DEFAULT_TASKS, projectApps);
		DjangoJenkinsBuilder after = jRule.configRoundtrip(before);
		jRule.assertEqualBeans(before, after, "tasks,projectApps");
	}
}
