package project.java.stepper.flow.execution;


import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.FlowDefinitionImpl;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.StepExecutionContextImpl;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import project.java.stepper.step.api.DataNecessity;

import java.time.Duration;
import java.util.*;

public class FlowExecution {

    private final String uniqueId;
    private final FlowDefinition flowDefinition;
    private StepExecutionContext flowContexts;
    private Duration totalTime;
    private String startedTime;
    private ObjectProperty<FlowExecutionResult> flowExecutionResult = new SimpleObjectProperty<>();
    private Map<String, Object> startersFreeInputForContext;
    private Map<String, Object> allDataValues;
    private List<flowOutputsData> outputsStepData = new ArrayList<>();
    private IntegerProperty stepFinished = new SimpleIntegerProperty(0);

    public IntegerProperty getStepFinishedProperty() {
        return stepFinished;
    }
    public void addStepFinished(int adder) {
        stepFinished.setValue(stepFinished.get() + adder);
    }


    public static class flowOutputsData{
        private final String finalName;
        private StepUsageDeclaration outputStep;
        private final DataDefinitionDeclaration outputDD;
        private final Object data;
        boolean createdFromFlow = true;
        public flowOutputsData(String finalName, StepUsageDeclaration outputStep, DataDefinitionDeclaration outputDD,Object data){
            this.finalName = finalName;
            this.outputStep = outputStep;
            this.outputDD = outputDD;
            this.data = data;
        }
        public flowOutputsData(String finalName, StepUsageDeclaration outputStep, DataDefinitionDeclaration outputDD,Object data,boolean created){
            this.finalName = finalName;
            this.outputStep = outputStep;
            this.outputDD = outputDD;
            this.data = data;
            createdFromFlow = created;
        }
        public boolean isOutputExist(String finalName,DataDefinitionDeclaration dd){
               if(this.finalName.equals(finalName) && this.outputDD.dataDefinition().getName().equals(dd.dataDefinition().getName()))
                   return true;
               else return false;
        }
        public boolean getCreatedFromFlow(){return createdFromFlow;}

        public String getFinalName() {
            return finalName;
        }

        public StepUsageDeclaration getOutputStep() {
            return outputStep;
        }

        public DataDefinitionDeclaration getOutputDD() {
            return outputDD;
        }

