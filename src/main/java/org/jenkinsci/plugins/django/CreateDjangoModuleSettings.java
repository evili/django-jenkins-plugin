package org.jenkinsci.plugins.django;

import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumSet;

import jenkins.MasterToSlaveFileCallable;

public class CreateDjangoModuleSettings extends MasterToSlaveFileCallable<Boolean> {

	private static final long serialVersionUID = 2L;
	private String settingsModule;
	private EnumSet<Task> tasks;
	private String projectApps;

	public CreateDjangoModuleSettings(String settingsModule, EnumSet<Task> actualTasks, String projectApps) {

		this.settingsModule = settingsModule;
		this.tasks = actualTasks;
		this.projectApps = projectApps;
	}

	@Override
	public Boolean invoke(File f, VirtualChannel channel) throws IOException,
			InterruptedException {
		DjangoJenkinsBuilder.LOGGER.info("Creating special djano-jenkins settings file");
		File settingsFile;
		PrintWriter settingsWriter;
		try {
			settingsFile = new File(f, PythonVirtualenv.DJANGO_JENKINS_SETTINGS+".py");
			settingsFile.createNewFile();
			settingsWriter = new PrintWriter(settingsFile);
			settingsWriter.println("from "+settingsModule+" import *");
			settingsWriter.println("INSTALLED_APPS = ('django_extensions'," +
                  "'django_jenkins',) +INSTALLED_APPS");
			String[] apps = projectApps.trim().replace(" ","").split(",");
			settingsWriter.println("PROJECT_APPS = (");
			for(String a: apps){
				settingsWriter.println("'"+a+"',");
			}
			settingsWriter.println(")");
			settingsWriter.println("JENKINS_TASKS = (\n");
			for (Task s: tasks) {
                  settingsWriter.println("'"+s.getPythonPackage()+"',");
			}
            settingsWriter.println(  ")");
			settingsWriter.close();
		} catch(IOException e) {
			DjangoJenkinsBuilder.LOGGER.info(e.getMessage());
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

}
