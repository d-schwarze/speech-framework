package de.speech.core.postprocessing;

import de.speech.core.annotation.adaptions.postprocessing.PostProcessFactory;

@PostProcessFactory
public class TestPostProcessFactory implements IPostProcessFactory {

    @Override
    public IPostProcess createPostProcess() {
        return new TestPostProcess();
    }
}
