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
import java.util.Iterator;

import jenkins.MasterToSlaveFileCallable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;

/**
 * The Class DjangoProjectSettingsFinder.
 */
public class DjangoProjectSettingsFinder extends
        MasterToSlaveFileCallable<String> {
    /** (non-Javadoc) @see java.io.Serializable#serialVersionUID. */
    private static final long serialVersionUID = 2L;
    /** List of django settings module candidates. */
    private static final String[] MODULE_CANDIDATES = {"test_settings",
            "settings"};

    /*
     * (non-Javadoc)
     * @see hudson.FilePath.FileCallable#invoke(java.io.File,
     * hudson.remoting.VirtualChannel)
     */
    @Override
    public final String invoke(final File dir, final VirtualChannel channel)
            throws IOException, InterruptedException {
        File found = null;
        DjangoJenkinsBuilder.LOGGER.info("Finding settings modules in "
                + dir.getPath());
        String foundCandidate = null;
        final NotFileFilter excldeJenkins = new NotFileFilter(
                new NameFileFilter(PythonVirtualenv.DJANGO_JENKINS_MODULE));

        searchCandidate: for (final String candidate : MODULE_CANDIDATES) {
            DjangoJenkinsBuilder.LOGGER
                    .info("Trying to find some " + candidate);
            DjangoJenkinsBuilder.LOGGER.info("Probing " + candidate);
            final Iterator<File> iter = FileUtils.iterateFiles(dir,
                    new NameFileFilter(candidate + ".py"), excldeJenkins);
            while (iter.hasNext()) {
                found = iter.next();
                foundCandidate = candidate;
                DjangoJenkinsBuilder.LOGGER.info("Found settings in "
                        + found.getPath());
                break searchCandidate;
            }
        }

        if (found == null) {
            DjangoJenkinsBuilder.LOGGER.info("No settings modules found!!!");
            DjangoJenkinsBuilder.LOGGER.info("No settings module!");
            throw (new IOException("No settings module found"));
        }
        final String pkgPath = dir.toURI()
                .relativize(found.getParentFile().toURI()).toString();
        DjangoJenkinsBuilder.LOGGER.info("Pakage found: " + pkgPath);
        final String module = pkgPath.replace(File.separatorChar, '.')
                + foundCandidate;
        DjangoJenkinsBuilder.LOGGER.info("Settings module is: " + module);
        return module;
    }
}
