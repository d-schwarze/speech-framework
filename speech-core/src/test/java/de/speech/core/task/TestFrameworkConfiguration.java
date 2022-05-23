package de.speech.core.task;

import de.speech.core.framework.IFramework;
import de.speech.core.task.implementation.FrameworkConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestFrameworkConfiguration {
    FrameworkConfiguration frameworkConfiguration;

    @BeforeEach
    void setUp() {
        List<String> preProcesses = new ArrayList<>();
        preProcesses.add("preProcess1");
        preProcesses.add("preProcess2");
        preProcesses.add("preProcess3");
        frameworkConfiguration = new FrameworkConfiguration(new MockFramework(), preProcesses);
    }

    @Test
    void testGetPreProcesses() {
        assert (frameworkConfiguration.getPreprocesses().size() == 3);
    }

    @Test
    void testGetFramework() {
        assertEquals("identifier", frameworkConfiguration.getFramework().getIdentifier());
        assertEquals("model", frameworkConfiguration.getFramework().getModel());
    }

    private class MockFramework implements IFramework {

        @Override
        public String getModel() {
            return "model";
        }

        @Override
        public String getIdentifier() {
            return "identifier";
        }
    }
}
