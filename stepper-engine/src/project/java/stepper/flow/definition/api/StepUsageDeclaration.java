package project.java.stepper.flow.definition.api;

import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.StepDefinition;
import project.java.stepper.step.api.StepResult;

import java.time.Duration;
import java.util.Map;

public interface StepUsageDeclaration {
    String getFinalStepName();
    StepDefinition getStepDefinition();
    boolean skipIfFail();
    StepResult getStepResult();
    StepLogs getStepLogs();
    void setStepLogs(StepLogs logs);
    void setStepResult(StepResult result);
    Map<String,String> getinputToFinalName();
    Map<String,String> getoutputToFinalName();
    Map<String,String> getFinalNameToInput();
    Map<String,String> getFinalNameToOutput();
    void setDuration(Duration time);
    long getDuration();
    void setSummaryLine(String line);
    String getSummaryLine();
    boolean addLevelAlias(String name,String finalName);
    void addCustomeMapInput(String inputName,String inputTarget);
    String thisInputHaveCustomeMapping(String data);
}
