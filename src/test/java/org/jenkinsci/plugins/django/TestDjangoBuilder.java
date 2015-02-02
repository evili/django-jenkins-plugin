package org.jenkinsci.plugins.django;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class TestDjangoBuilder {
	@Rule
	public JenkinsRule jRule = new JenkinsRule();

	private DjangoJenkinsBuilder djangoBuilder;

	private final static String NO_TASK = "nonsensetask";

	@Test
	public void testRoundTrip() throws Exception {
		DjangoJenkinsBuilder before = new DjangoJenkinsBuilder(NO_TASK);
		DjangoJenkinsBuilder after = jRule.configRoundtrip(before);
		jRule.assertEqualBeans(before, after, "tasks");
	}
	
    @Test
	public void testPluginLoads() throws Exception {
        FreeStyleProject project = jRule.createFreeStyleProject();
		djangoBuilder = new DjangoJenkinsBuilder(NO_TASK);
		project.getBuildersList().add(djangoBuilder);
		FreeStyleBuild build = project.scheduleBuild2(0).get();
		String s = FileUtils.readFileToString(build.getLogFile());
		assertThat(s, containsString(NO_TASK));
	}
}
