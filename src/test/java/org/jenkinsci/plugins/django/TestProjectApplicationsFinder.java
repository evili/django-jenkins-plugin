package org.jenkinsci.plugins.django;

import static org.junit.Assert.assertNotNull;
import hudson.remoting.VirtualChannel;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class TestProjectApplicationsFinder {

	private ProjectApplicationsFinder pAFinder;

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
		File dir = new File(System.getProperty("java.io.tmpdir"));
		pAFinder.invoke(dir, channel);
	}
}
