package de.speech.worker.loader;

import de.fraunhofer.iosb.spinpro.annotations.Service;
import de.fraunhofer.iosb.spinpro.speechtotext.ISpeechToTextService;

public class SpeechToTextServiceWrapper {

    private ISpeechToTextService service;
    private String name;
    private String author;
    private String organisation;
    private String version;
    private String crypticId;

    public SpeechToTextServiceWrapper(ISpeechToTextService service, String name, String author, String organisation, String version, String crypticId) {
        this.service = service;
        this.name = name;
        this.author = author;
        this.organisation = organisation;
        this.version = version;
    }

    public SpeechToTextServiceWrapper(ISpeechToTextService service, Service annotation) {
        this(service, annotation.name(), annotation.author(), annotation.organisation(), annotation.version(), annotation.crypticId());
    }

    public ISpeechToTextService getService() {
        return service;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getOrganisation() {
        return organisation;
    }

    public String getVersion() {
        return version;
    }
}
