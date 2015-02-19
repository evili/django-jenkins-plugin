package org.jenkinsci.plugins.django;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.plugins.git.GitSCM;

import java.util.EnumSet;

import jenkins.scm.DefaultSCMCheckoutStrategyImpl;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class ITDjangoJenkinsBuilder {

	private static final String DJANGO_TEST_PROJECT_GIT_URL = "https://github.com/evili/django_test_deploy.git";

	@Rule
	public JenkinsRule jRule  = new JenkinsRule();

	@Test
	public void testPluginLoads() throws Exception {
        FreeStyleProject project = jRule.createFreeStyleProject();
		DjangoJenkinsBuilder djangoBuilder = new DjangoJenkinsBuilder(EnumSet.of(Task.PEP8), "items", true);
		project.getBuildersList().add(djangoBuilder);
		GitSCM scm = new GitSCM(DJANGO_TEST_PROJECT_GIT_URL);
		project.setScm(scm);
		DefaultSCMCheckoutStrategyImpl scmCheckoutStrategy = new DefaultSCMCheckoutStrategyImpl();
		project.setScmCheckoutStrategy(scmCheckoutStrategy);
		FreeStyleBuild build = project.scheduleBuild2(1).get();
		String s = FileUtils.readFileToString(build.getLogFile());
		assertThat("Output should contain scheduled tasks.", s, containsString("Finished: SUCCESS"));
	}
}
