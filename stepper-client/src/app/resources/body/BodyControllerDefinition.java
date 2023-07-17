package app.resources.body;

import dto.FlowDefinitionDTO;
import project.java.stepper.flow.definition.api.FlowDefinition;

import java.util.List;

public interface BodyControllerDefinition {
    void setFlowsDetails(List<FlowDefinition> flow, List<FlowDefinitionDTO> flowDTO);
    void show();
    void setBodyController(BodyController bodyCTRL);
}
