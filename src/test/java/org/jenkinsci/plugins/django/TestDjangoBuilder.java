package org.jenkinsci.plugins.django;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import hudson.Plugin;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.scm.SCM;

import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.jenkinsci.plugins.git
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;


public class TestDjangoBuilder {
	@Rule
	public JenkinsRule jRule = new JenkinsRule();

	private DjangoJenkinsBuilder djangoBuilder;

	private final static String NON_SENSE = "nonsensetask";
	
	private final static ArrayList<String> NO_TASKS = new ArrayList<String>();
	static {
		NO_TASKS.add(NON_SENSE);
	}
	
	private final static ArrayList<String> DEFAULT_TASKS = new ArrayList<String>();
	static {
		DEFAULT_TASKS.add("pep8");
	}
	
	@Test
	public void testGlobalConfig() throws Exception {
		Plugin djPlugin = jRule.getPluginManager().getPlugin("django").getPlugin();
		assertThat("Django Plugin sould not be null.", djPlugin, notNullValue());
	}
	
	@Test
	public void testRoundTrip() throws Exception {
		DjangoJenkinsBuilder before = new DjangoJenkinsBuilder(NO_TASKS);
		DjangoJenkinsBuilder after = jRule.configRoundtrip(before);
		jRule.assertEqualBeans(before, after, "tasks");
	}
	
    @Test
	public void testPluginLoads() throws Exception {
        FreeStyleProject project = jRule.createFreeStyleProject();
		djangoBuilder = new DjangoJenkinsBuilder(DEFAULT_TASKS);
		project.getBuildersList().add(djangoBuilder);
		project.setScm(scm);
		FreeStyleBuild build = project.scheduleBuild2(1).get();
		String s = FileUtils.readFileToString(build.getLogFile());
		System.err.print(s);
		assertThat("Output should contain scheduled tasks.", s, containsString(DEFAULT_TASKS.get(0)));
	}
}
