package de.speech.dev.builder.taskbuilder;

import de.speech.core.framework.IFramework;
import de.speech.core.postprocessing.IPostProcess;
import de.speech.core.postprocessing.IPostProcessFactory;
import de.speech.core.task.IFrameworkConfiguration;
import de.speech.core.task.implementation.Task;
import de.speech.core.task.result.ISpeechToTextServiceData;
import de.speech.dev.builder.taskbuilder.implementation.TaskBuilderWithRequests;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestAbstractTaskBuilder {

    private static class Postprocess implements IPostProcess {

        @Override
        public String process(List<ISpeechToTextServiceData> inputData) {
            return null;
        }
    }

    private static class FrameworkConfiguration implements IFrameworkConfiguration {

        @Override
        public List<String> getPreprocesses() {
            return null;
        }

        @Override
        public IFramework getFramework() {
            return null;
        }
    }

    private TaskBuilderWithRequests taskBuilder;



    @BeforeEach
    void cleanTaskBuilder() {
        taskBuilder = new TaskBuilderWithRequests();
    }

    @AfterAll
    void cleanUp() {
        taskBuilder = null;
    }



    @Test
    void testAddPostProcess() {
        MockPostProcessFactory postProcessFactory = new MockPostProcessFactory();
        taskBuilder.addPostProcessFactory(postProcessFactory);
        Task task = taskBuilder.buildTask();

        assert(!task.getPostProcessFactories().isEmpty());
    }

    @Test
    void testTryAddFrameworkConfigurationWithoutPreProcess() {
        Task task = taskBuilder.addFrameworkConfiguration("test","test",null).buildTask();
        assert(task.getFrameworkConfigurations().size() == 1);
        assert(task.getFrameworkConfigurations().get(0).getPreprocesses().size() == 0);
    }

    @Test
    void testAddFrameworkConfigurationWithPreprocess() {
        Task task = taskBuilder.addFrameworkConfiguration("test","test", builder -> builder.addPreProcesses("","")).buildTask();
        assert(!task.getFrameworkConfigurations().isEmpty());
    }

    @Test
    void testAddFrameworkConfigurationWithoutPreProcess() {
        Task task = taskBuilder.addFrameworkConfiguration("test","test").buildTask();
        assert(!task.getFrameworkConfigurations().isEmpty());
    }

    @Test
    void testNormalBuildingOfTask() {
        Task task = taskBuilder.addFrameworkConfiguration("test","test").addPostProcessFactory(new MockPostProcessFactory()).buildTask();
        assert (!task.getFrameworkConfigurations().isEmpty());
        assert (!task.getPostProcessFactories().isEmpty());
    }

    @Test
    void testTaskIdCalculation() {
        Task task0 = taskBuilder.buildTask();
        TaskBuilderWithRequests taskBuilder1 = new TaskBuilderWithRequests();
        Task task1 = taskBuilder1.buildTask();
        TaskBuilderWithRequests taskBuilder2 = new TaskBuilderWithRequests();
        Task task2 = taskBuilder2.buildTask();

        assert(task0.getTaskID() == task1.getTaskID() -1);
        assert(task0.getTaskID() == task2.getTaskID() -2);
        assert(task1.getTaskID() == task2.getTaskID() -1);
    }

    private static class MockPostProcessFactory implements IPostProcessFactory {

        @Override
        public IPostProcess createPostProcess() {
            return null;
        }
    }
}
