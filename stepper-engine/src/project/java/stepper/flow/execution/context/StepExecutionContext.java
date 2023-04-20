package project.java.stepper.flow.execution.context;

import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.context.logs.StepLogs;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType);
    boolean storeDataValue(String dataName, Object value);
    void updateCurrentWorkingStep(StepUsageDeclaration newStep);
    StepUsageDeclaration getCurrentWorkingStep();
    void addStepLog(StepLogs stepLogsToAdd);
    void addStepSummaryLine(String line);

    // some more utility methods:
    // allow step to store log lines
    // allow steps to declare their summary line
}
