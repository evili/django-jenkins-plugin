
package org.jenkinsci.plugins.django;

public enum Task {
	PEP8      ("PEP8",      "django_jenkins.tasks.run_pep8"),
	PYLINT    ("PYLINT",    "django_jenkins.tasks.run_pylint"),
	PYFLAKES  ("PYFLAKES",  "django_jenkins.tasks.run_pyflakes"),
	FLAKE8    ("FLAKE8",    "django_jenkins.tasks.run_flake8"),
	JSHINT    ("JSHINT",    "django_jenkins.tasks.run_jshint"),
	CSSLINT   ("CSSLINT",   "django_jenkins.tasks.run_csslint"),
	SLOCCOUNT ("SLOCCOUNT", "django_jenkins.tasks.run_sloccount");
	private final String name;
	private final String pythonPackage;

	Task(String name, String pythonPackage) {
		this.name = name;
		this.pythonPackage = pythonPackage;
	}

	public String getName() {
		return name;
	}
	public String getPythonPackage() {
		return pythonPackage;
	}
	public String toString() {
		return name;
	}
}
