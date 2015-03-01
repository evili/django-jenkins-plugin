package org.jenkinsci.plugins.django;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestTask {
    private static final String PYFLAKES_PACKAGE = "django_jenkins.tasks.run_pyflakes";

    @Test
    public void testGetName() {
        final Task t = Task.PEP8;
        assertEquals("PEP8 name should be PEP8!", "PEP8", t.getName());
    }

    @Test
    public void testGetPythonPackage() {
        final Task t = Task.PYFLAKES;
        assertEquals("PYFLAKES package should be: ", PYFLAKES_PACKAGE, t.getPythonPackage());
    }

    @Test
    public void testToString() {
        final Task t = Task.FLAKE8;
        assertEquals("FLAKE8 String should retuen FLAKE8", "FLAKE8", t.toString());
    }
}
