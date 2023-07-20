package app.resources.body;

import dto.FlowDefinitionDTO;

import java.util.List;

public interface AdminBodyControllerDefinition {
    void setFlowsDetails(List<FlowDefinitionDTO> flowDTO);
    void show();
    void setBodyController(AdminBodyController bodyCTRL);
}
