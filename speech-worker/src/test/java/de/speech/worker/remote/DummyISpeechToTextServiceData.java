package de.speech.worker.remote;

import de.speech.core.task.result.ISpeechToTextServiceData;

import java.util.*;

public class DummyISpeechToTextServiceData implements ISpeechToTextServiceData {
    @Override
    public List<String> getPreprocesses() {
        return Collections.singletonList("dummy");
    }

    @Override
    public String getRecognizedSentence() {
        return "dummy sentence";
    }

    @Override
    public List<Map<String, Double>> getProbabilitiesPerRecognizedWord() {
        HashMap<String, Double> firstWord = new HashMap<>();
        firstWord.put("dummy", 99.9);
        firstWord.put("dumy", 10.3);
        HashMap<String, Double> secondWord = new HashMap<>();
        secondWord.put("text", 99.3);
        secondWord.put("tex", 5.3);
        return Arrays.asList(firstWord, secondWord);
    }

    @Override
    public String getFramework() {
        return "dummy framework";
    }

    @Override
    public String getModel() {
        return "dummy model";
    }

    @Override
    public String getFrameworkDependentJson() {
        return "{}";
    }
}
