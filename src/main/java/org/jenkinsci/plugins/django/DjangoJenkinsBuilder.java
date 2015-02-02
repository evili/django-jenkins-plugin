package org.jenkinsci.plugins.django;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.IOException;
import java.io.Serializable;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class DjangoJenkinsBuilder extends Builder implements Serializable {

	private static final long serialVersionUID = -744011259213259821L;
	private final String tasks;

	@Extension
	public static final class DescriptorImpl
        extends BuildStepDescriptor<Builder> {

		public static final String DEFAULT_TASKS = "test";
		private String defaultTasks = DEFAULT_TASKS;
		
		@Override
		public boolean configure(StaplerRequest req, JSONObject json)
				throws FormException {
			defaultTasks = json.getString("defaultTasks");
			save();
			return super.configure(req, json);		
		}

		public String getDefaultTasks() {
			return defaultTasks;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

		@Override
		public String getDisplayName() {
			return "Django Jenkins Builder";
		}
	}
	
	@DataBoundConstructor
	public DjangoJenkinsBuilder(String tasks) {
		this.tasks = tasks;
	}

	public String getTasks() {
		return tasks;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		listener.getLogger().println("Calling seyUpVirtualenv");

		boolean status = false;

		PythonVirtualenv venv = new PythonVirtualenv(build, launcher, listener);
		
		try {
			status =  venv.perform();
		}
		catch(Exception e) {
			status = false;
		}
		return status;
	}
}
