package project.java.stepper.flow.execution;

import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
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
    private FlowExecutionResult flowExecutionResult;
    private final Map<String, Object> startersFreeInputForContext;
    private Map<String, Object> allDataValues;
    private List<flowOutputsData> outputsStepData;


    public static class flowOutputsData{
        private String finalName;
        private StepUsageDeclaration outputStep;
        private DataDefinitionDeclaration outputDD;
        private Object data;
        public flowOutputsData(String finalName, StepUsageDeclaration outputStep, DataDefinitionDeclaration outputDD,Object data){
            this.finalName = finalName;
            this.outputStep = outputStep;
            this.outputDD = outputDD;
            this.data = data;
        }
        public boolean isOutputExist(String finalName,DataDefinitionDeclaration dd){
               if(this.finalName.equals(finalName) && this.outputDD.dataDefinition().getName().equals(dd.dataDefinition().getName()))
                   return true;
               else return false;
        }
    }
    public FlowExecution(String uniqueId, FlowDefinition flowDefinition) {
        this.uniqueId = uniqueId;
        this.flowDefinition = flowDefinition;
        startersFreeInputForContext = new HashMap<>();

    }
    public void setFlowContexts(StepExecutionContext context){flowContexts = context;}
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
    public boolean addFreeInputForStart(StepUsageDeclaration step,DataDefinitionDeclaration dataDefinitionDeclaration, String data) throws StepperExeption {
        Object newData = dataDefinitionDeclaration.dataDefinition().convertUserInputToDataType(data,dataDefinitionDeclaration.dataDefinition().getType());
        startersFreeInputForContext.put(step.getinputToFinalName().get(dataDefinitionDeclaration.getName()),newData);
        return true;
    }
    public Map<String, Object> getStartersFreeInputForContext() {
        return startersFreeInputForContext;
    }
    public List<String> getAllFreeInputsWithDataToPrintList(){
        //First print all the mandatories input:
        List<String> data = new ArrayList<>();
        for (Map.Entry<StepUsageDeclaration, List<DataDefinitionDeclaration>> entry : flowDefinition.getFlowFreeInputs().entrySet()) {
            StepUsageDeclaration key = entry.getKey();
            List<DataDefinitionDeclaration> value = entry.getValue();
            for (DataDefinitionDeclaration dd : value) {
                if(dd.necessity() == DataNecessity.MANDATORY) {
                    String inputFinalName = key.getinputToFinalName().get(dd.getName());
                    Object inputContext = startersFreeInputForContext.get(inputFinalName);
                    data.add(inputFinalName + "[" + inputContext + "]" + "(" + dd.dataDefinition().getName() + ") - " +
                            dd.necessity());
                }
            }
        }
        //Print optional free inputs
        for (Map.Entry<StepUsageDeclaration, List<DataDefinitionDeclaration>> entry : flowDefinition.getFlowFreeInputs().entrySet()) {
            StepUsageDeclaration key = entry.getKey();
            List<DataDefinitionDeclaration> value = entry.getValue();
            for (DataDefinitionDeclaration dd : value) {
                if(dd.necessity() == DataNecessity.OPTIONAL) {
                    String inputFinalName = key.getinputToFinalName().get(dd.getName());
                    Optional<Object> inputContext = Optional.ofNullable(startersFreeInputForContext.get(inputFinalName));
                    if(inputContext.isPresent()) {
                        data.add(inputFinalName + "[" + inputContext.get() + "]" + "(" + dd.dataDefinition().getName() + ") - " +
                                dd.necessity());
                    }
                }
            }
        }
        return data;
    }
    public List<String> getAllOutPutsWithDataToPrintList() {
        List<String> outputsString = new ArrayList<>();
        for(flowOutputsData output : outputsStepData){
            String outputLine = output.finalName + "," + output.outputDD.userString() + "(" + output.outputDD.dataDefinition().getName() + ")";
            if(output.data.getClass() == String.class)
                if(output.data.equals("Not created due to failure in flow"))
                    outputLine += "-NOTE:Not created due to failure in flow";
            outputsString.add(outputLine);
        }
        return outputsString;
    }
    public List<String> getAllStepsWithDataToPrintList() {
        List<String> stepsString = new ArrayList<>();
        for(StepUsageDeclaration step : flowDefinition.getFlowSteps()){
            StepExecutionContextImpl.stepData data = flowContexts.getStepData(step);
            String line;
            line = step.getFinalStepName();
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
                    outputsStepData.add(new flowOutputsData(outputFinalName,key,dd,"Not created due to failure in flow"));
                }
            }
        }
    }
}
