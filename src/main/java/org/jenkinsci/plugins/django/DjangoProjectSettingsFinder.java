package org.jenkinsci.plugins.django;

import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import jenkins.MasterToSlaveFileCallable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;

public class DjangoProjectSettingsFinder extends MasterToSlaveFileCallable<String> {

	private static final long serialVersionUID = 1L;
	private static final String[] MODULE_CANDIDATES = { "test_settings",
	"settings" };

	private PrintStream logger;

	public DjangoProjectSettingsFinder(PrintStream logger) {
		this.logger = logger;
	}

	@Override
	public String invoke(File dir, VirtualChannel channel) throws IOException,
	InterruptedException {
		File found = null;
		DjangoJenkinsBuilder.LOGGER.info("Finding settings modules in "+dir.getPath());
		String foundCandidate = null;
		NotFileFilter excldeJenkins = new NotFileFilter(
				new NameFileFilter(PythonVirtualenv.DJANGO_JENKINS_MODULE));

		searchCandidate:
			for (String candidate : MODULE_CANDIDATES) {
				logger.println("Trying to find some "+candidate);
				DjangoJenkinsBuilder.LOGGER.info("Probing "+candidate);
				Iterator<File> iter = FileUtils.iterateFiles(dir,
						new NameFileFilter(candidate+".py"),
						excldeJenkins);
				while(iter.hasNext()) {
					found = iter.next();
					foundCandidate = candidate;
					logger.println("Found settings in "+found.getPath());
					break searchCandidate;
				}
			}

		if(found==null) {
			DjangoJenkinsBuilder.LOGGER.info("No settings modules found!!!");
			logger.println("No settings module!");
			throw(new IOException("No settings module found"));
		}
		String pkgPath = dir.toURI().relativize(found.getParentFile().toURI()).toString();
		DjangoJenkinsBuilder.LOGGER.info("Pakage found: "+pkgPath);
		String module =pkgPath.replace(File.separatorChar, '.')+foundCandidate;
		DjangoJenkinsBuilder.LOGGER.info("Settings module is: "+module);
		return module;
	}
}
