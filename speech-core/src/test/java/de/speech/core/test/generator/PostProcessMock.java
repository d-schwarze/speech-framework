package de.speech.core.test.generator;

import de.speech.core.postprocessing.IPostProcess;
import de.speech.core.task.result.ISpeechToTextServiceData;

import java.util.List;

public class PostProcessMock implements IPostProcess {
    @Override
    public String process(List<ISpeechToTextServiceData> inputData) {
        return inputData.get(0).getRecognizedSentence();
    }
}
