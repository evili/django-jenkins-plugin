package org.jenkinsci.plugins.django;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class DjangoJenkinsBuilder extends Builder implements Serializable {

	public static final String DISPLAY_NAME = "Django Jenkins Builder";

	private static final long serialVersionUID = 1L;
	final static Logger LOGGER = Logger.getLogger(DjangoJenkinsBuilder.class.getName());

	private final String tasks;

	static {
		FileHandler h;
		SimpleFormatter f = new SimpleFormatter();
		try {
			h = new FileHandler("%t/django-jenkins-builder-%g.log", 1024*1024, 2, true);
			h.setLevel(Level.ALL);
			h.setFormatter(f);
			LOGGER.addHandler(h);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOGGER.setLevel(Level.ALL);
	}
	
	@Override
	public Descriptor<Builder> getDescriptor() {
		
		LOGGER.info("Returning descriptor");
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static final class DescriptorImpl
        extends BuildStepDescriptor<Builder> {

		public static final String DEFAULT_TASKS = "jenkins";
		private String defaultTasks = DEFAULT_TASKS;
		
		@Override
		public boolean configure(StaplerRequest req, JSONObject json)
				throws FormException {
			LOGGER.info("In configure");
			defaultTasks = json.getString("defaultTasks");
			LOGGER.info("Saving...");
			save();
			LOGGER.info("Returning super.configure");
			return super.configure(req, json);		
		}

		public String getDefaultTasks() {
			LOGGER.info("Returning default tasks: "+defaultTasks);
			return defaultTasks;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			LOGGER.info("Yes, we're applicable!");
			return true;
		}

		@Override
		public String getDisplayName() {
			LOGGER.info("How We are called "+DISPLAY_NAME );
			return DISPLAY_NAME;
		}
	}
	
	@DataBoundConstructor
	public DjangoJenkinsBuilder(String tasks) {
		LOGGER.info("In Constructor");
		this.tasks = tasks;
	}

	public String getTasks() {
		LOGGER.info("Returning tasks: "+tasks);
		return tasks;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		LOGGER.info("Performing build");
		listener.getLogger().println("Calling seyUpVirtualenv");
		
		boolean status = false;

		PythonVirtualenv venv = new PythonVirtualenv(build, launcher, listener);
		
		try {
			LOGGER.info("Calling venv.perform");
			status =  venv.perform(tasks);
		}
		catch(Exception e) {
			LOGGER.info("Something went wrong: "+e.getMessage());
			status = false;
		}
		if(status)
			LOGGER.info("Success");
		else
			LOGGER.info("Build failed <:-( ");
		return status;
	}
}
