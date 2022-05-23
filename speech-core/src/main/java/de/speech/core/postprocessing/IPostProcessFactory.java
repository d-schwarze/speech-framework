package de.speech.core.postprocessing;

/**
 * Factory for {@link IPostProcess}es.
 * To use an implementation of {@link IPostProcess} you have to implement this interface to
 * create instances of the IPostProcess-implementation.
 */
public interface IPostProcessFactory {

    /**
     * Factory method that creates new instance of {@link IPostProcess} implementation
     * @return new instance of IPostProcess-implementation
     */
    IPostProcess createPostProcess();

}
