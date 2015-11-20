package org.jenkinsci.plugins.django;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import hudson.Plugin;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;
import org.mockito.Mock;


public class TestDjangoJenkinsBuilder {

    @Rule
    public JenkinsRule jRule = new JenkinsRule();

    private String projectApps;
    private String settingsModule;
    private String requirementsFile;
    private boolean enableCoverage;
    
    
    @Mock
    private StaplerRequest staplerRequest;

    

    public DjangoJenkinsBuilder getBuilder() {
        return new DjangoJenkinsBuilder(DjangoJenkinsBuilder.DEFAULT_TASKS, 
                projectApps, 
                settingsModule,
                requirementsFile,
                enableCoverage);
    }

    public void checkDefaultTasks(final DjangoJenkinsBuilder builder,
            final EnumSet<Task> defaults) throws Exception {
        final DjangoJenkinsBuilder.DescriptorImpl desc =
                (DjangoJenkinsBuilder.DescriptorImpl) builder.getDescriptor();
        final EnumSet<Task> tasks = desc.getDefaultTasks();
        assertEquals("Default tasks should be equal.", defaults, tasks);
    }

    @Before
    public void setUp() {
        settingsModule = null;
        requirementsFile = null;
        projectApps = "items";
        enableCoverage = true;
    }

    @Test
    public void testGlobalConfig() throws Exception {
        final Plugin djPlugin = jRule.getPluginManager().getPlugin("django").getPlugin();
        assertThat("Django Plugin sould not be null.", djPlugin, notNullValue());
    }

    @Test
    public void testDisableCoverage() throws Exception {
        enableCoverage = false;
        assertFalse("Builder should have coverage disabled", getBuilder().isEnableCoverage());
    }

    @Test
    public void testRoundTrip() throws Exception {
        settingsModule = "bdd_tests.bdd_setings";
        DjangoJenkinsBuilder before = getBuilder();
        DjangoJenkinsBuilder after = jRule.configRoundtrip(before);
        jRule.assertEqualBeans(before, after, "tasks,projectApps,enableCoverage");
        enableCoverage = false;
        before = getBuilder();
        after = jRule.configRoundtrip(before);
        jRule.assertEqualBeans(before, after, "tasks,projectApps,enableCoverage");

    }

    @Test
    public void testConfigure() throws Exception {
        final Map<String, Boolean> taskMap = new HashMap<String, Boolean>();
        final EnumSet<Task> changedDefaults =
                EnumSet.complementOf(DjangoJenkinsBuilder.DEFAULT_TASKS);
        for (final Task t: changedDefaults) {
            taskMap.put(t.getName(), true);
        }
        final JSONObject json = new JSONObject();
        json.put("defaultTasks", JSONObject.fromObject(taskMap));
        final DjangoJenkinsBuilder builder = getBuilder();

        assertTrue("Global config has failed", builder.getDescriptor().configure(staplerRequest, json));
        checkDefaultTasks(builder , changedDefaults);
    }

    @Test
    public void testSettingsModule() throws Exception {
        settingsModule = "bdd_settings";
        final DjangoJenkinsBuilder builder = getBuilder();
        assertEquals(builder.getSettingsModule(), settingsModule);
    }

    @Test
    public void testRequirementsFile() throws Exception {
        requirementsFile = "test_requirements.txt";
        final DjangoJenkinsBuilder builder = getBuilder();
        assertEquals(builder.getRequirementsFile(), requirementsFile);
    }
        
    @Test
    public void testGetDefaultTasks() throws Exception {
        checkDefaultTasks(getBuilder(), DjangoJenkinsBuilder.DEFAULT_TASKS);
    }
}
