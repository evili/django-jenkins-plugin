package org.jenkinsci.plugins.django;

import static org.junit.Assert.assertSame;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class TestCreateBuildPackage {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private PrintStream logger;

	@Mock
	private VirtualChannel channel;

	File invokeFile;
	private CreateBuildPackage bPackage;


	@Before
	public void setUp() throws IOException {
		bPackage = new CreateBuildPackage(logger);
	}

	@Test
	public void testCreateBuildPackage() throws Exception {
		assertSame("Logger ", logger, bPackage.getLogger());
	}

	@Test
	public void testInvoke() throws Exception {
		invokeFile = new File(System.getProperty("java.io.tmpdir"));
		bPackage.invoke(invokeFile, channel);
	}
}
