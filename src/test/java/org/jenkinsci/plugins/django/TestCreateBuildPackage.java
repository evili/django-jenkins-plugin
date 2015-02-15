package org.jenkinsci.plugins.django;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
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
	private File mockFile;
	@Mock
	private PrintStream mockLogger;
	@Mock
	private CreateBuildPackage mockBPackage;
	@Mock
	RoleChecker checker;
	@Mock
	File invokeFile;
	@Mock
	private VirtualChannel channel;



	@Before
	public void setUp() throws IOException {
		when(mockBPackage.getLogger()).thenReturn(mockLogger);
	}

	@Test
	public void testCreateBuildPackage() throws Exception {
		assertSame("Logger ", mockLogger, mockBPackage.getLogger());
	}
	@Test
	public void testInvoke() throws Exception {
		mockBPackage.invoke(invokeFile, channel);
	}
	@Test
	public void testCheckRoles() throws Exception {
		mockBPackage.checkRoles(checker);
	}
}
