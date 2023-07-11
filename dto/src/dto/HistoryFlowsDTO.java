package dto;

import dto.execution.FlowExecutionDTO;

import java.util.List;

public class HistoryFlowsDTO {
    private final List<FlowExecutionDTO> flows;

    public HistoryFlowsDTO(List<FlowExecutionDTO> flows) {
        this.flows = flows;
    }

    public List<FlowExecutionDTO> getFlows() {
        return flows;
    }
}
