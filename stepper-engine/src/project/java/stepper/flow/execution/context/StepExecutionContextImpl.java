package project.java.stepper.flow.execution.context;

import project.java.stepper.dd.api.DataDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.*;

public class StepExecutionContextImpl implements StepExecutionContext {

    private final Map<String, Object> dataValues;
    private StepUsageDeclaration currentWorkingStep;
    private List<StepLogs> flowLogs;
    private List<String> stepSummaryLine;
    private final List<FlowExecution.flowOutputsData> outputsData;

    public StepExecutionContextImpl() {
        dataValues = new HashMap<>();
        flowLogs = new ArrayList<>();
        stepSummaryLine = new ArrayList<>();
        outputsData = new ArrayList<>();
    }

    @Override
    public Map<String, Object> getDataValuesMap(){return dataValues;}

    @Override
    public List<FlowExecution.flowOutputsData> getFlowOutputsData() {
        return outputsData;
    }

    @Override
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) {
       //Find of there is an input match
        String finalDataName = currentWorkingStep.getinputToFinalName().get(dataName);

        if(currentWorkingStep.thisInputHaveCustomeMapping(finalDataName) != null)
            finalDataName = currentWorkingStep.thisInputHaveCustomeMapping(finalDataName);

        String finalName = finalDataName;
        DataDefinitionDeclaration theExpectedDataDefinition = null;
        Optional<DataDefinitionDeclaration> maybeTheExpectedDataDefinition =
                currentWorkingStep.getStepDefinition().inputs().stream()
                .filter((input) -> input.getName() == dataName)////////Continue here
                .findFirst();

        if(maybeTheExpectedDataDefinition.isPresent()){
            theExpectedDataDefinition = maybeTheExpectedDataDefinition.get();
            if (expectedDataType.isAssignableFrom(theExpectedDataDefinition.dataDefinition().getType())) {
                Object aValue = dataValues.get(finalDataName);
                return expectedDataType.cast(aValue);
            }
        }
        else{
               //Handle there is no input;
        }
        return null;
    }

    @Override
    public boolean storeDataValue(String dataName, Object value) {
        if(currentWorkingStep != null) {
            String finalDataName = currentWorkingStep.getoutputToFinalName().get(dataName);
            dataValues.put(finalDataName, value);

            Optional<DataDefinitionDeclaration> getDataDefinition =
                    currentWorkingStep.getStepDefinition().outputs().stream()
                            .filter((output) -> output.getName() == dataName)////////Continue here
                            .findFirst();
            outputsData.add(new FlowExecution.flowOutputsData(finalDataName,currentWorkingStep,getDataDefinition.get(),value));
        }
        else
            dataValues.put(dataName, value);



        return true;
    }
    @Override
    public void updateCurrentWorkingStep(StepUsageDeclaration newStep){
        currentWorkingStep = newStep;
    }
    @Override
    public StepUsageDeclaration getCurrentWorkingStep(){
        return currentWorkingStep;
    }
    @Override
    public void addStepLog(StepLogs stepLogsToAdd) {
        flowLogs.add(stepLogsToAdd);
    }
    @Override
    public void addStepSummaryLine(String line){ stepSummaryLine.add("Summary of " + currentWorkingStep.getFinalStepName() + ": " + line); }

    @Override
    public StepLogs getLastStepLogs() {
        return flowLogs.get(flowLogs.size()-1);
    }
    @Override
    public String getLastStepSummaryLine() {
        return stepSummaryLine.get(stepSummaryLine.size()-1);
    }

}
