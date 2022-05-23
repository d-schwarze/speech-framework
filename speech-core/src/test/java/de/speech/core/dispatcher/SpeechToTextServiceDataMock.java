package de.speech.core.dispatcher;

import de.speech.core.task.result.ISpeechToTextServiceData;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SpeechToTextServiceDataMock implements ISpeechToTextServiceData, Serializable {

    private final String framework;
    private final String model;
    private final String sentence;

    public SpeechToTextServiceDataMock(String framework, String Model, String sentence) {
        this.framework = framework;
        this.model = Model;
        this.sentence = sentence;
    }

    @Override
    public List<String> getPreprocesses() {
        return null;
    }

    @Override
    public String getRecognizedSentence() {
        return sentence;
    }

    @Override
    public List<Map<String, Double>> getProbabilitiesPerRecognizedWord() {
        return null;
    }

    @Override
    public String getFramework() {
        return framework;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public String getFrameworkDependentJson() {
        return null;
    }
}
