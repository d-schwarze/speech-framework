package de.speech.worker;

import de.fraunhofer.iosb.spinpro.annotations.Service;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextService;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextServiceMetadata;
import de.fraunhofer.iosb.spinpro.speechtotext.NullObjectSpeechToTextServiceMetadata;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Service(crypticId = "predefined-speech-to-text-service", name = "predefined-service", author = "Steffen Steudle", organisation = "PSE", version = "1.0")
public class PreDefinedSpeechToTextService implements ISpeechToTextService {

    private final static String TEXT_PATH = "texts.txt";

    private Scanner scanner;

    public PreDefinedSpeechToTextService() {
        if (!Files.exists(Paths.get(TEXT_PATH))) {
            System.err.println("texts.txt not found, creating one");
            try {
                Files.createFile(Paths.get(TEXT_PATH));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File file = new File(TEXT_PATH);
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String speechToText(AudioInputStream audioInputStream) {
        return speechToTextWithMetadata(audioInputStream).getRecognizedSentence();
    }

    @Override
    public ISpeechToTextServiceMetadata speechToTextWithMetadata(AudioInputStream ais) {
        if (!scanner.hasNextLine()) {
            return new NullObjectSpeechToTextServiceMetadata("");
        }

        return new ISpeechToTextServiceMetadata() {
            @Override
            public String getRecognizedSentence() {
                return scanner.nextLine();
            }

            @Override
            public List<Map<String, Double>> getProbabilitiesPerRecognizedWord() {
                return null;
            }

            @Override
            public String getFramework() {
                return "predefined-service";
            }

            @Override
            public String getModel() {
                return "file";
            }

            @Override
            public String getFrameworkDependentJson() {
                return "{}";
            }
        };
    }

    @Override
    public AudioFormat[] getSupportedAudioFormats() {
        return new AudioFormat[0];
    }

    @Override
    public String getModelIdentifier() {
        return "dummy-predefined-speech-to-text-service";
    }
}
