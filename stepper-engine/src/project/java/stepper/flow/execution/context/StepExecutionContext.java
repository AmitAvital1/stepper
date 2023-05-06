package project.java.stepper.flow.execution.context;

import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.StepResult;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType) throws NoStepInput;
    boolean storeDataValue(String dataName, Object value);
    void updateCurrentWorkingStep(StepUsageDeclaration newStep);
    StepUsageDeclaration getCurrentWorkingStep();
    void addStepLog(StepLogs stepLogsToAdd);
    void addStepSummaryLine(String line);
    StepLogs getLastStepLogs();
    String getLastStepSummaryLine();
    Map<String, Object> getDataValuesMap();
    List<FlowExecution.flowOutputsData> getFlowOutputsData();
    void addStepData(StepUsageDeclaration step, String stepSummaryLine, StepLogs logs, Duration time, StepResult result);
    StepExecutionContextImpl.stepData getStepData(StepUsageDeclaration step);

    // some more utility methods:
    // allow step to store log lines
    // allow steps to declare their summary line
}
