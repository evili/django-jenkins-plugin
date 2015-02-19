
package org.jenkinsci.plugins.django;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Task {
	PEP8      ("PEP8",      "django_jenkins.tasks.run_pep8",      "pep8"),
	PYLINT    ("PYLINT",    "django_jenkins.tasks.run_pylint",    "pylint"),
	PYFLAKES  ("PYFLAKES",  "django_jenkins.tasks.run_pyflakes",  "pyflakes"),
	FLAKE8    ("FLAKE8",    "django_jenkins.tasks.run_flake8",    "flake8" ),
	JSHINT    ("JSHINT",    "django_jenkins.tasks.run_jshint",     null),
	CSSLINT   ("CSSLINT",   "django_jenkins.tasks.run_csslint",    null),
	SLOCCOUNT ("SLOCCOUNT", "django_jenkins.tasks.run_sloccount",  null);

	private final String name;
	private final String pythonPackage;
	private final String requirements;

	Task(String name, String pythonPackage, String requirements) {
		this.name = name;
		this.pythonPackage = pythonPackage;
		this.requirements = requirements;
	}

	public String getName() {
		return name;
	}
	public String getPythonPackage() {
		return pythonPackage;
	}
	public String getRequirements() {
		return requirements;
	}
	public String toString() {
		return name;
	}
	private static Map<String, Task> taskMap;

	static {
		taskMap = new HashMap<String, Task>();
		for(Task t: EnumSet.allOf(Task.class)) {
			taskMap.put(t.getName(), t);
		}
	}

	public static Task getTask(String name) {
		return taskMap.get(name);
	}
}
