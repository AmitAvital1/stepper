package project.java.stepper.flow.execution.context;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;

import java.time.Duration;
import java.util.*;

public class StepExecutionContextImpl implements StepExecutionContext {

    private final Map<String, Object> dataValues;
    private StepUsageDeclaration currentWorkingStep;
    private List<StepLogs> flowLogs;
    private List<String> stepSummaryLine;
    private final List<FlowExecution.flowOutputsData> outputsData;
    private List<stepData> flowStepsData;
    private ListProperty<stepData> flowStepsDataProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    public StepExecutionContextImpl() {
        dataValues = new HashMap<>();
        flowLogs = new ArrayList<>();
        stepSummaryLine = new ArrayList<>();
        outputsData = new ArrayList<>();
        flowStepsData = new ArrayList<>();
    }

    public class stepData{
        public StepUsageDeclaration step;
        public String stepSummaryLine;
        public StepLogs logs;
        public Duration time;
        public StepResult result;

        public stepData(StepUsageDeclaration step,String stepSummaryLine,StepLogs logs, Duration time, StepResult result){
            this.step = step;
            this.result = result;
            this.stepSummaryLine = stepSummaryLine;
            this.logs = logs;
            this.time = time;
        }
    }
    @Override
    public Map<String, Object> getDataValuesMap(){return dataValues;}

    @Override
    public List<FlowExecution.flowOutputsData> getFlowOutputsData() {
        return outputsData;
    }

    @Override
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) throws NoStepInput {
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
                if(theExpectedDataDefinition.necessity() == DataNecessity.MANDATORY && aValue == null)//There is missing data
                    throw new NoStepInput("During failures in the flow, the input: " + theExpectedDataDefinition.userString() + " missed");
                else return expectedDataType.cast(aValue);
            }
        }
        else{
               throw new NoStepInput("During failures in the flow, the input: " + finalName + " missed");
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
    public void addStepData(StepUsageDeclaration step,String stepSummaryLine,StepLogs logs, Duration time, StepResult result){
        stepData data = new stepData(step,stepSummaryLine,logs,time,result);
        flowStepsData.add(data);
        flowStepsDataProperty.add(data);
    }
    public ListProperty<stepData> getFlowStepsDataProperty(){return flowStepsDataProperty;}
    public stepData getStepData(StepUsageDeclaration step){
        for(stepData data : flowStepsData){
            if(data.step == step)
                return data;
        }
        return null;
    }
    @Override
    public List<StepLogs> getFlowLogs() {
        return flowLogs;
    }
    @Override
    public List<String> getStepSummaryLine() {
        return stepSummaryLine;
    }
    @Override
    public List<stepData> getRegFlowStepsData() {
        return flowStepsData;
    }
}
