package org.jenkinsci.plugins.django;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import jenkins.plugins.shiningpanda.builders.VirtualenvBuilder;
import jenkins.plugins.shiningpanda.tools.PythonInstallationFinder;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;

import org.apache.commons.lang.StringUtils;

public class PythonVirtualenv implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String EQUAL_LINE = StringUtils.repeat("=", 72);
	static final String DJANGO_JENKINS_REQUIREMENTS = "nosexcover pep8 pyflakes flake8 "
			+ "coverage django-extensions django-jenkins selenium";

	static final String DJANGO_JENKINS_MODULE = "jenkins_build";
	static final String DJANGO_JENKINS_SETTINGS = "jenkins_settings";

	private AbstractBuild<?, ?> build;
	private Launcher launcher;
	private BuildListener listener;

	public PythonVirtualenv(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) {
		this.build = build;
		this.launcher = launcher;
		this.listener = listener;
	}


	public boolean perform(EnumSet<Task> actualTasks, String projectApps) throws InterruptedException,
			IOException {

		DjangoJenkinsBuilder.LOGGER.info("Perfroming "+actualTasks);
		List<PythonInstallation> pInstalls;

		try {
			pInstalls = PythonInstallationFinder.configure();
		}
		catch(NullPointerException e){
			DjangoJenkinsBuilder.LOGGER.info("No Python Installations found: "+e.getMessage());
			return false;
		}

		String pythonName = pInstalls.get(0).getName();

		ArrayList<String> commandList = new ArrayList<String>();

		DjangoJenkinsBuilder.LOGGER.info("Installing Django Requirements");
		commandList.add(installDjangoJenkinsRequirements());

		DjangoJenkinsBuilder.LOGGER.info("Installing Project Requirements");
		commandList.add(installProjectRequirements());

		DjangoJenkinsBuilder.LOGGER.info("Building jenkins package/module");
		commandList.add(createBuildPackage(actualTasks, projectApps));

		DjangoJenkinsBuilder.LOGGER.info("Adding jenkins tasks");
		commandList.add("$PYTHON_EXE manage.py jenkins");

		String command = StringUtils.join(commandList, "\n");
		DjangoJenkinsBuilder.LOGGER.info("Command:\n"+command);

		DjangoJenkinsBuilder.LOGGER.info("Final Command: ");
		DjangoJenkinsBuilder.LOGGER.info(EQUAL_LINE);
		DjangoJenkinsBuilder.LOGGER.info(command);
		DjangoJenkinsBuilder.LOGGER.info(EQUAL_LINE);
		DjangoJenkinsBuilder.LOGGER.info("Creating VirtualnevBuilder");
		VirtualenvBuilder venv = new VirtualenvBuilder(pythonName,
				"django-jenkins", false, false, "Shell", command.toString(),
				false);
		DjangoJenkinsBuilder.LOGGER.info("Performing in VirtualenvBuilder");
		return venv.perform(build, launcher, listener);
	}

	private String installDjangoJenkinsRequirements() {
		return "pip install " + DJANGO_JENKINS_REQUIREMENTS;
	}

	private String installProjectRequirements() throws InterruptedException {
		String requirementsFile = "# No project requirements found";
		try {
			requirementsFile = build.getWorkspace().act(new ProjectRequirementsFinder());
		}
		catch(IOException e) {
			DjangoJenkinsBuilder.LOGGER.info("No requirements file found:");
			DjangoJenkinsBuilder.LOGGER.info(e.getMessage());
		}
		return "pip install -r "+requirementsFile;
	}

	private String createBuildPackage(EnumSet<Task> actualTasks, String projectApps) throws IOException,
			InterruptedException {

		FilePath djModule = new FilePath(build.getWorkspace(),
				DJANGO_JENKINS_MODULE);
		DjangoJenkinsBuilder.LOGGER.info("Finding Django project settings");

		String settingsModule = build.getWorkspace().act(new DjangoProjectSettingsFinder());

		DjangoJenkinsBuilder.LOGGER.info("Creating Build Package");
		if(!djModule.act(new CreateBuildPackage())) {
			throw new IOException("Could not create Build Package.");
		}

		if ((projectApps==null) || (projectApps.trim().length()==0)) {
			DjangoJenkinsBuilder.LOGGER.info("No project apps provided. Trying to find some");
			projectApps = build.getWorkspace().act(new ProjectApplicationsFinder());
		}

		DjangoJenkinsBuilder.LOGGER.info("Creating jenkins settings module");
		if(!djModule.act(new CreateDjangoModuleSettings(settingsModule, actualTasks, projectApps))) {
			throw new IOException("Could not create jenkins setting module.");
		}

		DjangoJenkinsBuilder.LOGGER.info("Returning settings: "+settingsModule);
		return "export DJANGO_SETTINGS_MODULE="+DJANGO_JENKINS_MODULE+"."+DJANGO_JENKINS_SETTINGS;
	}
}
