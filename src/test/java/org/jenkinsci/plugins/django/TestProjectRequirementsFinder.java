package org.jenkinsci.plugins.django;

import static org.junit.Assert.*;
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
		String reqs = "requirements.txt";
		String sDir = "build";
		File dir = folder.newFolder(sDir);
		folder.newFile(sDir+File.separator+reqs);
		String found = finder.invoke(dir, channel);
		assertEquals("We should find requirements file.", reqs, found);
	}

}