        public Object getData() {
            return data;
        }
    }
    public FlowExecution(String uniqueId, FlowDefinition flowDefinition) {
        this.uniqueId = uniqueId;
        this.flowDefinition = flowDefinition;
        startersFreeInputForContext = new HashMap<>(this.flowDefinition.getInitialValues());
    }
    public void setFlowContexts(StepExecutionContext context){flowContexts = context;}
    public StepExecutionContext getFlowContexts(){return  flowContexts;}
    public void setAllDataValues(Map<String, Object> allDataValues) {this.allDataValues = allDataValues;}
    public String getUniqueId() {
        return uniqueId;
    }
    public String getStartedTime() {
        return startedTime;
    }
    public void setStartedTime(String time) {
        startedTime = time;
    }
    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }
    public void setFlowExecutionResult(FlowExecutionResult result) {
        Platform.runLater(() -> {
            flowExecutionResult.setValue(result);
        });
    }
    public ObjectProperty<FlowExecutionResult> getFlowExecutionResultProperty(){return flowExecutionResult;}
    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult.get();
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
                    throw new MissMandatoryInput("Missing Mandatory input: " + dd.userString() + "[" + key.getinputToFinalName().get(dd.getName()) + "]");
                }
            }

        }
        return res;
    }
    public boolean addFreeInputForStart(String inputName,DataDefinitionDeclaration dataDefinitionDeclaration, String data) throws StepperExeption {
        Object newData = dataDefinitionDeclaration.dataDefinition().convertUserInputToDataType(data,dataDefinitionDeclaration.dataDefinition().getType());
        startersFreeInputForContext.put(inputName,newData);
        return true;
    }
    public Map<String, Object> getStartersFreeInputForContext() {
        return startersFreeInputForContext;
    }
    public List<String> getAllFreeInputsWithDataToPrintList(){
        int i = 1;
        //First print all the mandatories input:
        List<String> data = new ArrayList<>();
        for (Map.Entry<String, DataDefinitionDeclaration> entry : flowDefinition.getFreeInputFinalNameToDD().entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclaration dd = entry.getValue();
                if(dd.necessity() == DataNecessity.MANDATORY) {
                    String inputFinalName = key;
                    Object inputContext = startersFreeInputForContext.get(inputFinalName);
                    data.add(i + "." + inputFinalName + "[" + inputContext + "]" + "(" + dd.dataDefinition().getName() + ") - " +
                            (!getFlowDefinition().getInitialValues().containsKey(inputFinalName) ? dd.necessity() : "Initial Value"));
                    i++;
            }
        }
        //Print optional free inputs
        for (Map.Entry<String, DataDefinitionDeclaration> entry : flowDefinition.getFreeInputFinalNameToDD().entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclaration dd = entry.getValue();

                if(dd.necessity() == DataNecessity.OPTIONAL) {
                    String inputFinalName = key;
                    Optional<Object> inputContext = Optional.ofNullable(startersFreeInputForContext.get(inputFinalName));
                    if(inputContext.isPresent()) {
                        data.add(i + "." + inputFinalName + "[" + inputContext.get() + "]" + "(" + dd.dataDefinition().getName() + ") - " +
                                (!getFlowDefinition().getInitialValues().containsKey(inputFinalName) ? dd.necessity() : "Initial Value"));
                        i++;
                    }
            }
        }
        return data;
    }
    public List<String> getAllOutPutsWithDataToPrintList() {
        int i = 1;
        boolean noOutput = false;
        List<String> outputsString = new ArrayList<>();
        for(flowOutputsData output : outputsStepData){
            String outputLine = i + "." + output.finalName + "," + output.outputDD.userString() + "(" + output.outputDD.dataDefinition().getName() + ")";
            if(output.data.getClass() == String.class) {
                if (output.data.equals("Not created due to failure in flow")) {
                    outputLine += "-NOTE:Not created due to failure in flow";
                    noOutput = true;
                }
            }
            if(!noOutput){
                outputLine += "\nData:" + output.data.toString();
            }

            outputsString.add(outputLine);
            i++;
        }
        return outputsString;
    }
    public List<flowOutputsData> getOutputsStepData(){return outputsStepData; }

    public List<StepExecutionContextImpl.stepData> getStepsData(){
        List<StepExecutionContextImpl.stepData> lst = new ArrayList<>();
        for(StepUsageDeclaration step : flowDefinition.getFlowSteps()) {
            StepExecutionContextImpl.stepData data = flowContexts.getStepData(step);
            lst.add(data);
        }
        return lst;
    }
    public List<String> getAllStepsWithDataToPrintList() {
        int i = 1;
        List<String> stepsString = new ArrayList<>();
        for(StepUsageDeclaration step : flowDefinition.getFlowSteps()){
            StepExecutionContextImpl.stepData data = flowContexts.getStepData(step);
            String line = i + ".";
            line += step.getFinalStepName();
            if(data != null) {
                if (!step.getFinalStepName().equals(step.getStepDefinition().name()))
                    line += "(" + data.step.getStepDefinition().name() + ")";
                line += ", Total Time:[" + data.time.toMillis() + ".ms]" + ", Result:" + data.result;
                line += "\n" + data.stepSummaryLine + ",Total logs(" + data.logs.getStepLogs().size() + "):";
                for (String log : data.logs.getStepLogs())
                    line += "\n" + log;
            }
            else
                line += " don't have data during failure of the step";

            stepsString.add(line);
            i++;
        }
        return stepsString;
    }
    public void addFlowOutputsData(List<flowOutputsData> outputsData){
        //This function get from context all the outputs that success. If there are failure output this function will add him.
        outputsStepData = outputsData;
        for (StepUsageDeclaration key : flowDefinition.getFlowSteps()) {
            List<DataDefinitionDeclaration> value = key.getStepDefinition().outputs();
            for (DataDefinitionDeclaration dd : value) {
                String outputFinalName = key.getoutputToFinalName().get(dd.getName());
                if(outputsStepData.stream().noneMatch(outputData -> outputData.isOutputExist(outputFinalName,dd))) {
                    outputsStepData.add(new flowOutputsData(outputFinalName,key,dd,"Not created due to failure in flow",false));
                }
            }
        }
    }
    public Map<String,Object> getFormalOutPutsData(){
        Map<String,Object> formalOutputToData = new HashMap<>();
        for (Map.Entry<String, DataDefinitionDeclaration> entry : flowDefinition.getFormalOutput().entrySet()) {
            String newVal = entry.getKey() + "-" + entry.getValue().userString();
            Object key = outputsStepData.stream().filter(output -> output.finalName.equals(entry.getKey())).findFirst().get().data;
            formalOutputToData.put(newVal,key);
        }
        return formalOutputToData;
    }
    public FlowExecution runContinuationFlow(FlowDefinitionImpl.continuationFlowDetails targetFlowDetails){
        UUID uuid = UUID.randomUUID();
        FlowExecution flowExecution = new FlowExecution(uuid.toString(), targetFlowDetails.getTargetFlow());
        for(Map.Entry<String,String> sourceToTarget : targetFlowDetails.getSourceToTargetInput().entrySet()){
            Optional<flowOutputsData> flowOutput = outputsStepData.stream().
                    filter(outputData -> outputData.getFinalName().equals(sourceToTarget.getKey()))
                    .findFirst();
            boolean freeInput = startersFreeInputForContext.containsKey(sourceToTarget.getKey());
            if(flowOutput.isPresent()){
                if(flowOutput.get().createdFromFlow && !flowExecution.getFlowDefinition().getInitialValues().containsKey(flowOutput.get().finalName))
                    flowExecution.startersFreeInputForContext.put(sourceToTarget.getValue(),flowOutput.get().data);
            }
            else if(freeInput){
                if(!flowExecution.getFlowDefinition().getInitialValues().containsKey(sourceToTarget.getKey()))
                    flowExecution.startersFreeInputForContext.put(sourceToTarget.getValue(), startersFreeInputForContext.get(sourceToTarget.getKey()));
            }
        }
        //Do automatic mapping for continuation (outputs values ---> free inputs) with the same name and same dd
        outputsStepData.stream().forEach(out -> {
                if(out.createdFromFlow && flowExecution.getFlowDefinition().getFreeInputFinalNameToDD().containsKey(out.finalName)){
                    if(flowExecution.getFlowDefinition().getFreeInputFinalNameToDD().get(out.finalName).dataDefinition().getType() == out.getOutputDD().dataDefinition().getType())
                        if(!flowExecution.getFlowDefinition().getInitialValues().containsKey(out.finalName))
                            flowExecution.startersFreeInputForContext.put(out.finalName,out.data);
                }
        });
        //Do automatic mapping for continuation (free inputs values ---> free inputs (in the next flow) with the same name and same dd
        startersFreeInputForContext.entrySet().stream().forEach(set -> {
            if(flowExecution.getFlowDefinition().getFreeInputFinalNameToDD().containsKey(set.getKey()))
                if(flowExecution.getFlowDefinition().getFreeInputFinalNameToDD().get(set.getKey()).dataDefinition().getType() == getFlowDefinition().getFreeInputFinalNameToDD().get(set.getKey()).dataDefinition().getType())
                    if(!flowExecution.getFlowDefinition().getInitialValues().containsKey(set.getKey()))
                        flowExecution.startersFreeInputForContext.put(set.getKey(),set.getValue());
        });
        return flowExecution;
    }
    public FlowExecution reRunFlow(){
        UUID uuid = UUID.randomUUID();
        FlowExecution flowExecution = new FlowExecution(uuid.toString(), flowDefinition);
        Map<String,Object> copyFreeInputs = new HashMap<>(startersFreeInputForContext);
        flowExecution.setStartersFreeInputForContext(copyFreeInputs);
        return flowExecution;
    }
    public void setStartersFreeInputForContext(Map<String,Object> newContext){
        startersFreeInputForContext = newContext;
    }
}
