package org.jenkinsci.plugins.django;

import static org.junit.Assert.*;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class TestCreateBuildPackage {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private VirtualChannel channel;

	private CreateBuildPackage bPackage;


	@Before
	public void setUp() throws IOException {
		bPackage = new CreateBuildPackage();
	}

	@Test
	public void testCreateBuildPackage() throws Exception {
		assertNotNull("CreateBuildPackage should not be null.", bPackage);
	}

	@Test
	public void testInvoke() throws Exception {
		File invokeFile = folder.newFolder();
		assertTrue("Path should be in /tmp", bPackage.invoke(invokeFile, channel));
	}
}
