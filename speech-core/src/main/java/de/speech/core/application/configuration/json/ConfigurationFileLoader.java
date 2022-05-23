package de.speech.core.application.configuration.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import de.speech.core.application.configuration.SpeechConfiguration;
import de.speech.core.logging.Loggable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

/**
 * Class for loading {@linkplain SpeechConfiguration} from files.
 */
public class ConfigurationFileLoader implements Loggable {

    /**
     * Location to access the default json speech configuration
     */
    private final static String DEFAULT_LOCATION = "configuration.json";

    /**
     * Loads the the default json speech configuration defined by a json file.
     * @return default configuration
     */
    public static SpeechConfiguration loadDefaultJsonConfiguration() throws IOException {
        return loadJsonConfiguration(Path.of(DEFAULT_LOCATION));
    }

    /**
     * Loads a speech configuration from a json file.
     * @param location location of the json file
     * @return loaded speech configuration
     * @throws FileNotFoundException if no file was found
     */
    public static SpeechConfiguration loadJsonConfiguration(Path location) throws IOException {
        Gson gson = new Gson();

        if (!Files.exists(location)) {
            createJsonConfiguration(location);
        }

        JsonReader reader = new JsonReader(new FileReader(new File(location.toString())));
        JsonSpeechConfiguration config = gson.fromJson(reader, JsonSpeechConfiguration.class);

        return config;
    }

    private static void createJsonConfiguration(Path location) throws IOException {
        Files.createFile(location);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonSpeechConfiguration config = new JsonSpeechConfiguration();

        Files.write(location, Collections.singleton(gson.toJson(config)));
    }

}
