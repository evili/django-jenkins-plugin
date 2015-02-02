package org.jenkinsci.plugins.django;

import junit.framework.TestCase;
import hudson.model.FreeStyleProject;

import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsRule;


public abstract class AbstractDjangoTestCase extends TestCase {

	protected FreeStyleProject project;
	protected DjangoJenkinsBuilder djangoBuilder;
	
	@Rule public JenkinsRule jenkinsRule = new JenkinsRule();
	@Override
	protected void setUp() throws Exception {
		project = jenkinsRule.createFreeStyleProject("testdjango");
	}
}
