package project.java.stepper.flow.definition.api;

import project.java.stepper.exceptions.*;
import project.java.stepper.flow.statistics.FlowStats;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.time.Duration;
import java.util.*;

public class FlowDefinitionImpl implements FlowDefinition {

    private final String name;
    private final String description;
    private boolean readOnly;
    private final FlowStats flowStatistics;
    private final List<StepUsageDeclaration> steps;
    private Map<StepUsageDeclaration,List<DataDefinitionDeclaration>> stepToFreeInputFinalNameToDD;
    private Map<String,DataDefinitionDeclaration> freeInputFinalNameToDD;
    private Map<String,DataDefinitionDeclaration> formalFinalOutPutNameToDD;
    private List<continuationFlowDetails> flowsContinuations;
    private Map<String, Object> initialValues;

    public FlowDefinitionImpl(String name, String description) {
        this.name = name;
        this.description = description;
        readOnly = true;
        steps = new ArrayList<>();
        formalFinalOutPutNameToDD = new HashMap<>();
        flowStatistics = new FlowStats();
        flowsContinuations = new ArrayList<>();
        initialValues = new HashMap<>();
    }

    public class continuationFlowDetails{
        private FlowDefinition targetFlow; //The target flow to continue
        private Map<String,String> sourceToTargetInput;

        continuationFlowDetails(FlowDefinition flow, Map<String,String> contDetails) throws InvalidContinuationsData{
            targetFlow = flow;
            for(Map.Entry<String,String> val : contDetails.entrySet()){
                boolean found = false;
                for(StepUsageDeclaration step : steps){
                    if(step.getFinalNameToOutput().containsKey(val.getKey()) || step.getFinalNameToInput().containsKey(val.getKey())){
                        found = true;
                    }
                }
                if(!found)
                    throw new InvalidContinuationsData("In flow " + name + " the source data in the continuation:" + val.getKey() + " does not output of the flow.");
            }
            sourceToTargetInput = contDetails;
        }

        public FlowDefinition getTargetFlow() {
            return targetFlow;
        }

        public Map<String, String> getSourceToTargetInput() {
            return sourceToTargetInput;
        }
    }
    @Override
    public void addFormalOutput(String name, DataDefinitionDeclaration data) {
        formalFinalOutPutNameToDD.put(name,data);
    }

    @Override
    public boolean isReadOnly(){return readOnly;}

    @Override
    public void setReadOnly(boolean bool) {
        readOnly = bool;
    }

    @Override
    public Map<String,DataDefinitionDeclaration> getFormalOutput() {
        return formalFinalOutPutNameToDD;
    }

    @Override
    public void addFlowRunStepStats(StepUsageDeclaration step, Duration time) {
        flowStatistics.addStepStats(step,time);
    }

    @Override
    public void addFlowRunStats(Duration time) {
        flowStatistics.addFlowRunStats(time);
    }
    @Override
    public void addContinuation(FlowDefinition name, Map<String,String> data) throws StepperExeption{
        flowsContinuations.add(new continuationFlowDetails(name,data));
    }
    @Override
    public List<continuationFlowDetails> getFlowsContinuations(){ return flowsContinuations;}
    public FlowStats getFlowStatistics() {
        return flowStatistics;
    }

