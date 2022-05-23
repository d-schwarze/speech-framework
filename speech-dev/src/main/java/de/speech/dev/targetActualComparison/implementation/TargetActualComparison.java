package de.speech.dev.targetActualComparison.implementation;

import de.speech.core.logging.Loggable;
import de.speech.core.task.implementation.audioRequest.DeveloperAudioRequest;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResultWithTac;
import de.speech.core.task.result.implementation.FinalTaskResultWithTac;
import de.speech.dev.targetActualComparison.ITargetActualComparison;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Used to compare a FinalTaskResult to the actualText Strings of the DeveloperAudioRequests of this Task.
 */
public class TargetActualComparison implements ITargetActualComparison, Loggable {

    /**
     * Creates a new TargetActualComparison Object with a FinalTaskResult that should get compared.
     * The calculation uses String.Utils.difference()
     */
    public TargetActualComparison() {
    }

    /**
     * Iterates through all Strings that got processed by the PostProcesses compares
     * those to the actualText and calculates how alike they are to the actual text.
     *
     * The output is in the Form:
     * AudioRequest1Id
     * ExpectedText1
     *
     * PostProcessName1
     * calculatedText1
     * difference in percentage
     *
     * PostProcessName2
     * calculatedText2
     * difference in percentage
     *
     * .
     * .
     * .
     *
     *
     * AudioRequest2Id
     * .
     * .
     * .
     * @return The File containing the comparison in the described way.
     */
    @Override
    public File compare(ITaskResult<FinalAudioRequestResult> taskResult, String fileName) {
        if (fileName.equals("")) {
            fileName = UUID.randomUUID().toString() + ".txt";
        }

        File dir = new File(System.getProperty("user.dir") + File.separator + "src/test/resources/targetActualComparisonResult");
        if (!dir.exists()) {
            dir.mkdir();
        }

        File compareFile = new File(System.getProperty("user.dir") + File.separator + "src/test/resources/targetActualComparisonResult/"+ fileName);
        try {
            if (compareFile.createNewFile()) {
                try {
                    writeComparison(compareFile, taskResult);
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "There was an Error while writing the comparison file", e);
                }
            } else {
                LOGGER.log(Level.WARNING, "There was already an existing file which could not be written in.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING,"An Error accrued while creating the File",e);
        }
        return compareFile;
    }

    /**
     * Iterates through all Strings that got processed by the PostProcesses compares
     * those to the actualText and calculates how alike they are to the actual text.
     * @return A new FinalTaskResultWithTac Object that contains the comparisonresults.
     */
    @Override
    public FinalTaskResultWithTac compare(ITaskResult<FinalAudioRequestResult> taskResult) {
        List<FinalAudioRequestResultWithTac> requestResults= new ArrayList<>();

        for (FinalAudioRequestResult requestResult: taskResult.getResults()) {
            Map<String, Float> equalityMap = setEqualityMap(requestResult);
            requestResults.add(new FinalAudioRequestResultWithTac(requestResult, equalityMap));
        }

        return new FinalTaskResultWithTac(taskResult.getTask(), requestResults);
    }



    private Map<String, Float> setEqualityMap(FinalAudioRequestResult requestResult) {
        Map<String, Float> equalityMap = new HashMap<>();
        for (Map.Entry<String, String> entry: requestResult.getPostProcessingResults().entrySet()) {
            String postProcess = entry.getKey();
            String calculatedText = entry.getValue();
            String actualText = (requestResult.getRequest() instanceof DeveloperAudioRequest) ? getActualTextOfRequest(requestResult) : "";

            float equality = calculateEquality(actualText, calculatedText);
            equalityMap.put(postProcess, equality);
        }
        return equalityMap;
    }

    private void writeComparison(File compareFile, ITaskResult<FinalAudioRequestResult> taskResult) throws IOException {
        FileWriter writer = new FileWriter(compareFile.getAbsolutePath());

        for(FinalAudioRequestResult requestResult: taskResult.getResults()) {
            Map<String, String> results = requestResult.getPostProcessingResults();
            String actualText = (requestResult.getRequest() instanceof DeveloperAudioRequest) ? getActualTextOfRequest(requestResult) : "";

            /*
            AudioRequestId: "audioRequestId"
            ExpectedText: "actualText"


             */
            printAudioRequestAndExpectedText(writer, requestResult, actualText);


            for (Map.Entry<String, String> entry : results.entrySet()) {
                String postProcessName = entry.getKey();
                String calculatedText = entry.getValue();

                /*
                PostProcess: "postprocessName"
                ProcessedText: "processedText"

                 */
                printPostProcessAndProcessedText(writer, postProcessName, calculatedText);
                float equalPercentage = calculateEquality(actualText, calculatedText);
                writer.write("Calculated Similarity: " + equalPercentage + "%");

                writer.write(System.lineSeparator());
                writer.write(System.lineSeparator());
            }
            writer.write(System.lineSeparator());
        }
        writer.close();
    }

    private void printAudioRequestAndExpectedText(FileWriter writer, FinalAudioRequestResult request, String actualText) throws IOException {
        writer.write("AudioRequestId: " + request.getRequest().getRequestId());
        writer.write(System.lineSeparator());
        writer.write("Expected Text: " + actualText);
        writer.write(System.lineSeparator());
        writer.write(System.lineSeparator());
    }

    private void printPostProcessAndProcessedText(FileWriter writer, String postProcess, String processedText) throws IOException {
        writer.write("PostProcess: " + postProcess);
        writer.write(System.lineSeparator());
        writer.write("Processed Text: " + processedText);
        writer.write(System.lineSeparator()); }


    private String getActualTextOfRequest(FinalAudioRequestResult request) {
        DeveloperAudioRequest devAudioRequest = (DeveloperAudioRequest) request.getRequest();

        return devAudioRequest.getActualText();
    }

}
