package org.jenkinsci.plugins.django;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jenkinsci.remoting.RoleChecker;

public class DjangoProjectSettingsFinder implements FileCallable<String> {

	private static final long serialVersionUID = 1L;
	private static final String[] MODULE_CANDIDATES = { "test_settings",
			"settings" };
	
	private PrintStream logger;
	
	public DjangoProjectSettingsFinder(PrintStream logger) {
		this.logger = logger;
	}

	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {
	}

	@Override
	public String invoke(File dir, VirtualChannel channel) throws IOException,
			InterruptedException {
		File found = null;
		DjangoJenkinsBuilder.LOGGER.info("Finding settings modules in "+dir.getPath());
		String foundCandidate = null;
		for (String candidate : MODULE_CANDIDATES) {
			logger.println("Trying to find some "+candidate);
			DjangoJenkinsBuilder.LOGGER.info("Probing "+candidate);
			Iterator<File> iter = FileUtils.iterateFiles(dir, 
						new NameFileFilter(candidate+".py"),
						TrueFileFilter.INSTANCE);
			while(iter.hasNext()) {
				found = iter.next();
				foundCandidate = candidate;
				logger.println("Found settings in "+found.getPath());
				break;
			}
			if(found != null)
				break;
		}
		
		if(found==null) {
			DjangoJenkinsBuilder.LOGGER.info("No settings modules found!!!");
			logger.println("No settings module!");
			throw(new IOException("No settings module found"));
		}
		String module = found.getParent().replace(File.pathSeparatorChar, '.')+foundCandidate;
		logger.println("Settings module is: "+module);
		return module;
	}
}
