package project.java.stepper.flow.definition.api;

import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.List;

public interface FlowDefinition {
    String getName();
    String getDescription();
    List<StepUsageDeclaration> getFlowSteps();
    List<String> getFlowFormalOutputs();

    void validateFlowStructure();
    List<DataDefinitionDeclaration> getFlowFreeInputs();
}
