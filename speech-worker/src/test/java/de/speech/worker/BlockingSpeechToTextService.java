package de.speech.worker;

import de.fraunhofer.iosb.spinpro.annotations.Service;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextService;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextServiceMetadata;
import de.fraunhofer.iosb.spinpro.speechtotext.NullObjectSpeechToTextServiceMetadata;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

@Service(crypticId = "blockingspeechtottextservice", name = "BlockingService", author = "Steffen Steudle", organisation = "PSE", version = "1.0")
public class BlockingSpeechToTextService implements ISpeechToTextService {

    @Override
    public String speechToText(AudioInputStream audioInputStream) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("sleep interrupted");
        }
        return "sentence";
    }

    @Override
    public ISpeechToTextServiceMetadata speechToTextWithMetadata(AudioInputStream ais) {
        return new NullObjectSpeechToTextServiceMetadata(speechToText(ais));
    }

    @Override
    public AudioFormat[] getSupportedAudioFormats() {
        return new AudioFormat[0];
    }

    @Override
    public String getModelIdentifier() {
        return "blocking";
    }
}
