package dto.execution;

public class FlowExecutionUUIDDTO {

    private final String uuid;
    private String flowNameContinuation;

    public FlowExecutionUUIDDTO(String uuid) {
        this.uuid = uuid;
    }

    public FlowExecutionUUIDDTO(String uuid, String flowNameContinuation) {
        this.uuid = uuid;
        this.flowNameContinuation = flowNameContinuation;
    }

    public String getUuid() {
        return uuid;
    }
    public String getFlowNameContinuation() {
        return flowNameContinuation;
    }
}
