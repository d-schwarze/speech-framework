package de.speech.core.application;

import de.speech.core.application.execution.RuntimeUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RuntimeUtilTest {

    @Test
    public final void testAvailableProcessors_WithMax() {
        assertEquals(1, RuntimeUtil.getAvailableProcessors(1));
    }

}
