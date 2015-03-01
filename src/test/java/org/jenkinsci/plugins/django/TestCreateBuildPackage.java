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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
/**
 * Tests for {@link CreateBuildPackage}.
 * @author Evili del Rio
 *
 */
public class TestCreateBuildPackage {
    /** Temporary folder. */
    @Rule
    public TemporaryFolder folderRule = new TemporaryFolder();
    /** {@link org.mockito.junit.Mockito} rule. */
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    /** Mocked {@link VirtualChannel}. */
    @Mock
    private VirtualChannel channel;
    /** {@link CreateBuildPackage} to test. */
    private CreateBuildPackage bPackage;

    /** Set up tests. */
    @Before
    public final void setUp() throws IOException {
        bPackage = new CreateBuildPackage();
    }

    /** Test new() CreateBuildPackage.
     *  @throws Exception if something fails.
     */
    @Test
    public final void testCreateBuildPackage() throws Exception {
        assertNotNull("CreateBuildPackage should not be null.", bPackage);
    }

    /** Test invoke() method.
     *  @throws Exception if something fails.
     */
    @Test
    public final void testInvoke() throws Exception {
        final File invokeFile = folderRule.newFolder();
        assertTrue("Real path should build package",
                bPackage.invoke(invokeFile, channel));
        folderRule.delete();
        assertFalse("Null path should fail",
                bPackage.invoke(new File("/dev/null"), channel));
    }
}
