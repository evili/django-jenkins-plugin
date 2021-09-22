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
import java.io.PrintWriter;
import java.util.EnumSet;

import jenkins.MasterToSlaveFileCallable;

/**
 * Creates a special python module under which we can run the django-jenkins
 * tasks without altering the original django project.
 */
public class CreateDjangoModuleSettings extends
        MasterToSlaveFileCallable<Boolean> {
    /** (non-Javadoc) @see java.io.Serializable#serialVersionUID. */
    private static final long serialVersionUID = 2L;
    /** Name of the django settigns module. */
    private final String settingsModule;
    /** List of {@link Tasks} to perform. */
    private final EnumSet<Task> tasks;
    /** List of comma separated project applications. */
    private final String projectApps;

    /**
     * Instantiates a new creates the django module settings.
     *
     * @param settingsModule
     *            the name of the new django settings module.
     * @param tasks
     *            the django-jenkins tasks to perform.
     * @param projectApps
     *            the list of project applications where we run the
     *            django-jenkins tasks.
     */
    public CreateDjangoModuleSettings(final String settingsModule,
            final EnumSet<Task> tasks, final String projectApps) {
        this.settingsModule = settingsModule;
        this.tasks = tasks;
        this.projectApps = projectApps;
    }

    /*
     * (non-Javadoc)
     * @see hudson.FilePath.FileCallable#invoke(java.io.File,
     * hudson.remoting.VirtualChannel)
     * @return <code>true</code> upon correct completion, <code>false</code>
     * otherwise.
     */
    @Override
    public final Boolean invoke(final File f, final VirtualChannel channel)
            throws IOException, InterruptedException {
        DjangoJenkinsBuilder.LOGGER
                .info("Creating special djano-jenkins settings file");
        File settingsFile;
        try {
            settingsFile = new File(f, PythonVirtualenv.DJANGO_JENKINS_SETTINGS
                    + ".py");
            if(settingsFile.createNewFile()) {
		PrintWriter settingsWriter = new PrintWriter(settingsFile, "UTF-8");
		settingsWriter.println("from " + settingsModule + " import *");
		settingsWriter.println("INSTALLED_APPS = ('django_extensions',"
				       + "'django_jenkins',) +INSTALLED_APPS");
		final String[] apps = projectApps.trim().replace(" ", "")
                    .split(",");
		settingsWriter.println("PROJECT_APPS = (");
		for (final String a : apps) {
		    settingsWriter.println("'" + a + "',");
		}
		settingsWriter.println(")");
		settingsWriter.println("JENKINS_TASKS = (\n");
		for (final Task s : tasks) {
		    settingsWriter.println("'" + s.getPythonPackage() + "',");
		}
		settingsWriter.println(")");
		settingsWriter.close();
	    }
        } catch (final Exception e) {
            DjangoJenkinsBuilder.LOGGER.info(e.getMessage());
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
}
