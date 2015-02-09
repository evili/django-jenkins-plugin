package org.jenkinsci.plugins.django;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.jenkinsci.remoting.RoleChecker;

public class ProjectRequirementsFinder implements FileCallable<String> {

	private static final long serialVersionUID = 1L;
	private static final String[] MODULE_CANDIDATES = { "requirements.txt",
	"requirements.pip" };

	private PrintStream logger;

	public ProjectRequirementsFinder(PrintStream logger) {
		this.logger = logger;
	}

	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {
	}

	@Override
	public String invoke(File dir, VirtualChannel channel) throws IOException,
	InterruptedException {
		File found = null;
		DjangoJenkinsBuilder.LOGGER.info("Finding requirement files in "+dir.getPath());
		String foundCandidate = null;
		NotFileFilter excludeJenkins = new NotFileFilter(
				new NameFileFilter(PythonVirtualenv.DJANGO_JENKINS_MODULE));

		searchCandidate:
			for (String candidate : MODULE_CANDIDATES) {
				logger.println("Trying to find some "+candidate);
				DjangoJenkinsBuilder.LOGGER.info("Probing "+candidate);
				Iterator<File> iter = FileUtils.iterateFiles(dir,
						new NameFileFilter(candidate),
						excludeJenkins);
				while(iter.hasNext()) {
					found = iter.next();
					foundCandidate = candidate;
					logger.println("Found settings in "+found.getPath());
					break searchCandidate;
				}
			}

		if(found==null) {
			DjangoJenkinsBuilder.LOGGER.info("No requirements modules found!!!");
			logger.println("No settings module!");
			throw(new IOException("No settings module found"));
		}
		String pkgPath = dir.toURI().relativize(found.getParentFile().toURI()).toString(); 
		DjangoJenkinsBuilder.LOGGER.info("Pakage found: "+pkgPath);
		String module = pkgPath+foundCandidate;
		DjangoJenkinsBuilder.LOGGER.info("Settings module is: "+module);
		return module;
	}
}
