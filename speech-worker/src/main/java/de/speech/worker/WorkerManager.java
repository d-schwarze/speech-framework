package de.speech.worker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextService;
import de.speech.worker.loader.LoadingException;
import de.speech.worker.loader.SpeechToTextServiceLoader;
import de.speech.worker.local.IResultHandler;
import de.speech.worker.local.IWorkerServer;
import de.speech.worker.local.ResultHandler;
import de.speech.worker.local.WorkerServer;
import de.speech.worker.remote.RemoteManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * The main entry point for the worker
 */
public class WorkerManager {

    private final static Path CONFIG_PATH = Paths.get("config.json");

    private IWorkerServer workerServer;
    private RemoteManager remoteManager;

    private static Config config;

    /**
     * Creates a new {@linkplain WorkerManager} with the given {@linkplain Config}
     */
    public WorkerManager(Config c) {
        if (c == null) {
            config = loadConfig();
        } else {
            config = c;
        }

        SpeechToTextServiceLoader loader;
        try {
            loader = new SpeechToTextServiceLoader(Paths.get(config.getServiceJar()));
        } catch (LoadingException e) {
            WorkerLogger.error("Error while loading the service", e);
            return;
        }

        ISpeechToTextService service = loader.getResult().getService();
        try {
            workerServer = new WorkerServer(config.getMaxQueueSize(), service, loader.getResult().getName(), Paths.get(config.getPreProcessDir()));
        } catch (LoadingException e) {
            WorkerLogger.error("Error while loading preprocesses", e);
            return;
        }
        remoteManager = new RemoteManager(workerServer, config.getPort());
        IResultHandler resultHandler = new ResultHandler(remoteManager.getConnection());
        workerServer.setResultHandler(resultHandler);
    }

    /**
     * Creates a new {@linkplain WorkerManager} and loads the config or creates a new one if none is available
     */
    public WorkerManager() {
        this(null);
    }

    /**
     * Shuts down the worker
     */
    public void shutdown() {
        try {
            workerServer.shutdown();
        } catch (InterruptedException e) {
            WorkerLogger.error("Error shutting down the worker", e);
        }
        try {
            remoteManager.shutdown();
        } catch (Exception e) {
            WorkerLogger.error("Error shutting down the network connection", e);
        }
    }

    private Config loadConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            try {
                Files.createFile(CONFIG_PATH);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Files.write(CONFIG_PATH, Collections.singleton(gson.toJson(new Config())));
                WorkerLogger.info("No Config was found, so a default one was created");
                System.exit(0);
            } catch (IOException e) {
                WorkerLogger.error("Error while creating the default config", e);
            }
            return new Config();
        }
        List<String> lines = null;
        try {
            lines = Files.readAllLines(CONFIG_PATH);
        } catch (IOException e) {
            WorkerLogger.error("Error while loading the config, using the default config");
        }
        if (lines == null) {
            return new Config();
        }

        String json = String.join("", lines);
        return new Gson().fromJson(json, Config.class);
    }

    public static Config getConfig() {
        return config;
    }
}
