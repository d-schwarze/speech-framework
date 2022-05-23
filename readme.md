# Introduction
Das Projekt bietet ein Framework für verteilte und transitive Spracherkennung an.
Es lassen sich verschiedene Speech-to-Text-Frameworks einbinden.
Audiodateien werden auf die Speech-to-Text-Frameworks verteilt und von diesen erkannt.
Daraufhin werden im Postprocessing die Ergebnisse der Frameworks mit Algorithmen kombiniert 
und können mit Solltexten verglichen werden.

Das Projekt lässt sich mit eigenen Postprocessing-Algorithmen, Audiofiltern, Ist-Soll-Vergleichen und Speech-to-Text-Frameworks erweitern.
Die Ausführung kann dabei individuell konfiguriert werden.
# Setup

## Core

1. JAR in das eigene Projekt einbinden
2. Benötigte Klassen überschreiben
3. Framework starten (siehe [Vorgehen](#vorgehen))


## Worker

1. JAR in Ordner kopieren
2. JAR einmal ausführen, um eine Default Config konfigurieren zu lassen
3. Config anpassen
4. JAR ausführen

# Usage

### Vorgehen
1. Konfiguration entweder per Annotation definieren oder die Konfigurations-Datei nutzen.
    Datei wird automatisch nach dem ersten Start erstellt.
2. Instanz von `SpeechApplication` oder `DeveloperSpeechApplication` erstellen
3. `SpeechApplication.start()` bei der Instanz aufrufen

### SpeechApplication
Benutze die abstrakte Klasse `SpeechApplication` um das Framework zu erweitern.
Mittels der erweiterten Klasse kann man das Framework dann starten.
```
class CustomSpeechApplication extends SpeechApplicaiton {
    /*
      ...
      Override methods if neceassary
      ...
    */
}

...

class Main {

    public static void main(String[] args) {
        SpeechApplication application = new CustomSpeechApplication();
        application.start();
    }
}

```

#### Task ausführen
Mögliche Methoden
1. `runTask(ITask task)`
```
    SpeechApplication application = ...;
    application.start();
    ITask task = ...;
        
    application.runTask(task);

```

2. `runTaskWithFuture(ITask task)`

Lasse Task ausführen und warte direkt auf das Ergebnis mittels des Futures.
```
    SpeechApplication application = ...;
    application.start();
    ITask task = ...;
        
    Future<ITaskResult<FinalAudioRequestResult>> future = application.runTask(task);

```

#### Exceptions bekommen

Nur möglich bei `runTaskWithFuture(ITask task)`. Exceptions die während der Ausführung auftreten werden
in `ExecutionErrorException` gekapselt. Cause des ExecutionException.

```
    Future<ITaskResult<FinalAudioRequestResult>> future = application.runTaskWithFuture(task);
    
    try {
        ITaskResult<FinalAudioRequestResult> r = future.get();
    } catch(ExecutionException ex) {
        ExecutionErrorException eee = (ExecutionErrorException) ex.getCause();

        ...

    } ...
```

#### Erweiterbare Methoden

Eigenes `SpeechAnnotationSystem` benutzen.
```
@Override
protected SpeechAnnotationSystem createAnnotationSystem();
```

Eigene `SpeechConfiguration` benutzen.
```
@Override
protected SpeechConfiguration loadConfiguration();
```

Eigenen `AbstractDispatcher` benutzen.
```
@Override
protected AbstractDispatcher createDispatcher();
```

Weitere `ExecutionParts` dem System hinzufügen. Die Parts werden
dem `ExectuionSystem` hinzugefügt, sowie sie in die Liste gelegt wurden.
```
@Override
protected List<ExecutionPart> createExecutionParts() {
    List<ExecutionPart> parts = super.createExecutionParts();
    
    /*
      ...
      Add more parts
      ...
    */
    
    return parts;
}
```

#### SpeechConfiguration

Aufbau in der Json-Datei `configuration.json`
- workers: Liste an Konfigurierten Workern
    - Location: Je nach Implementation von `IDispatcher` zu wählen
- resultTimeout: Die Zeit bis ein Request abgebrochen wird, nachdem dieser abgeschickt wurde(Millisekunden).
- httpTimeout: Der Timeout für einen Http-request (Millisekunden).
- port: Der Port an dem der Httpserver für die Ergebnisse gestartet wird.
- acceptors: Anzahl an Akzeptoren. -1 für einen Default Wert. Wenn der Wert auf 0 gesetzt ist, werden die Selektoren für das Akzeptieren von Verbindungen genommen.
- selectors: Anzahl an Selektoren. <= 0 für einen Default Wert.
- queueSize: Größe der Akzeptierwarteschlange.
  (https://www.eclipse.org/jetty/documentation.ph)

Beispiel der Datei
```
{
  "workers": [{
    "location": "http://127.0.0.1:3000"
  }],
  "resultTimeout": 100000,
  "httpTimeout": 30000,
  "port": 3001,
  "acceptors": 1,
  "selectors": 1,
  "queueSize": 128
}
```

Äquivalentes Interface: `SpeechConfiguration`

Benutzung der Annotation `ApplicationConfiguration`
Falls eine Konfiguration mittels der Annotation benutzt wurde, wird sie jeder andern
Konfigurations-Art vorgezogen.
```
@ApplicationConfiguration
public class CustomSpeechConfiguration implements SpeechConfiguration {
    
    @Overrride
    List<WorkerConfiguration> getWorkers() {
        ...
    }
    
    @Overrride
    int getResultTimeout() {
       ...
    }
    
    @Overrride
    int getPort() {
        ...
    }

    @Overrride
    int getAcceptors() {
        ...
    }

    @Overrride
    int getSelectors() {
        ...
    }

    @Overrride
    int getQueueSize() {
        ...
    }

}
```


### DeveloperSpeechApplication
Erweitert die Klasse `SpeechApplication`. Fügt Funktionalitäten hinzu, die für Entwickler interessant sind.
- Ist-Soll-Vergleich: `TargetActualComparison`
    - `public Future<FinalTaskResultWithTac> runTaskWithFutureAndTac(ITask task)`
- Ausführen von `Tasks` beim Start.

```
public class CustomDeveloperSpeechApplication extends DeveloperSpeechApplication {
    
    @Override
    protected List<ITask> runTasksOnStart() {
        ...
        Create new tasks and return them as list
        ...
    }
}
```

#### Erweiterbare Methoden
Eigene `TargetActualComparison` benutzen.
```
@Override
protected TargetActualComparison createTargetActualComparison();
```

### TaskBuilder
Es gibt verschiedene Builder Klassen um eine `Task` zu erstellen.

#### `TaskBuilderWithPath`
Erstellt eine `Task` indem alle Wav-Dateien aus einem Ordner für die `AudioRequest`
genutzt werden.
```
ITask task = new TaskBuilderWithPath("realitve/path/to/folder/with/wav/files")
                .addFrameworkConfiguration("FrameworkA", "ModelB")
                .addFrameworkConfiguration("FrameworkA", "ModelA", builder -> builder.addPreProcesses("preProcessA"))
                .addFrameworkConfiguration("FrameworkC", "ModelA", builder -> builder.addPreProcesses("preProcessB","preProcessC"))
                .addPostProcessFactory(new IPostProcessFactory({
                    @Override
                    public IPostProcess createPostProcess() {
                        return new IPostProcess() {
                            @Override
                            public String process(List<ISpeechToTextServiceData> inputData) {
                                ...                
                            }
                        };        
                    }
                }))
                .buildTask();
```

####  `TaskBuilderWithRequests`
Erstellt eine `Task` indem die AudioInputStreams der Sprachdateien direkt
übergeben werden.
```
ITask task = new TaskBuilderWithRequests()
                .addAudioInputStream(...)
                .addAudioInputStream(...)
                .addAudioInputStream(...)
                .addAudioInputStream(...)
                .addFrameworkConfiguration("FrameworkA", "ModelB")
                .addFrameworkConfiguration("FrameworkA", "ModelA", builder -> builder.addPreProcesses("preProcessA"))
                .addFrameworkConfiguration("FrameworkC", "ModelA", builder -> builder.addPreProcesses("preProcessB","preProcessC"))
                .addPostProcessFactory(new IPostProcessFactory({
                    @Override
                    public IPostProcess createPostProcess() {
                        return new IPostProcess() {
                            @Override
                            public String process(List<ISpeechToTextServiceData> inputData) {
                                ...                
                            }
                        };        
                    }
                }))
                .buildTask();
```
####  `DeveloperTaskBuilderWithPath`
Erstellt eine `Task` indem alle Wav-Dateien aus einem Ordner für die `AudioRequest`
genutzt werden. Zusätzlich werden alle Strings aus einer Datei gelesen und als Vergleichstexte genutzt.
Die Texte müssen dabei in einer .txt Datei sein. Jede Zeile ist für einen Request und den dazugehörigen Vergleichstext.
Als Trennung zwischen dem Namen und dazugehörigen Text wird "//" genutzt. Der Name des Audiorequest darf keine extension wie .wav benutzen.
Es darf, muss aber kein Leerzeichen zwischen dem Namen, dem Trennzeichen und dem Vergleichstext geben.
Eine Zeile hat also die Form:
audioRequestName // Vergleichstext
oder:
audioRequestName//Vergleichstext
```
ITask task = new DeveloperTaskBuilderWithPath("realitve/path/to/folder/with/wav/files", "relative/path/to/actualtexts.txt)
                .addFrameworkConfiguration("FrameworkA", "ModelB")
                .addFrameworkConfiguration("FrameworkA", "ModelA", builder -> builder.addPreProcesses("preProcessA"))
                .addFrameworkConfiguration("FrameworkC", "ModelA", builder -> builder.addPreProcesses("preProcessB","preProcessC"))
                .addPostProcessFactory(new IPostProcessFactory({
                    @Override
                    public IPostProcess createPostProcess() {
                        return new IPostProcess() {
                            @Override
                            public String process(List<ISpeechToTextServiceData> inputData) {
                                ...                
                            }
                        };        
                    }
                }))
                .buildTask();
```
####  `DeveloperTaskBuilderWithRequests`
```
ITask task = new DeveloperTaskBuilderWithRequests()
                .addAudioInputStreamWithActualText(audioInputStreamA, "actualTextA")
                .addAudioInputStreamWithActualText(audioInputStreamB, "actualTextA")
                .addAudioInputStream(audioInputStreamC)
                .addAudioInputStream(audioInputStreamD)
                .addFrameworkConfiguration("FrameworkA", "ModelB")
                .addFrameworkConfiguration("FrameworkA", "ModelA", builder -> builder.addPreProcesses("preProcessA"))
                .addFrameworkConfiguration("FrameworkC", "ModelA", builder -> builder.addPreProcesses("preProcessB","preProcessC"))
                .addPostProcessFactory(new IPostProcessFactory({
                    @Override
                    public IPostProcess createPostProcess() {
                        return new IPostProcess() {
                            @Override
                            public String process(List<ISpeechToTextServiceData> inputData) {
                                ...                
                            }
                        };        
                    }
                }))
                .buildTask();
```


### TargetActualComparison

Um einen eigenen Ist-Soll-Vergleich zu verwenden, erweitere `TargetActualComparison` und überschreibe die e Methode _calculateEquality_.
```
class DeveloperTargetActualComparison extends TargetActualComparison {
    @Override
    public float calculateEquality(String actualText, String calculatedText) {
        ...
    }
}
```
Benutzt eine Instanz von `FinalTaskResult` um zu berechnen, inwieweit sich die berechneten String von den
eigentlichen unterscheiden.
Es existiert die Methoden FinalTaskresultWithTac compare(ITaskresult<FinalAudioRequestResult> taskResult),
welche ein neues `FinalTaskResultWithTac` Objekt zurückgibt.
Dieses enthält für jeden Request zusätzlich zu den berechneten Strings auch die prozentuale Übereinstimmung
mit dem eigentlichen Text.

Es existiert die Methode File compare(ITaskresult<FinalAudioRequestResult> taskResult, String filename),
welche eine neue File erzeugt und die Übereinstimmung textuell in diese File schreibt.

### PostProcessing

#### Neuen PostProcess Hinzufügen
Implementiere das Interface `IPostProcess`.
```
class PostProcess implements IPostProcess {
    @Override
    String process(List<ISpeechToTextServiceData> inputData) {
        ...
    }
}
```

Implementiere das Interface `IPostProcessFactory` und nutze die Methode _createPostProcess_ 
um eine neue Instanz des implementierten `IPostProcess` zurückzugeben.
```
class PostProcessFactory implements IPostProcessFactory {
    @Override
    IPostProcess createPostProcess() {
        return new PostProcess();
    }
}
```

#### @PostProcessFactory

Verwende die `@PostProcessFactory` Annotation, um Implementierungen der `IPostProcessFactory` zu annotieren. Die
annotierten Klassen werden standardmäßig im Postprocessing verwendet, wenn die Task keine `IPostProcessFactory` enthält.

### Worker

#### WorkerConfiguration

Aufbau in der Json-Datei `config.json`

- port: Der Port auf dem der Worker die HTTP-Requests des Dispatchers entgegennimmt. Muss mit dem Port für diesen Worker
  in der [SpeechConfiguration](#speechconfiguration) übereinstimmen.
- maxQueueSize: Die maximale Größe der internen `IWorkerAudioRequest` Warteschlange.
- preProcessDir: Ein Pfad zu einem Ordner, in welchem sich die Jar-Dateien, welche `IPreProcess` implementieren
  befinden.
- serviceJar: Ein Pfad zu einer Jar-Datei, die eine `ISpeechToTextService` Implementierung enthält.

Beispiel der Datei

```
{
  "port": 3000,
  "maxQueueSize": 30,
  "preProcessDir": "preprocesses",
  "serviceJar": "service.jar"
}
```

#### Neuen PreProcess hinzufügen

Implementiere das Interface `IPreProcess`:

```
class PreProcess implements IPreProcess {
    @Override
    public String getName() {
        return "EXAMPLE";
    }
    
    @Override
    public AudioInputStream process(AudioInputStream input) {
        ...
    }
}
```

Füge den PreProcess in den in der [WorkerConfiguration](#workerconfiguration) angegebenen PreProcess Ordner ein.

# Test Coverage
Class Coverage: 90%\
Method Coverage: 86%\
Line Coverage: 84%