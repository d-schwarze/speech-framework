package de.speech.core.task.result;

import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextServiceMetadata;

import java.util.List;
import java.util.Map;

public class SpeechToTextServiceData implements ISpeechToTextServiceData {

    private final String recognizedSentence;
    private final List<Map<String, Double>> probabilities;
    private final String framework;
    private final String model;
    private final String frameworkJson;
    private final List<String> preprocesses;

    public SpeechToTextServiceData(ISpeechToTextServiceMetadata metadata, List<String> preprocesses) {
        this.recognizedSentence = metadata.getRecognizedSentence();
        this.probabilities = metadata.getProbabilitiesPerRecognizedWord();
        this.framework = metadata.getFramework();
        this.model = metadata.getModel();
        this.frameworkJson = metadata.getFrameworkDependentJson();
        this.preprocesses = preprocesses;
    }

    @Override
    public List<String> getPreprocesses() {
        return preprocesses;
    }

    @Override
    public String getRecognizedSentence() {
        return recognizedSentence;
    }

    @Override
    public List<Map<String, Double>> getProbabilitiesPerRecognizedWord() {
        return probabilities;
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
        return frameworkJson;
    }
}
