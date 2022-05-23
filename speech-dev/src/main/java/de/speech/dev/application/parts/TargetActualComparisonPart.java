package de.speech.dev.application.parts;

import de.speech.core.application.execution.ExecutionPart;
import de.speech.core.application.execution.RuntimeUtil;
import de.speech.core.application.execution.parts.ParallelizedExecutionPart;
import de.speech.core.task.result.ITaskResult;
import de.speech.core.task.result.implementation.FinalAudioRequestResult;
import de.speech.core.task.result.implementation.FinalTaskResultWithTac;
import de.speech.dev.targetActualComparison.ITargetActualComparison;

public class TargetActualComparisonPart extends ParallelizedExecutionPart<ITaskResult<FinalAudioRequestResult>, FinalTaskResultWithTac> {

    private final static String TARGETACTUALCOMPARISON_PART_IDENTIFIER = "targetActualComparisonPart";

    private ITargetActualComparison targetActualComparison;

    public TargetActualComparisonPart(ITargetActualComparison targetActualComparison) {
        this(targetActualComparison, TARGETACTUALCOMPARISON_PART_IDENTIFIER, 4, RuntimeUtil.getAvailableProcessors(4), null);
    }
    public TargetActualComparisonPart(ITargetActualComparison targetActualComparison, String identifier, int queueSize, int numberOfExecutors, ExecutionPart<FinalTaskResultWithTac, ?> next) {
        super(identifier, queueSize, numberOfExecutors, next);

        this.targetActualComparison = targetActualComparison;
    }

    @Override
    public FinalTaskResultWithTac executeElement(ITaskResult<FinalAudioRequestResult> element) {
        FinalTaskResultWithTac tacResult = targetActualComparison.compare(element);

        return tacResult;
    }
}
