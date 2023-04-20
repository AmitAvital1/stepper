package project.java.stepper.flow.definition.api;

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
    boolean addFreeInputForStart(DataDefinitionDeclaration dataDefinitionDeclaration,String data);
    Map<String,Object> getStartersFreeInputForContext();
}
