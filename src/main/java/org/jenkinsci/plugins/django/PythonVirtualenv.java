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

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import jenkins.plugins.shiningpanda.builders.VirtualenvBuilder;
import jenkins.plugins.shiningpanda.tools.PythonInstallationFinder;
import jenkins.plugins.shiningpanda.tools.PythonInstallation;

import org.apache.commons.lang.StringUtils;

/**
 * The Class PythonVirtualenv.
 */
public class PythonVirtualenv implements Serializable {
    /** (non-Javadoc) @see java.io.Serializable#serialVersionUID. */
    private static final long serialVersionUID = 2L;
    /** PIP Install command */
    static final String PIP_INSTALL = "pip install";
    static final String PIP_UPGRADE = " --upgrade ";
    /** Python requirements needed for the django-jenkins module. */
    static final String DJANGO_JENKINS_REQUIREMENTS =
            "nosexcover django-extensions django-jenkins selenium";
    /** Name of the python package created to run django-jenkins tasks. */
    static final String DJANGO_JENKINS_MODULE = "jenkins_build";
    /**
     * Name of the django settings module loaded to run django-jenkins tasks.
     */
    static final String DJANGO_JENKINS_SETTINGS = "jenkins_settings";
    /** CLI flag to run coverage tool. */
    private static final String ENABLE_COVERAGE = "--enable-coverage";
    /** Python requirement for coverage tool. */
    // django_jenkins<=0.17.0 needs coverage<4.0
    private static final String COVERAGE_REQUIREMENT = "coverage";

    /**
     * AbstractBuild. (non-Javadoc)
     *
     * @see DjangoJenkinsBuilder#perform(AbstractBuild, Launcher, BuildListener)
     */
    private final AbstractBuild<?, ?> build;
    /**
     * Launcher. (non-Javadoc)
     *
     * @see DjangoJenkinsBuilder#perform(AbstractBuild, Launcher, BuildListener)
     */
    private final Launcher launcher;
    /**
     * BuildListener. (non-Javadoc)
     *
     * @see DjangoJenkinsBuilder#perform(AbstractBuild, Launcher, BuildListener)
     */
    private final BuildListener listener;

    /** Workspace FilePah directory for the current build */
    private FilePath workspace = null;

    /**
     * Instantiates a new python virtualenv.
     *
     * @param build
     *            the build
     * @param launcher
     *            the launcher
     * @param listener
     *            the listener
     */
    public PythonVirtualenv(final AbstractBuild<?, ?> build,
            final Launcher launcher, final BuildListener listener) {
        this.build = build;
        this.launcher = launcher;
        this.listener = listener;
    }

    /**
     * Perform.
     *
     * @param actualTasks
     *            the actual tasks
     * @param projectApps
     *            the project apps
     * @param settingsModule 
     * @param requirementsFile 
     * @param enableCoverage
     *            the enable coverage
     * @return true, if successful
     * @throws InterruptedException
     *             the interrupted exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public final boolean perform(final EnumSet<Task> actualTasks,
            final String projectApps, String settingsModule, String requirementsFile, final boolean enableCoverage)
            throws InterruptedException, IOException {
        PrintStream logger = listener.getLogger();

        logger.println("Perfroming " + actualTasks);
        List<PythonInstallation> pInstalls;

        try {
            pInstalls = PythonInstallationFinder.configure();
        } catch (final NullPointerException e) {
            logger.println("No Python Installations found: " + e.getMessage());
            return false;
        }

        workspace = build.getWorkspace();
        if (workspace == null) {
            throw new IOException("No workspace found");
        }

        final String pythonName = pInstalls.get(0).getName();

        final ArrayList<String> commandList = new ArrayList<String>();
        /* First upgrade pip and friends */
        logger.println("Upgrade pip first.");
        commandList.add(PIP_INSTALL+PIP_UPGRADE+"pip");

        /* DjangoJenkins Requirements (can be overridden by project ones) */
        logger.println("Installing Django Requirements");
        commandList.add(installDjangoJenkinsRequirements(actualTasks,
                enableCoverage));
        
