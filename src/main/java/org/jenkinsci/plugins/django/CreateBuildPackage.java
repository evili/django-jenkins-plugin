package org.jenkinsci.plugins.django;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;
import org.jenkinsci.remoting.RoleChecker;

public class CreateBuildPackage implements FileCallable<Void> {

	private static final long serialVersionUID = 1L;
	private PrintStream logger;

	public CreateBuildPackage(PrintStream logger) {
		this.logger = logger;
	}

	@Override
	public Void invoke(File dir, VirtualChannel channel) {
		File initFile;
		PrintWriter initWriter;
		logger.println("Creating " + PythonVirtualenv.DJANGO_JENKINS_MODULE);
		try {
			FileUtils.deleteDirectory(dir);
			dir.mkdirs();
			initFile = new File(dir, "__init__.py");
			initFile.createNewFile();
			initWriter = new PrintWriter(initFile);
			initWriter.println("#");
			initWriter.close();
		} catch (IOException e) {
			logger.println(e.getMessage());
		}

		return null;

	}

	public PrintStream getLogger() {
		return logger;
	}

	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {
		// TODO Auto-generated method stub

	}
}
