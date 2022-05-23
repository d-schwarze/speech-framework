package de.speech.core.test.expected;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ExpectedResultFinderTest {

    @Test
    public final void test() throws FileNotFoundException {
        ExpectedResultFinder finder = new ExpectedResultFinder();
        List<ExpectedResult> results = finder.getExpectedResults();

        assertEquals(14, results.size());
    }

}