        /* Project Requirements */
        logger.println("Installing Project Requirements");
        commandList.add(installProjectRequirements(requirementsFile));
        
        logger.println("Building jenkins package/module");
        commandList.add(createBuildPackage(actualTasks, projectApps, settingsModule));

        logger.println("Adding jenkins tasks");
        String jenkinsCli = "$PYTHON_EXE manage.py jenkins";
        if (enableCoverage) {
            jenkinsCli += " " + ENABLE_COVERAGE;
        }
        commandList.add(jenkinsCli + "\n");

        final String command = StringUtils.join(commandList, "\n");
        logger.println("Command:\n" + command);

        logger.println("Final Command: ");
        logger.println(command);

        logger.println("Creating VirtualnevBuilder");
        final VirtualenvBuilder venv = new VirtualenvBuilder(pythonName,
                "django-jenkins", false, false, "Shell", command.toString(),
                false);
        logger.println("Performing in VirtualenvBuilder");
        return venv.perform(build, launcher, listener);
    }

    /**
     * Install django jenkins requirements.
     *
     * @param actualTasks
     *            the actual tasks
     * @param enableCoverage
     *            the enable coverage
     * @return the string
     */
    private String installDjangoJenkinsRequirements(
            final EnumSet<Task> actualTasks, final boolean enableCoverage) {
        PrintStream logger = listener.getLogger();
        String pip = PIP_INSTALL + PIP_UPGRADE + DJANGO_JENKINS_REQUIREMENTS;
        if (enableCoverage) {
            pip += " " + COVERAGE_REQUIREMENT;
        }

        for (final Task t : actualTasks) {
            if (t.getRequirements() != null) {
                pip += " " + t.getRequirements();
            } else {
                logger.println("WARINING: Task " + t.getName()
                        + " has non-python requirements.");
            }
        }
        return pip;
    }

    /**
     * Install project requirements.
     * @param requirementsFile 
     *
     * @return the requirements file found
     * @throws InterruptedException, IOException
     */
    private String installProjectRequirements(String requirementsFile) throws InterruptedException,
            IOException {
        PrintStream logger = listener.getLogger();
        if(requirementsFile==null) {
            requirementsFile = "# No project requirements found";
            try {
                requirementsFile = workspace.act(new ProjectRequirementsFinder());
            } catch (final IOException e) {
                logger.println("No requirements file found:");
                logger.println(e.getMessage());
            }
        }
        return PIP_INSTALL + PIP_UPGRADE + " -r " + requirementsFile;
    }

    /**
     * Creates the build package.
     *
     * @param actualTasks
     *            the actual tasks
     * @param projectApps
     *            the project apps
     * @param settingsModule 
     * @return the string
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws InterruptedException
     *             the interrupted exception
     */
    private String createBuildPackage(final EnumSet<Task> actualTasks,
            final String projectApps, final String settingsModule) throws IOException, InterruptedException {

        PrintStream logger = listener.getLogger();

        String actualProjectApps = projectApps;
        String actualSettings = settingsModule;
        final FilePath djModule = new FilePath(workspace,
                DJANGO_JENKINS_MODULE);
        logger.println("Finding Django project settings");
        if (actualSettings == null) {
            logger.println("No settings provided. Trying to find one.");
            actualSettings = workspace
                .act(new DjangoProjectSettingsFinder());
        }

        logger.println("Creating Build Package");
        if (!djModule.act(new CreateBuildPackage())) {
            throw new IOException("Could not create Build Package.");
        }

        if ((actualProjectApps == null)
                || (actualProjectApps.trim().length() == 0)) {
            logger.println("No project apps provided. Trying to find some");
            actualProjectApps = workspace.act(new ProjectApplicationsFinder());
        }

        logger.println("Creating jenkins settings module");
        if (!djModule.act(new CreateDjangoModuleSettings(actualSettings,
                actualTasks, actualProjectApps))) {
            throw new IOException("Could not create jenkins setting module.");
        }

        logger.println("Returning settings: " + settingsModule);
        return "export DJANGO_SETTINGS_MODULE=" + DJANGO_JENKINS_MODULE + "."
                + DJANGO_JENKINS_SETTINGS;
    }
}
