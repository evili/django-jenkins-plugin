package org.jenkinsci.plugins.django;

import static org.junit.Assert.assertSame;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.jenkinsci.remoting.RoleChecker;
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
	private File file;
	@Mock
	private PrintStream logger;
	@Mock
	RoleChecker checker;
	@Mock
	File invokeFile;
	@Mock
	private VirtualChannel channel;

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
		bPackage.invoke(invokeFile, channel);
	}

	@Test
	public void testCheckRoles() throws Exception {
		bPackage.checkRoles(checker);
	}
}
