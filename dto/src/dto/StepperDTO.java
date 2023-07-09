package dto;

import java.util.List;

public class StepperDTO {

    private final List<FlowDefinitionDTO> flows;

    public StepperDTO(List<FlowDefinitionDTO> flows) {
        this.flows = flows;
    }

    public List<FlowDefinitionDTO> getFlows() {
        return flows;
    }
}
