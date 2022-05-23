package de.speech.core.annotation.annotatedtestclasses;

import de.speech.core.annotation.adaptions.postprocessing.PostProcessFactory;
import de.speech.core.postprocessing.IPostProcess;
import de.speech.core.postprocessing.IPostProcessFactory;

@PostProcessFactory
public class TestPostProcessFactory2 implements IPostProcessFactory {

    public final IPostProcess POST_PROCESS_OBJ = inputData -> "testPostProcessFactory2";

    @Override
    public IPostProcess createPostProcess() {
        return POST_PROCESS_OBJ;
    }
}
