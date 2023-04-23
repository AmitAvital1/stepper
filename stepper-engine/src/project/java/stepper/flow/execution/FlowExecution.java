package project.java.stepper.flow.execution;

import project.java.stepper.flow.definition.api.FlowDefinition;

import java.time.Duration;

public class FlowExecution {

    private final String uniqueId;
    private final FlowDefinition flowDefinition;
    private Duration totalTime;
    private FlowExecutionResult flowExecutionResult;

    // lots more data that needed to be stored while flow is being executed...

    public FlowExecution(String uniqueId, FlowDefinition flowDefinition) {
        this.uniqueId = uniqueId;
        this.flowDefinition = flowDefinition;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }
    public void setFlowExecutionResult(FlowExecutionResult result) {
        flowExecutionResult = result;
    }
    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }
    public void setDuration(Duration time){
        totalTime = time;
    }
    public long getDuration(){
        return totalTime.toMillis();
    }
}
