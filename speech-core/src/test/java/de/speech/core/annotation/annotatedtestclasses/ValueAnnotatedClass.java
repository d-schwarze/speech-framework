package de.speech.core.annotation.annotatedtestclasses;

import de.speech.core.postprocessing.IPostProcess;
import de.speech.core.postprocessing.IPostProcessFactory;

@TestAnnotationWithValue(test = "tut")
public class ValueAnnotatedClass implements IPostProcessFactory {

    public final IPostProcess POST_PROCESS_OBJ = inputData -> "valueAnnotatedClass";

    @Override
    public IPostProcess createPostProcess() {
        return POST_PROCESS_OBJ;
    }
}
