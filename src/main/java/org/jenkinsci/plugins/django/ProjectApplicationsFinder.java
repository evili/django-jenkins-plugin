package org.jenkinsci.plugins.django;

import hudson.FilePath.FileCallable;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.eclipse.jgit.util.StringUtils;
import org.jenkinsci.remoting.RoleChecker;

public class ProjectApplicationsFinder implements FileCallable<String> {

	private static final long serialVersionUID = 1L;

	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {
	}

	@Override
	public String invoke(File dir, VirtualChannel channel) throws IOException,
			InterruptedException {
		DjangoJenkinsBuilder.LOGGER.info("Finding project apps in: "+dir.getPath());
		String apps = null;
		NotFileFilter excludeJenkins = new NotFileFilter(
				new NameFileFilter(PythonVirtualenv.DJANGO_JENKINS_MODULE));

		String appFiles[] = {"views.py","models.py","urls.py"};
		NameFileFilter filterApps = new NameFileFilter(appFiles);

		Iterator<File> iter = FileUtils.iterateFiles(dir,
				filterApps,
				excludeJenkins);
		Set<String> foundApps = new HashSet<String>();
		while(iter.hasNext()) {
			File found = iter.next();
			String app = found.getParentFile().getName();
			DjangoJenkinsBuilder.LOGGER.info("Found app: "+app);
			foundApps.add(app);
		}
		if(foundApps.size()>0) {
			apps = StringUtils.join(foundApps, ",");
		}
		DjangoJenkinsBuilder.LOGGER.info("Found apps: "+apps);
		return apps;
	}
}
