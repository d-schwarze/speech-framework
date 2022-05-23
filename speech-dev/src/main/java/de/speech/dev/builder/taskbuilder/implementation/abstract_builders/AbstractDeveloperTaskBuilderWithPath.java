package de.speech.dev.builder.taskbuilder.implementation.abstract_builders;

import de.speech.core.logging.Loggable;
import de.speech.core.task.IAudioRequest;
import de.speech.core.task.implementation.audioRequest.AudioRequestWithPath;
import de.speech.core.task.implementation.audioRequest.DeveloperAudioRequest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Abstract class that extends AbstractTaskBuilderWithPath and adds the possibility to add Strings to every AudioRequest that
 * defines what the actual text of this AudioRequest is
 * @param <B>
 */
public abstract class AbstractDeveloperTaskBuilderWithPath<B extends AbstractDeveloperTaskBuilderWithPath<B>> extends AbstractTaskBuilderWithPath<B> implements Loggable {
    protected String textPath;

    /**
     * creates a new instance of TaskBuilder and dynamically sets the taskId for the buildTask method.
     * The file defined by the path has to be a textfile containing the text separated by a new line.
     * The separator used to distinguish the requestName from the actualText is a "//".
     *
     * nameOfAudioRequest1 // actualTextOfAudioRequest1AsString
     * nameOfAudioRequest2 // actualTextOfAudioRequest2AsString
     *  .
     *  .
     *  .
     *
     * @param audioPath the String that defines the Path to the audioFiles
     * @param textPath the String that defines the Path to the textFile
     */
    public AbstractDeveloperTaskBuilderWithPath(String audioPath, String textPath) {
        super(audioPath);
        this.textPath = textPath;
    }

    /**
     * The file defined by the path has to be a textfile containing the text separated by a new line.
     * The separator used to distinguish the requestName from the actualText is a "//".
     *
     * nameOfAudioRequest1 // actualTextOfAudioRequest1AsString
     * nameOfAudioRequest2 // actualTextOfAudioRequest2AsString
     *  .
     *  .
     *  .
     *
     * @param textPath the String that defines the Path to the textFile
     * @return this (see Builder-Pattern for more information)
     */
    public B setTextPath(String textPath) {
        this.textPath = textPath;

        return (B) this;
    }


    /**
     * creates the new AudioRequests of the Task
     * @return  the AudioRequests of the Task
     */
    @Override
    protected List<IAudioRequest> buildAudioRequests() {
        long requestId = 0;
        File dir = new File(path);
        File textFile = new File(textPath);
        List<IAudioRequest> requests = new ArrayList<>();
        Map<String,String> textMap = new HashMap<>();

        //creating map for texts
        try {
            FileReader reader = new FileReader(textFile.getAbsolutePath());
            BufferedReader br = new BufferedReader(reader);
            textMap = addActualTextToRequestName(br);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "There was an error while reading the file for the actual texts.", e);
        }
        //Creating devRequests with textMap
        if (dir.exists()) {
            if (dir.isDirectory()) {
                createAudioRequest(dir.listFiles(), requests, requestId, textMap);
            } else {
                addSingleRequest(requestId, dir, requests, textMap);
            }
        } else {
            LOGGER.log(Level.WARNING, "Der angegebene Pfad existiert nicht.");
        }


        return requests;
    }

    private void addSingleRequest(long requestId, File dir, List<IAudioRequest> requests, Map<String, String> textMap) {
        String absolutePathOfFile = dir.getAbsolutePath();
        String fileName = dir.getName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String actualText = textMap.get(fileName);
        if (actualText == null) {
            actualText = "";
        }
        DeveloperAudioRequest devAudioRequest = new DeveloperAudioRequest(new AudioRequestWithPath(requestId, absolutePathOfFile), actualText);

        requests.add(devAudioRequest);
    }

    private Map<String, String> addActualTextToRequestName(BufferedReader br) throws IOException {
        Map<String, String> results = new HashMap<>();
        String line = br.readLine();
        while (line != null) {

            if (!line.equals("")) {
                String[] splitString = line.split("//",2);
                int len = splitString.length;

                if (len == 2) {
                    splitString[0] = trimFirst(splitString[0]);
                    splitString[1] = trimSecond(splitString[1]);
                    results.put(splitString[0], splitString[1]);
                }

            }
            line = br.readLine();
        }

        return results;
    }

    private String trimFirst(String string) {
        int len = string.length();
        if (len > 0) {
            if (string.charAt(len - 1) == ' ') {
                string = string.substring(0, len - 1);
            }
        }
        return string;
    }

    private String trimSecond(String string) {
        if (string.length() > 1) {
            if (string.charAt(0) == ' ') {
                string = string.substring(1);
            }
        }
        return string;
    }

    private void createAudioRequest(File[] files, List<IAudioRequest> requests, long requestId, Map<String, String> textMap) {
        Arrays.sort(files);
        for (File file : files) {
            if (file.isDirectory()) {
                createAudioRequest(file.listFiles(), requests, requestId, textMap);
            } else {
                addSingleRequest(requestId, file, requests, textMap);


                requestId++;
            }
        }
    }
}
