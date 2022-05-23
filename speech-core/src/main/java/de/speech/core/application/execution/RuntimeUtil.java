package de.speech.core.application.execution;

public class RuntimeUtil {

    /**
     * Gets the amount of available cores. You may specify a maximum value in case there are to many cores
     * available as needed.
     * @param max in case more cores are available that max, max is returned
     * @return available cores <= max
     */
    public static int getAvailableProcessors(int max) {
        int cores = getAvailableProcessors();

        return Math.min(cores, max);
    }

    /**
     * Gets the amount of available cores.
     */
    public static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }
}
