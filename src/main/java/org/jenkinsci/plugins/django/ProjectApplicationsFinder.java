/*
 * The MIT License Copyright (c) 2015, Evili del Rio i Silvan. Permission is
 * hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions: The above copyright notice and this
 * permission notice shall be included in all copies or substantial portions of
 * the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package org.jenkinsci.plugins.django;

import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jenkins.MasterToSlaveFileCallable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.eclipse.jgit.util.StringUtils;

/**
 * The Class ProjectApplicationsFinder.
 */
public class ProjectApplicationsFinder extends
        MasterToSlaveFileCallable<String> {
    /** (non-Javadoc) @see java.io.Serializable#serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     * @see hudson.FilePath.FileCallable#invoke(java.io.File,
     * hudson.remoting.VirtualChannel)
     */
    @Override
    public final String invoke(final File dir, final VirtualChannel channel)
            throws IOException, InterruptedException {
        DjangoJenkinsBuilder.LOGGER.info("Finding project apps in: "
                + dir.getPath());
        String apps = null;
        final NotFileFilter excludeJenkins = new NotFileFilter(
                new NameFileFilter(PythonVirtualenv.DJANGO_JENKINS_MODULE));

        final String[] appFiles = {"views.py", "models.py", "urls.py"};
        final NameFileFilter filterApps = new NameFileFilter(appFiles);

        final Iterator<File> iter = FileUtils.iterateFiles(dir, filterApps,
                excludeJenkins);
        final Set<String> foundApps = new HashSet<String>();
        while (iter.hasNext()) {
            final File found = iter.next();
            final String app = found.getParentFile().getName();
            DjangoJenkinsBuilder.LOGGER.info("Found app: " + app);
            foundApps.add(app);
        }
        if (foundApps.size() > 0) {
            apps = StringUtils.join(foundApps, ",");
        }
        DjangoJenkinsBuilder.LOGGER.info("Found apps: " + apps);
        return apps;
    }
}
