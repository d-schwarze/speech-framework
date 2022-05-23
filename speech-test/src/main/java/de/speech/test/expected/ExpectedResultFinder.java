package de.speech.test.expected;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class ExpectedResultFinder {

    private static String DEFAULT_RESULTS_LOCATION = "src/test/resources/clips/wavSet1/actual.json";

    private String resultsLocation;

    private ExpectedResultContainer container;

    public ExpectedResultFinder() throws FileNotFoundException {
        this(DEFAULT_RESULTS_LOCATION);
    }

    public ExpectedResultFinder(String resultsLocation) throws FileNotFoundException {
        this.resultsLocation = resultsLocation;

        this.container = loadExpectedResults(this.resultsLocation);
    }

    private ExpectedResultContainer loadExpectedResults(String location) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(new File(location)));
        ExpectedResultContainer con = gson.fromJson(reader, ExpectedResultContainer.class);

        return con;
    }

    public List<ExpectedResult> getExpectedResults() {
        return container.getExpectedResults();
    }
}
