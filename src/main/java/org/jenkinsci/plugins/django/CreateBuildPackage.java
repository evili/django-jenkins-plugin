package org.jenkinsci.plugins.django;

import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.PrintWriter;

import jenkins.MasterToSlaveFileCallable;

import org.apache.commons.io.FileUtils;

public class CreateBuildPackage extends MasterToSlaveFileCallable<Boolean> {

	private static final long serialVersionUID = 2L;

	public CreateBuildPackage() {
	}

	@Override
	public Boolean invoke(File dir, VirtualChannel channel) {
		File initFile;
		PrintWriter initWriter;
		DjangoJenkinsBuilder.LOGGER.info("Creating " + PythonVirtualenv.DJANGO_JENKINS_MODULE);
		try {
			FileUtils.deleteDirectory(dir);
			dir.mkdirs();
			initFile = new File(dir, "__init__.py");
			initFile.createNewFile();
			initWriter = new PrintWriter(initFile);
			initWriter.println("#");
			initWriter.close();
		} catch (Exception e) {
			DjangoJenkinsBuilder.LOGGER.info(e.getMessage());
			return Boolean.FALSE;
		}

		return Boolean.TRUE;

	}
}
