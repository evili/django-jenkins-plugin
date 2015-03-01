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
import java.io.PrintWriter;

import jenkins.MasterToSlaveFileCallable;

import org.apache.commons.io.FileUtils;

/**
 * Creates the build package for django-jenkins.
 */
public class CreateBuildPackage extends MasterToSlaveFileCallable<Boolean> {

    /** (non-Javadoc) @see java.io.Serializable#serialVersionUID. */
    private static final long serialVersionUID = 2L;

    /**
     * Instantiates a new creates the build package.
     */
    public CreateBuildPackage() {
    }

    /*
     * (non-Javadoc)
     * @see hudson.FilePath.FileCallable#invoke(java.io.File,
     * hudson.remoting.VirtualChannel)
     * @return <code>true</code> upon correct completion, <code>false</code>
     * otherwise.
     */
    @Override
    public final Boolean invoke(final File dir, final VirtualChannel channel) {
        File initFile;
        PrintWriter initWriter;
        DjangoJenkinsBuilder.LOGGER.info("Creating "
                + PythonVirtualenv.DJANGO_JENKINS_MODULE);
        try {
            FileUtils.deleteDirectory(dir);
            dir.mkdirs();
            initFile = new File(dir, "__init__.py");
            initFile.createNewFile();
            initWriter = new PrintWriter(initFile);
            initWriter.println("#");
            initWriter.close();
        } catch (final Exception e) {
            DjangoJenkinsBuilder.LOGGER.info(e.getMessage());
            return Boolean.FALSE;
        }

        return Boolean.TRUE;

    }
}
