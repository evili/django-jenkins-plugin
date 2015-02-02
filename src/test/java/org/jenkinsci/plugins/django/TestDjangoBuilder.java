package org.jenkinsci.plugins.django;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import hudson.model.FreeStyleBuild;

import org.apache.commons.io.FileUtils;

@Rule public JenkinsRule jenkinsRule = new JenkinsRule();

public class TestDjangoBuilder {
	private final static String NO_TASK = "nonsensetask";

    public void setUp() {
        
    }
    @Test
	public void testPluginLoads() throws Exception {
        FreeStyleProject project = JenkinsRule.createFreeStyleProject();
		djangoBuilder = new DjangoJenkinsBuilder(NO_TASK);
		project.getBuildersList().add(djangoBuilder);
		FreeStyleBuild build = project.scheduleBuild2(0).get();
		String s = FileUtils.readFileToString(build.getLogFile());
		assertThat(s, containsString(NO_TASK));
	}
}
