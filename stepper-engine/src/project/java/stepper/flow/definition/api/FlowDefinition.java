package project.java.stepper.flow.definition.api;

import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.statistics.FlowStats;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface FlowDefinition {
    String getName();
    String getDescription();
    List<StepUsageDeclaration> getFlowSteps();
    boolean isReadOnly();
    void setReadOnly(boolean bool);
    void validateFlowStructure() throws StepperExeption;
    Map<StepUsageDeclaration,List<DataDefinitionDeclaration>> getFlowFreeInputs();
    void addFormalOutput(String name, DataDefinitionDeclaration data);
    Map<String,DataDefinitionDeclaration> getFormalOutput();
    void addFlowRunStepStats(StepUsageDeclaration step, Duration time);
    void addFlowRunStats(Duration time);
    FlowStats getFlowStatistics();
    void addContinuation(FlowDefinition name, Map<String,String> data) throws StepperExeption;
    List<FlowDefinitionImpl.continuationFlowDetails> getFlowsContinuations();
    Map<String,DataDefinitionDeclaration> getFreeInputFinalNameToDD();
    Map<String, Object> getInitialValues();
    void addInitialValue(String finalNameInput, Object dataInput);
}
