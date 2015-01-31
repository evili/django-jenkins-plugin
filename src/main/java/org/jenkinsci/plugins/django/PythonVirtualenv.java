package org.jenkinsci.plugins.django;

import java.io.IOException;

import jenkins.plugins.shiningpanda.interpreters.Python;
import jenkins.plugins.shiningpanda.interpreters.Virtualenv;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;
import jenkins.plugins.shiningpanda.utils.BuilderUtil;
import jenkins.plugins.shiningpanda.workspace.Workspace;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Launcher.ProcStarter;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

public class PythonVirtualenv {
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

	public boolean perform() {
		Workspace workspace;
		Virtualenv venv;
		PythonInstallation installation;
		EnvVars environment;
		Python interpreter;
		FilePath home;
		
		if(PythonInstallation.isEmpty()) {
			listener.getLogger().println("No Python installations found!");
			return false;
		}
		else {
			installation = PythonInstallation.list()[0];
		}
		try {
			workspace = Workspace.fromBuild(build);
			home = workspace.getVirtualenvHome("django-jenkins");
			environment = BuilderUtil.getEnvironment(build, listener);
			interpreter = BuilderUtil.getInterpreter(launcher, listener,
					installation.getHome());
			venv = BuilderUtil.getVirtualenv(listener, home);
			if(venv.isOutdated(workspace, interpreter, false)) {
				if(!venv.create(launcher, listener, workspace, home, environment, interpreter, false))
					return false;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		} catch (InterruptedException e1) {
			e1.printStackTrace();
			return false;
		}
		
		try {
			listener.getLogger().println("Launching shell command");
			ProcStarter p = launcher.launch();
			p.stdout(listener);
			p.cmdAsSingleString("virtualenv ");
			p.join();
		}
		catch(IOException e){
			e.printStackTrace();
			listener.getLogger().println("IOException !");
			return false;
		}
		catch(InterruptedException e) {
			e.printStackTrace();
			listener.getLogger().println("IOException !");
			return false;			
		}
		return true;
	}
}
