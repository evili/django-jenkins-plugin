package org.jenkinsci.plugins.django;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import hudson.remoting.VirtualChannel;

import java.io.File;

import org.jenkinsci.remoting.Role;
import org.jenkinsci.remoting.RoleChecker;
import org.jenkinsci.remoting.RoleSensitive;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class TestProjectApplicationsFinder {

	private ProjectApplicationsFinder pAFinder;

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Mock
	private RoleChecker checker;
	@Mock
	private VirtualChannel channel;
	@Mock
	private File dir;

	@Before
	public void setUp() {
		doThrow(new SecurityException()).when(checker).check(any(RoleSensitive.class), any(Role.class));
		pAFinder = new ProjectApplicationsFinder();
	}

	@Test
	public void testProjectApplicationsFinder() {
		assertNotNull("Null ProjectApplicationsFinder!", pAFinder);
	}

	@Test
	public void testCheckRoles() throws Exception {
		try {
			pAFinder.checkRoles(checker);
			fail("checkRoles should fail here");
		}
		catch(SecurityException e) {
			e.getMessage();
		}
	}

	@Test
	public void testInvoke() throws Exception {
		pAFinder.invoke(dir, channel);
	}
}
