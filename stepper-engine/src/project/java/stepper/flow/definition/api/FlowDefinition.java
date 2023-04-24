package project.java.stepper.flow.definition.api;

import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.List;
import java.util.Map;

public interface FlowDefinition {
    String getName();
    String getDescription();
    List<StepUsageDeclaration> getFlowSteps();
    List<String> getFlowFormalOutputs();

    void validateFlowStructure();
    Map<StepUsageDeclaration,List<DataDefinitionDeclaration>> getFlowFreeInputs();
    List<DataDefinitionDeclaration> getfreeInputsDataDefinitionDeclaration();
}
