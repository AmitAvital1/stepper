package app.resources.body;

import dto.FlowDefinitionDTO;
import project.java.stepper.flow.definition.api.FlowDefinition;

import java.util.List;

public interface AdminBodyControllerDefinition {
    void setFlowsDetails(List<FlowDefinition> flow,List<FlowDefinitionDTO> flowDTO);
    void show();
    void setBodyController(AdminBodyController bodyCTRL);
}
