package org.jenkinsci.plugins.django;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import jenkins.plugins.shiningpanda.builders.VirtualenvBuilder;
import jenkins.plugins.shiningpanda.tools.PythonInstallationFinder;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;

import org.apache.commons.lang.StringUtils;

public class PythonVirtualenv implements Serializable {

	private static final long serialVersionUID = 2L;

	static final String DJANGO_JENKINS_REQUIREMENTS = "nosexcover django-extensions django-jenkins selenium";

	static final String DJANGO_JENKINS_MODULE = "jenkins_build";
	static final String DJANGO_JENKINS_SETTINGS = "jenkins_settings";

	private static final String ENABLE_COVERAGE = "--enable-coverage";
	private static final String COVERAGE_REQUIREMENT = "coverage";

	private AbstractBuild<?, ?> build;
	private Launcher launcher;
	private BuildListener listener;
	private PrintStream logger;

	public PythonVirtualenv(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) {
		this.build = build;
		this.launcher = launcher;
		this.listener = listener;
	}


	public boolean perform(EnumSet<Task> actualTasks, String projectApps, boolean enableCoverage) throws InterruptedException,
			IOException {
		logger = listener.getLogger();

		logger.println("Perfroming "+actualTasks);
		List<PythonInstallation> pInstalls;

		try {
			pInstalls = PythonInstallationFinder.configure();
		}
		catch(NullPointerException e){
			logger.println("No Python Installations found: "+e.getMessage());
			return false;
		}

		String pythonName = pInstalls.get(0).getName();

		ArrayList<String> commandList = new ArrayList<String>();

		logger.println("Installing Django Requirements");
		commandList.add(installDjangoJenkinsRequirements(actualTasks, enableCoverage));

		logger.println("Installing Project Requirements");
		commandList.add(installProjectRequirements());

		logger.println("Building jenkins package/module");
		commandList.add(createBuildPackage(actualTasks, projectApps));

		logger.println("Adding jenkins tasks");
		commandList.add("$PYTHON_EXE manage.py jenkins"+(enableCoverage ? " "+ENABLE_COVERAGE : "" ));

		String command = StringUtils.join(commandList, "\n");
		logger.println("Command:\n"+command);

		logger.println("Final Command: ");
		logger.println(command);

		logger.println("Creating VirtualnevBuilder");
		VirtualenvBuilder venv = new VirtualenvBuilder(pythonName,
				"django-jenkins", false, false, "Shell", command.toString(),
				false);
		logger.println("Performing in VirtualenvBuilder");
		return venv.perform(build, launcher, listener);
	}

	private String installDjangoJenkinsRequirements(EnumSet<Task> actualTasks, boolean enableCoverage) {
		String pip = "pip install "+DJANGO_JENKINS_REQUIREMENTS+
				(enableCoverage ? " "+COVERAGE_REQUIREMENT : "");
		for(Task t: actualTasks) {
			if(t.getRequirements() != null)
				pip += " "+t.getRequirements();
			else
				logger.println("WARINING: Task "+t.getName()+" has non-python requirements.");
		}
		return pip;
	}

	private String installProjectRequirements() throws InterruptedException {
		String requirementsFile = "# No project requirements found";
		try {
			requirementsFile = build.getWorkspace().act(new ProjectRequirementsFinder());
		}
		catch(IOException e) {
			logger.println("No requirements file found:");
			logger.println(e.getMessage());
		}
		return "pip install -r "+requirementsFile;
	}

	private String createBuildPackage(EnumSet<Task> actualTasks, String projectApps) throws IOException,
			InterruptedException {

		FilePath djModule = new FilePath(build.getWorkspace(),
				DJANGO_JENKINS_MODULE);
		logger.println("Finding Django project settings");

		String settingsModule = build.getWorkspace().act(new DjangoProjectSettingsFinder());

		logger.println("Creating Build Package");
		if(!djModule.act(new CreateBuildPackage())) {
			throw new IOException("Could not create Build Package.");
		}

		if ((projectApps==null) || (projectApps.trim().length()==0)) {
			logger.println("No project apps provided. Trying to find some");
			projectApps = build.getWorkspace().act(new ProjectApplicationsFinder());
		}

		logger.println("Creating jenkins settings module");
		if(!djModule.act(new CreateDjangoModuleSettings(settingsModule, actualTasks, projectApps))) {
			throw new IOException("Could not create jenkins setting module.");
		}

		logger.println("Returning settings: "+settingsModule);
		return "export DJANGO_SETTINGS_MODULE="+DJANGO_JENKINS_MODULE+"."+DJANGO_JENKINS_SETTINGS;
	}
}
