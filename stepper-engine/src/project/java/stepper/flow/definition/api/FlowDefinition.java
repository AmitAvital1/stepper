package project.java.stepper.flow.definition.api;

import project.java.stepper.exceptions.CustomeMappingInvalid;
import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.step.api.DataDefinitionDeclaration;

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
}
