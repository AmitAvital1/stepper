package project.java.stepper.flow.manager;

import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.runner.FlowsExecutionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {

    private final List<FlowDefinition> flows = new ArrayList<>();
    private final Map<String,FlowDefinition> nameFlowToFlowDefinition = new HashMap<>();

    private final List<FlowExecution> flowExecutions = new ArrayList<>();
    private final Map<String,FlowExecution> uuidToFlowExecution = new HashMap<>();

    private final FlowsExecutionManager flowsExecutionManager = new FlowsExecutionManager();

    public FlowsExecutionManager getFlowsExecutionManager() {
        return flowsExecutionManager;
    }

    public synchronized List<FlowDefinition> getFlows() {
        return flows;
    }

    public synchronized List<FlowExecution> getFlowExecutions() {
        return flowExecutions;
    }

    public synchronized boolean checkIfFlowExist(String nameOfFlow){
        return nameFlowToFlowDefinition.containsKey(nameOfFlow);
    }
    public synchronized FlowDefinition getFlowDefinitionByName(String nameOfFlow){
        return nameFlowToFlowDefinition.get(nameOfFlow);
    }
    public synchronized void addFlowExecution(FlowExecution flowExecution){
        flowExecutions.add(flowExecution);
        uuidToFlowExecution.put(flowExecution.getUniqueId(),flowExecution);
    }
    public synchronized void addFlow(FlowDefinition flow){
        if (!checkIfFlowExist(flow.getName())){
            nameFlowToFlowDefinition.put(flow.getName(),flow);
            flows.add(flow);
        }
    }
    public FlowExecution getFlowExecutionByUUID(String uuid){
        return uuidToFlowExecution.get(uuid);
    }

}
