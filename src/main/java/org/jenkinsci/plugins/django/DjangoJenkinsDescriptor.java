package org.jenkinsci.plugins.django;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

@Extension // This indicates to Jenkins that this is an implementation of an extension point.
public final class DjangoJenkinsDescriptor extends BuildStepDescriptor<Builder> {

	private static DjangoJenkinsDescriptor descriptor = new DjangoJenkinsDescriptor();

	public static DjangoJenkinsDescriptor getDescriptor() {
		return descriptor;
	}

	private DjangoJenkinsDescriptor() {
		if (descriptor != null) {
			throw new IllegalStateException("Already instantiated");
		}
		load();
	}

	@Override
	public boolean isApplicable(Class<? extends AbstractProject> aClass) {
		return true;
	}

	@Override
	public String getDisplayName() {
		return "Django Jenkins Builder";
	}

}