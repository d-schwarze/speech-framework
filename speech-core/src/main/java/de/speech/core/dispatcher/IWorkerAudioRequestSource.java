package de.speech.core.dispatcher;

import java.util.concurrent.TimeUnit;

/**
 * Used to get a new audio request for a specific {@linkplain de.speech.core.framework.IFramework}.
 */
public interface IWorkerAudioRequestSource {

    /**
     * Returns the next {@linkplain ICompletableFrameworkAudioRequest}.
     *
     * @return next audio request
     */
    ICompletableFrameworkAudioRequest next() throws InterruptedException;

    /**
     * Returns the next {@linkplain ICompletableFrameworkAudioRequest}.
     *
     * @param time the maximum time to wait
     * @param unit the timeunit
     * @return next request
     */
    ICompletableFrameworkAudioRequest next(long time, TimeUnit unit) throws InterruptedException;

    boolean hasNext();
}
