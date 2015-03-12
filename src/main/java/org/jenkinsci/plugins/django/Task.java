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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Tasks that can be performed by django-jenkins. <li>{@link #PEP8}</li> <li>
 * {@link #PYLINT}</li> <li>{@link #PYFLAKES}</li> <li>{@link #JSHINT}</li> <li>
 * {@link #CSSLINT}</li> <li>{@link #SLOCCOUNT}</li>
 *
 */
public enum Task {

    /** PYLINT code analysis. */
    PYLINT("PYLINT", "django_jenkins.tasks.run_pylint", "pylint"),

    /** PYFLAKES code checker. */
    PYFLAKES("PYFLAKES", "django_jenkins.tasks.run_pyflakes", "pyflakes"),

    /** FLAKE8 wrapper around pyflakes and pep8. */
    FLAKE8("FLAKE8", "django_jenkins.tasks.run_flake8", "flake8"),

    /** PEP8 style guide check tool. */
    PEP8("PEP8", "django_jenkins.tasks.run_pep8", "pep8"),

    /** JSHINT JavaScript quality tool. */
    JSHINT("JSHINT", "django_jenkins.tasks.run_jshint", null),

    /** CSSLINT CSS code quality tool. */
    CSSLINT("CSSLINT", "django_jenkins.tasks.run_csslint", null),

    /** SLOCCOUNT Tool for counting physical Source Lines of Code. */
    SLOCCOUNT("SLOCCOUNT", "django_jenkins.tasks.run_sloccount", null);

    /** Name of the task. */
    private final String name;
    /** Python package to import for the task. */
    private final String pythonPackage;
    /** Python requirement needed for the task. */
    private final String requirements;

    /**
     * Instantiates a new task.
     *
     * @param name
     *            the name of the django-jenkis task
     * @param pythonPackage
     *            the python package to import in the settings module.
     * @param requirements
     *            the requirements (if any) needed to run the task.
     */
    Task(final String name, final String pythonPackage,
            final String requirements) {
        this.name = name;
        this.pythonPackage = pythonPackage;
        this.requirements = requirements;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the python package.
     *
     * @return the python package
     */
    public String getPythonPackage() {
        return pythonPackage;
    }

    /**
     * Gets the python requirements for the task.
     *
     * @return the requirements
     */
    public String getRequirements() {
        return requirements;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    /** Maps task names to Task elements. */
    private static Map<String, Task> taskMap;

    static {
        taskMap = new HashMap<String, Task>();
        for (final Task t : EnumSet.allOf(Task.class)) {
            taskMap.put(t.getName(), t);
        }
    }

    /**
     * Gets the Task element from a given task name.
     *
     * @param name
     *            the name of the task.
     * @return the task element.
     */
    public static Task getTask(final String name) {
        return taskMap.get(name);
    }
}
