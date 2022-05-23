package de.speech.worker.local.preprocessing;

import de.speech.worker.loader.LoadingException;
import de.speech.worker.loader.PreProcessLoader;

import javax.sound.sampled.AudioInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main implementation of the {@linkplain IPreProcessor} interface
 */
public class PreProcessor implements IPreProcessor {

    private final Map<String, IPreProcess> preProcessMap;

    /**
     * Creates a new PreProcessor and loads the available {@linkplain IPreProcess}es
     */
    public PreProcessor(Path preprocessesPath) throws LoadingException {
        preProcessMap = new HashMap<>();
        PreProcessLoader loader = new PreProcessLoader(preprocessesPath);
        loader.getPreProcesses().forEach(preProcess -> preProcessMap.put(preProcess.getName(), preProcess));
    }

    /**
     * Mostly used for testing
     *
     * @param preprocesses the preprocesses available
     */
    public PreProcessor(IPreProcess... preprocesses) {
        preProcessMap = new HashMap<>();
        for (IPreProcess preprocess : preprocesses) {
            preProcessMap.put(preprocess.getName(), preprocess);
        }
    }

    @Override
    public AudioInputStream process(AudioInputStream input, List<String> preProcesses) throws IllegalArgumentException {
        if (input == null) {
            throw new NullPointerException("the input can't be null");
        }
        List<IPreProcess> preProcessesToApply = getPreProcesses(preProcesses);
        AudioInputStream result = input;
        for (IPreProcess preProcess : preProcessesToApply) {
            result = preProcess.process(result);
        }
        return result;
    }

    @Override
    public boolean areAllPrePreProcessesAvailable(List<String> preProcesses) {
        return preProcesses.stream().allMatch(preProcessMap::containsKey);
    }

    private List<IPreProcess> getPreProcesses(List<String> preprocessStringList) throws IllegalArgumentException {
        List<IPreProcess> preProcesses = new ArrayList<>();
        for (String preProcessString : preprocessStringList) {
            IPreProcess result = preProcessMap.get(preProcessString);
            if (result == null) {
                throw new IllegalArgumentException("The specified preprocess does not exist");
            }
            preProcesses.add(result);
        }
        return preProcesses;
    }
}