    public void validateFlowStructure() throws StepperExeption {
        /*
        This function doing check of the flow stracture by:
        Check and save the free input
        Check that all input (not free) have an output to get data
        Check the free outputs and save them
        Check there in no problem in the aliasing
         */
        Map<String,DataDefinitionDeclaration> inputOnTheWay = new HashMap<>();
        freeInputFinalNameToDD = new HashMap<>();
        stepToFreeInputFinalNameToDD = new HashMap<>();
        //Over all the steps, and his inputs, and check if there are have output from the lasts steps
        for(StepUsageDeclaration step : steps) {
            List<DataDefinitionDeclaration> freeInputStepDD = new ArrayList<>();
            List<DataDefinitionDeclaration> stepInputs = step.getStepDefinition().inputs();
            for(DataDefinitionDeclaration data : stepInputs) {
                String customMappingData = step.thisInputHaveCustomeMapping(step.getinputToFinalName().get(data.getName())); //Check if this input have custom mapping
                if (customMappingData != null) {//If there is custom mapping to the input
                    if (!inputOnTheWay.containsKey(customMappingData))//If there is custom mapping but there is no output that return from other steps to take ---> exception
                        throw new CustomeMappingInvalid("In flow: " + name + " the input: " + step.getinputToFinalName().get(data.getName()) + " have no data to take as the custom mapping says.");
                    if(inputOnTheWay.get(customMappingData).dataDefinition().getType() != data.dataDefinition().getType())
                        throw new CustomeMappingInvalid("In flow: " + name + " the input: " + step.getinputToFinalName().get(data.getName()) + " have no data to take with the same data type as the custom mapping says.");
                } else {
                    //If there is no custom mapping - check if there is an output to the input
                    boolean exist = false;
                    for (Map.Entry<String, DataDefinitionDeclaration> entry : inputOnTheWay.entrySet()) {
                        String key = entry.getKey();
                        DataDefinitionDeclaration value = entry.getValue();
                        if (key.equals(step.getinputToFinalName().get(data.getName())) && value.dataDefinition().getType() == data.dataDefinition().getType()) {
                            exist = true;
                        }
                    }
                    if (!exist) {
                        //There is no output to take for the input - so its free input
                        if(!data.dataDefinition().isUserFriendly())//If the free input does not user-friendly
                            throw new FreeInputNotUserFriendly("In flow: " + name + ", the free input: " + step.getinputToFinalName().get(data.getName()) + " cannot get from user");
                        if(freeInputFinalNameToDD.containsKey(step.getinputToFinalName().get(data.getName()))) {
                            if (freeInputFinalNameToDD.get(step.getinputToFinalName().get(data.getName())).dataDefinition().getType() != data.dataDefinition().getType())//It's mean that there are two free inputs with other DD
                                throw new SameFreeInputNamesButNoDD("In flow: " + name + ", the are duplicate free input called:" + step.getinputToFinalName().get(data.getName()) + " but with other definition");
                        }
                        else
                            freeInputFinalNameToDD.put(step.getinputToFinalName().get(data.getName()), data);
                        freeInputStepDD.add(data);
                    }
                }
            }
            //Check if there are two outputs in the flow with the same name
            final boolean[] thereIsTwoOutputsInTheSameName = {false};
            step.getStepDefinition().outputs().stream().forEach(outPut -> {
                    if (!inputOnTheWay.containsKey(step.getoutputToFinalName().get(outPut.getName())))
                        inputOnTheWay.put(step.getoutputToFinalName().get(outPut.getName()), outPut);
                    else
                        thereIsTwoOutputsInTheSameName[0] = true;
                });
            if(thereIsTwoOutputsInTheSameName[0])
                throw new DuplicateOutputsNames("In flow: " + name + " Error while reading the flow - there are two outputs with the same name");

            if(freeInputStepDD.size() > 0)
                stepToFreeInputFinalNameToDD.put(step,freeInputStepDD);
        }
    }

    @Override
    public Map<StepUsageDeclaration,List<DataDefinitionDeclaration>> getFlowFreeInputs() {
        return stepToFreeInputFinalNameToDD;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<StepUsageDeclaration> getFlowSteps() {
        return steps;
    }

    @Override
    public Map<String,DataDefinitionDeclaration> getFreeInputFinalNameToDD(){return freeInputFinalNameToDD;}

    @Override
    public Map<String, Object> getInitialValues() {
        return initialValues;
    }
    @Override
    public void addInitialValue(String finalNameInput, Object dataInput){
        initialValues.put(finalNameInput,dataInput);
    }
}
