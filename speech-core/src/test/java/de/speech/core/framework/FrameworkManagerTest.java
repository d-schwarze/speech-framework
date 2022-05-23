package de.speech.core.framework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FrameworkManagerTest {

    @Test
    public void testFindFramework_FrameworkNotManaged_NewFramework() {

        IFramework framework = FrameworkManager.getInstance().findFramework("newId", "newModel");
        assertNotNull(framework);

        IFramework managedFramework = FrameworkManager.getInstance().findFramework("newId", "newModel");

        assertSame(framework, managedFramework);
    }

}
