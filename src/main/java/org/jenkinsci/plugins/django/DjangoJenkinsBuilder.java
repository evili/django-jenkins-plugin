package org.jenkinsci.plugins.django;

import java.io.IOException;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.Builder;

import org.kohsuke.stapler.DataBoundConstructor;


public class DjangoJenkinsBuilder extends Builder {

	private final String tasks;

	@DataBoundConstructor
	public DjangoJenkinsBuilder(String tasks) {
		this.tasks = tasks;
	}
	
	public String getTasks() {
		return tasks;
	}

	@Override
	public DjangoJenkinsDescriptor getDescriptor() {
		return (DjangoJenkinsDescriptor) super.getDescriptor();
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		listener.getLogger().println("We still do nothing at all!");
		return true;
	}
	
	
	
}