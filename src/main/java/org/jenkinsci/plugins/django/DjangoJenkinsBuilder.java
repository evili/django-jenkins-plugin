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

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import net.sf.json.JSONObject;

import org.jvnet.localizer.Localizable;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Builds a jenkins job for Django projects.
 */
public class DjangoJenkinsBuilder extends Builder implements Serializable {
    /** (non-Javadoc) @see java.io.Serializable#serialVersionUID. */
    private static final long serialVersionUID = 5L;
    /** Display name of this plugin. */
    public static final String DISPLAY_NAME = "Django Jenkins Builder";
    /** Default django-jenkins task list. */
    public static final EnumSet<Task> DEFAULT_TASKS = EnumSet
            .noneOf(Task.class);
    /** Debug Logger. */
    static final Logger LOGGER = Logger.getLogger(DjangoJenkinsBuilder.class
            .getName());
    /** Log size. */
    private static final int LOG_SIZE = 1024 * 1024;
    /** Django-jenkins task list. */
    private final EnumSet<Task> tasks;
    /** Project applications to be tested. */
    private final String projectApps;
    /** Django settings module to use */
    private final String settingsModule;
    /** Pip requirements to use */
    private final String requirementsFile;
    /** Enable coverage tool. */
    private final boolean enableCoverage;


    static {
        /*
         * By default, add any django-jenkins tasks that only depends on
         * python-pip packages except flake8 (incompatible with pyflakes).
         */
        EnumSet<Task> defultSet = EnumSet.complementOf(EnumSet.of(Task.FLAKE8));
        for (final Task t : defultSet) {
            if (t.getRequirements() != null) {
                DEFAULT_TASKS.add(t);
            }
        }
        FileHandler h;
        final SimpleFormatter f = new SimpleFormatter();
        try {
            h = new FileHandler("%t/django-jenkins-builder.log", LOG_SIZE,
                    2, true);
            h.setLevel(Level.ALL);
            h.setFormatter(f);
            LOGGER.addHandler(h);
        } catch (final SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LOGGER.setLevel(Level.ALL);
    }

    /*
     * (non-Javadoc)
     * @see hudson.tasks.Builder#getDescriptor()
     */
    @Override
    public final Descriptor<Builder> getDescriptor() {

        LOGGER.info("Returning descriptor");
        return super.getDescriptor();
    }

    /**
     * Implementation of {@link Descriptor}.
     */
    @Extension
    public static final class DescriptorImpl extends
            BuildStepDescriptor<Builder> {
        /** Server default django-jenkins task list. */
        private EnumSet<Task> defaultTasks = DjangoJenkinsBuilder.DEFAULT_TASKS;

        /*
         * (non-Javadoc)
         * @see
         * hudson.model.Descriptor#configure(org.kohsuke.stapler.StaplerRequest,
         * net.sf.json.JSONObject)
         */
        @Override
        public boolean configure(final StaplerRequest req,
                final JSONObject json) throws FormException {
            LOGGER.info("In configure:");
            LOGGER.info("JSON: " + json.toString(2));
            try {
                final JSONObject newJSONDefaults = json
                        .getJSONObject("defaultTasks");
                final Iterator<?> it = newJSONDefaults.keys();
                final EnumSet<Task> newDefaults = EnumSet.noneOf(Task.class);
                while (it.hasNext()) {
                    final String task = (String) it.next();
                    final boolean checked = newJSONDefaults.getBoolean(task);
                    if (checked) {
                        newDefaults.add(Task.getTask(task));
                    }
                }
                defaultTasks = newDefaults;
            } catch (final Exception e) {
                LOGGER.info("Could not configure Builder: " + e.getMessage());
            }
            LOGGER.info("Saving...");
            save();
            LOGGER.info("Returning super.configure");
            return super.configure(req, json);
        }

        /**
         * Gets the default tasks.
         *
         * @return the default tasks
         */
        public EnumSet<Task> getDefaultTasks() {
            LOGGER.info("Returning default tasks: " + defaultTasks);
            return defaultTasks;
        }

        /*
         * (non-Javadoc)
         * @see hudson.tasks.BuildStepDescriptor#isApplicable(java.lang.Class)
         */
        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(
                final Class<? extends AbstractProject> aClass) {
            LOGGER.info("Yes, we're applicable!");
            return true;
        }

        /*
         * (non-Javadoc)
         * @see hudson.model.Descriptor#getDisplayName()
         */
        @Override
        public String getDisplayName() {
            Localizable displayName =
                    Messages._DjangoJenkinsBuilder_DisplayName();
            LOGGER.info("How We are called " + displayName);
            return displayName.toString();
        }
    }

    /**
     * Instantiates a new django-jenkins builder.
     *
     * @param tasks
     *            Django-jenkins tasks to be run.
     * @param projectApps
     *            Django project applications to be analyzed.
     * @param settingsModule
     *            Django settings module under which the tests are run.
     * @param requirementsFile 
     *            PIP requirements file to install dependencies for tests.
     * @param enableCoverage
     *            Enable coverage tool analysis.
     */
    @DataBoundConstructor
    public DjangoJenkinsBuilder(final EnumSet<Task> tasks,
            final String projectApps, String settingsModule, String requirementsFile, final boolean enableCoverage) {
        LOGGER.info("In Constructor");
        // this.tasks = noTasks;
        this.tasks = tasks;
        this.projectApps = projectApps;
        this.settingsModule = settingsModule;
        this.requirementsFile = requirementsFile;
        this.enableCoverage = enableCoverage;
    }

    /**
     * Gets the tasks.
     *
     * @return the tasks
     */
    public final EnumSet<Task> getTasks() {
        LOGGER.info("Returning tasks: " + tasks);
        return tasks;
    }

    /**
     * Gets the project applications.
     *
     * @return the project applications.
     */
    public final String getProjectApps() {
        return projectApps;
    }

    /**
     * Gets the settings module.
     *
     * @return the settings module.
     */
    public String getSettingsModule() {
        return settingsModule;
    }

    /**
     * Gets the requirements file.
     *
     * @return the requirements file name.
     */
    public String getRequirementsFile() {
        return requirementsFile;
    }

    /**
     * Checks if is enable coverage.
     *
     * @return true, if coverage is enabled.
     */
    public final boolean isEnableCoverage() {
        return enableCoverage;
    }

    /*
     * (non-Javadoc)
     * @see
     * hudson.tasks.BuildStepCompatibilityLayer#perform(
     * hudson.model.AbstractBuild, hudson.Launcher, hudson.model.BuildListener)
     */
    @Override
    public final boolean perform(final AbstractBuild<?, ?> build,
            final Launcher launcher, final BuildListener listener)
            throws InterruptedException, IOException {

        final PrintStream logger = listener.getLogger();

        logger.println("Performing Django-Jenkins build");

        boolean status = false;

        final PythonVirtualenv venv = new PythonVirtualenv(build, launcher,
                listener);

        try {
            logger.println("Calling VirtualEnv Builder");
            final EnumSet<Task> actualTasks;
            if ((tasks == null) || (tasks.size() == 0)) {
                actualTasks = DEFAULT_TASKS;
            } else {
                actualTasks = tasks;
            }
            status = venv.perform(actualTasks, projectApps, settingsModule, requirementsFile, enableCoverage);
        } catch (final Exception e) {
            logger.println("Something went wrong: " + e.getMessage());
            status = false;
        }
        if (status) {
            logger.println("Success");
        } else {
            logger.println("Build failed <:-( ");
        }
        return status;
    }
}
