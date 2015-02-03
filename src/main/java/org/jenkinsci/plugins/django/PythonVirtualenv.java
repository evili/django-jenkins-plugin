package org.jenkinsci.plugins.django;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jenkins.plugins.shiningpanda.builders.VirtualenvBuilder;
import jenkins.plugins.shiningpanda.tools.PythonInstallationFinder;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;

public class PythonVirtualenv {
	private static final String EQUAL_LINE = StringUtils.repeat("=", 72);

	private static final String DJANGO_JENKINS_REQUIREMENTS = "nosexcover pep8 pyflakes flake8 "+
			"coverage django-extensions django-jenkins";
	
	private AbstractBuild<?, ?> build;
	private Launcher launcher;
	private BuildListener listener;
	
	public PythonVirtualenv(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
		this.setBuild(build);
		this.setLauncher(launcher);
		this.setListener(listener);
	}
	
	public AbstractBuild<?, ?> getBuild() {
		return build;
	}

	public void setBuild(AbstractBuild<?, ?> build) {
		this.build = build;
	}

	public Launcher getLauncher() {
		return launcher;
	}

	public void setLauncher(Launcher launcher) {
		this.launcher = launcher;
	}

	public BuildListener getListener() {
		return listener;
	}

	public void setListener(BuildListener listener) {
		this.listener = listener;
	}

	public boolean perform(String tasks) throws InterruptedException, IOException {

		PrintStream logger = listener.getLogger();
		List<PythonInstallation> pInstalls = PythonInstallationFinder.configure();
		String pythonName = pInstalls.get(0).getName();

		StringBuilder command = new StringBuilder();
		command.append(installDjangoJenkinsRequirements());
		command.append("\n$PYTHON_EXE manage.py django-jenkins "+tasks);
		
		logger.println("Final Command: ");
		logger.println(EQUAL_LINE);
		logger.println(command.toString());
		logger.println(EQUAL_LINE);

		VirtualenvBuilder venv = new VirtualenvBuilder(pythonName, "django-jenkins", false, false, "Shell", command.toString(), false);
		return venv.perform(build, launcher, listener);
	}
	
	private String installDjangoJenkinsRequirements() {
		return "pip install "+DJANGO_JENKINS_REQUIREMENTS;
	}
}
