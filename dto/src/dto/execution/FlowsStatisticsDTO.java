package dto.execution;

import java.util.List;

public class FlowsStatisticsDTO {
    private final List<FlowStatisticsDTO> flowStatisticsDTOS;

    public FlowsStatisticsDTO(List<FlowStatisticsDTO> flowStatisticsDTOS) {
        this.flowStatisticsDTOS = flowStatisticsDTOS;
    }

    public List<FlowStatisticsDTO> getFlowStatisticsDTOS() {
        return flowStatisticsDTOS;
    }
}
