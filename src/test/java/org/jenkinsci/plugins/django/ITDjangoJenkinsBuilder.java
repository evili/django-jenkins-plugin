/*
 * The MIT License
 *
 * Copyright (c) 2015, Evili del Rio i Silvan.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.django;

import static org.junit.Assert.assertTrue;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.plugins.git.GitSCM;

import java.util.EnumSet;

import jenkins.scm.DefaultSCMCheckoutStrategyImpl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Integration tests for {@link DjangoJenkinsBuilder}.
 */
public class ITDjangoJenkinsBuilder {

    /** Django simple test project. */
    private static final String DJANGO_TEST_PROJECT_GIT_URL =
            "https://github.com/evili/django_test_deploy.git";
    /** Jenkins Rule @see {@link JenkinsRule}. */
    @Rule
    public JenkinsRule jRule  = new JenkinsRule();

    /**
     * Test that the plugin loads.
     *
     * @throws Exception if any error occurs.
     *
     */
    @Test
    public final void testPluginLoads() throws Exception {
        final FreeStyleProject project = jRule.createFreeStyleProject();
        final DjangoJenkinsBuilder djangoBuilder =
                new DjangoJenkinsBuilder(EnumSet.of(Task.PEP8), "items", null, null, true, null);
        project.getBuildersList().add(djangoBuilder);
        final GitSCM scm = new GitSCM(DJANGO_TEST_PROJECT_GIT_URL);
        project.setScm(scm);
        final DefaultSCMCheckoutStrategyImpl scmCheckoutStrategy =
                new DefaultSCMCheckoutStrategyImpl();
        project.setScmCheckoutStrategy(scmCheckoutStrategy);
        final FreeStyleBuild build = project.scheduleBuild2(1).get();
        final String s = FileUtils.readFileToString(build.getLogFile());
        final String[] lines = StringUtils.split(s, "\n\r");
        final String lastL = lines[lines.length - 1];
        assertTrue("Test Project Build should be successful: '" + lastL + "'",
                lastL.contains("Finished: SUCCESS"));
    }
}
