package org.jenkinsci.plugins.django;

public abstract class AbstractDjangoTestCase extends HudsonTestCase {


    protected FreeStyleProject setupProject()
        throws Exception {
        FreeStyleProject project = createFreeStyleProject();
        return project;
    }
    
};
