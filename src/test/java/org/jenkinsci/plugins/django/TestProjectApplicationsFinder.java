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

public class TestProjectApplicationsFinder {

	private ProjectApplicationsFinder pAFinder;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private VirtualChannel channel;

	@Before
	public void setUp() {
		pAFinder = new ProjectApplicationsFinder();
	}

	@Test
	public void testProjectApplicationsFinder() {
		assertNotNull("Null ProjectApplicationsFinder!", pAFinder);
	}

	@Test
	public void testInvoke() throws Exception {
		String appName = "appwithviews";
		File dir = folder.newFolder(appName);
		folder.newFile(appName+File.separator+"views.py");
		String found = pAFinder.invoke(dir, channel);
		assertEquals("We should find the testapp.", appName, found);
	}
}
