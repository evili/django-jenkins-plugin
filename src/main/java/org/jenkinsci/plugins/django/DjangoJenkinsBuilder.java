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
import java.io.PrintStream;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class DjangoJenkinsBuilder extends Builder implements Serializable {

	private static final long serialVersionUID = 4L;
	public static final String DISPLAY_NAME = "Django Jenkins Builder";
	public final static EnumSet<Task> DEFAULT_TASKS = EnumSet.noneOf(Task.class);

	final static Logger LOGGER = Logger.getLogger(DjangoJenkinsBuilder.class.getName());

	private final EnumSet<Task> tasks;
	private String projectApps;
	private boolean enableCoverage;

	static {
		/*
		 * By default, add any django-jenkins tasks that only
		 * depends on python-pip packages.
		 */
		for(Task t: EnumSet.allOf(Task.class)) {
			if(t.getRequirements() != null)
				DEFAULT_TASKS.add(t);
		}
		FileHandler h;
		SimpleFormatter f = new SimpleFormatter();
		try {
			h = new FileHandler("%t/django-jenkins-builder.log", 1024*1024, 2, true);
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
		private EnumSet<Task> defaultTasks = DjangoJenkinsBuilder.DEFAULT_TASKS;

		@Override
		public boolean configure(StaplerRequest req, JSONObject json)
				throws FormException {
			LOGGER.info("In configure:");
			LOGGER.info("JSON: "+json.toString(2));
			try {
				JSONObject newJSONDefaults = json.getJSONObject("defaultTasks");
				Iterator<?> it = newJSONDefaults.keys();
				EnumSet<Task> newDefaults = EnumSet.noneOf(Task.class);
				while(it.hasNext()) {
					String task = (String) it.next();
					boolean checked = newJSONDefaults.getBoolean(task);
					if(checked) {
						newDefaults.add(Task.getTask(task));
					}
				}
				defaultTasks = newDefaults;
			}
			catch (Exception e){
				LOGGER.info("Could not configure Builder: "+e.getMessage());
			}
			LOGGER.info("Saving...");
			save();
			LOGGER.info("Returning super.configure");
			return super.configure(req, json);
		}

		public EnumSet<Task> getDefaultTasks() throws Exception {
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
	public DjangoJenkinsBuilder(EnumSet<Task> tasks, String projectApps, boolean enableCoverage) {
		LOGGER.info("In Constructor");
		//this.tasks = noTasks;
		this.tasks = tasks;
		this.projectApps = projectApps;
		this.enableCoverage = enableCoverage;
	}

	public EnumSet<Task> getTasks() {
		LOGGER.info("Returning tasks: "+tasks);
		return tasks;
	}

	public String getProjectApps() {
		return projectApps;
	}

	public boolean isEnableCoverage() {
		return enableCoverage;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		PrintStream logger = listener.getLogger();

		logger.println("Performing Django-Jenkins build");

		boolean status = false;

		PythonVirtualenv venv = new PythonVirtualenv(build, launcher, listener);

		try {
			logger.println("Calling VirtualEnv Builder");
			EnumSet<Task> actualTasks = ((tasks == null) || (tasks.size() == 0)) ? DEFAULT_TASKS: tasks;
			status =  venv.perform(actualTasks, projectApps, enableCoverage);
		}
		catch(Exception e) {
			logger.println("Something went wrong: "+e.getMessage());
			status = false;
		}
		if(status)
			logger.println("Success");
		else
			logger.println("Build failed <:-( ");
		return status;
	}
}
