package de.speech.core.postprocessing;

import de.speech.core.task.result.ISpeechToTextServiceData;

import java.util.List;

public class TestPostProcess implements IPostProcess {

    @Override
    public String process(List<ISpeechToTextServiceData> inputData) {
        return "test";
    }

}
