package org.jenkinsci.plugins.django;

import static org.junit.Assert.assertEquals;
import hudson.remoting.VirtualChannel;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class TestProjectRequirementsFinder {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    private VirtualChannel channel;

    ProjectRequirementsFinder finder;

    @Before
    public void setUp() {
        finder = new ProjectRequirementsFinder();
    }
    @Test
    public void testInvoke() throws Exception {
        final String reqs = "requirements.txt";
        final String sDir = "build";
        final File dir = folder.newFolder(sDir);
        folder.newFile(sDir+File.separator + reqs);
        final String found = finder.invoke(dir, channel);
        assertEquals("We should find requirements file.", reqs, found);
    }

}
