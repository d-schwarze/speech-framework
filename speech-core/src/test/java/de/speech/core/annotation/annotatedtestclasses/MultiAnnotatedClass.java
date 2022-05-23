package de.speech.core.annotation.annotatedtestclasses;

import de.speech.core.annotation.adaptions.postprocessing.PostProcessFactory;
import de.speech.core.postprocessing.IPostProcess;
import de.speech.core.postprocessing.IPostProcessFactory;

@PostProcessFactory
@TestAnnotation
public class MultiAnnotatedClass implements IPostProcessFactory {

    public final IPostProcess POST_PROCESS_OBJ = inputData -> "multiAnnotatedClass";

    @Override
    public IPostProcess createPostProcess() {
        return POST_PROCESS_OBJ;
    }
}
