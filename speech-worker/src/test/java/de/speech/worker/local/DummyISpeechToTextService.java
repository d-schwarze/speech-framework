package de.speech.worker.local;

import de.fraunhofer.iosb.spinpro.annotations.Service;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextService;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextServiceMetadata;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(crypticId = "dummy", name = "dummy-speech", author = "Steffen", version = "1.0", organisation = "PSE")
public class DummyISpeechToTextService implements ISpeechToTextService {
    @Deprecated
    @Override
    public String speechToText(AudioInputStream audioInputStream) {
        return "dummy text";
    }

    @Override
    public ISpeechToTextServiceMetadata speechToTextWithMetadata(AudioInputStream ais) {
        return new ISpeechToTextServiceMetadata() {
            @Override
            public String getRecognizedSentence() {
                return "dummy text";
            }

            @Override
            public List<Map<String, Double>> getProbabilitiesPerRecognizedWord() {
                HashMap<String, Double> firstWord = new HashMap<>();
                firstWord.put("dummy", 99.9);
                firstWord.put("dumy", 10.3);
                HashMap<String, Double> secondWord = new HashMap<>();
                secondWord.put("text", 99.3);
                secondWord.put("tex", 5.3);
                return Arrays.asList(firstWord, secondWord);
            }

            @Override
            public String getFramework() {
                return "dummy framework";
            }

            @Override
            public String getModel() {
                return "dummy model";
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
        return "dummy model";
    }
}
