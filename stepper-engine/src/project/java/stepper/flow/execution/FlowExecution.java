package project.java.stepper.flow.execution;

import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import project.java.stepper.step.api.DataNecessity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowExecution {

    private final String uniqueId;
    private final FlowDefinition flowDefinition;
    private Duration totalTime;
    private FlowExecutionResult flowExecutionResult;


    private Map<String, Object> startersFreeInputForContext;

    // lots more data that needed to be stored while flow is being executed...

    public FlowExecution(String uniqueId, FlowDefinition flowDefinition) {
        this.uniqueId = uniqueId;
        this.flowDefinition = flowDefinition;
        startersFreeInputForContext = new HashMap<>();

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


    public boolean validateToExecute() throws MissMandatoryInput {
        boolean res = true;

        for (Map.Entry<StepUsageDeclaration,List<DataDefinitionDeclaration>> entry : flowDefinition.getFlowFreeInputs().entrySet()) {
            StepUsageDeclaration key = entry.getKey();
            List<DataDefinitionDeclaration> value = entry.getValue();
            for (DataDefinitionDeclaration dd : value) {
                if (!startersFreeInputForContext.containsKey(key.getinputToFinalName().get(dd.getName())) && dd.necessity() == DataNecessity.MANDATORY) {
                    throw new MissMandatoryInput("Missing Mandatory input: " + key.getinputToFinalName().get(dd.getName()));
                }
            }

        }
        return res;
    }

    public boolean addFreeInputForStart(StepUsageDeclaration step,DataDefinitionDeclaration dataDefinitionDeclaration, String data) {
        Object newData = dataDefinitionDeclaration.dataDefinition().convertUserInputToDataType(data,dataDefinitionDeclaration.dataDefinition().getType());
        startersFreeInputForContext.put(step.getinputToFinalName().get(dataDefinitionDeclaration.getName()),newData);
        return true;
    }
    public Map<String, Object> getStartersFreeInputForContext() {
        return startersFreeInputForContext;
    }

}
