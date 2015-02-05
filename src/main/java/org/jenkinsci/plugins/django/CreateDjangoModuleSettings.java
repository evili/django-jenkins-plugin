package org.jenkinsci.plugins.django;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.jenkinsci.remoting.RoleChecker;

public class CreateDjangoModuleSettings implements FileCallable<Void> {

	private static final long serialVersionUID = 1L;
	private PrintStream logger;
	private String settingsModule;

	public CreateDjangoModuleSettings(PrintStream logger, String settingsModule) {
		this.logger = logger;
		this.settingsModule = settingsModule;
	}

	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {

	}

	@Override
	public Void invoke(File f, VirtualChannel channel) throws IOException,
			InterruptedException {
		logger.println("Creating special djano-jenkins settings file");
		File settingsFile;
		PrintWriter settingsWriter;
		try {
			settingsFile = new File(f, PythonVirtualenv.DJANGO_JENKINS_SETTINGS+".py");
			settingsFile.createNewFile();
			settingsWriter = new PrintWriter(settingsFile);
			settingsWriter.println("from "+settingsModule+" import *");
			settingsWriter.println("INSTALLED_APPS = ('django_extensions'," +
                  "'django_jenkins',) +INSTALLED_APPS");
			settingsWriter.println("JENKINS_TASKS = (\n"+
                  "'django_jenkins.tasks.run_pep8',"+
                  ")");
			settingsWriter.close();
			
		} catch(IOException e) {
			logger.println(e.getMessage());
		}
		return null;
	}

}