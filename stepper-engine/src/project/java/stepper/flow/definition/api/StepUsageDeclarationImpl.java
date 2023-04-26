package project.java.stepper.flow.definition.api;

import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.StepDefinition;
import project.java.stepper.step.api.StepResult;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class StepUsageDeclarationImpl implements StepUsageDeclaration {
    private final StepDefinition stepDefinition;
    private final boolean skipIfFail;
    private final String stepName;
    private StepResult stepResult;
    private StepLogs stepLogs;
    private Duration totalTime;
    private String summaryLine;
    private final Map<String,String> inputsToFinalNames;
    private final Map<String,String> outputsToFinalNames;
    private final Map<String,String> finalNamesToInput;
    private final Map<String,String> finalNamesToOutput;

    private final Map<String,String> customeMapInput;

    public void addCustomeMapInput(String inputName,String inputTarget){customeMapInput.put(inputName,inputTarget);}

    public StepUsageDeclarationImpl(StepDefinition stepDefinition) {
        this(stepDefinition, false, stepDefinition.name());
    }


    public StepUsageDeclarationImpl(StepDefinition stepDefinition, String name) {
        this(stepDefinition, false, name);
    }
    public String thisInputHaveCustomeMapping(String data){return customeMapInput.get(data);}
    public StepUsageDeclarationImpl(StepDefinition stepDefinition, boolean skipIfFail, String stepName) {
        this.stepDefinition = stepDefinition;
        this.skipIfFail = skipIfFail;
        this.stepName = stepName;
        inputsToFinalNames = new HashMap<>();
        outputsToFinalNames = new HashMap<>();
        finalNamesToInput = new HashMap<>();
        finalNamesToOutput = new HashMap<>();
        customeMapInput = new HashMap<>();

        stepDefinition.inputs().stream().forEach(stepD -> inputsToFinalNames.put(stepD.getName(),stepD.getName()));
        stepDefinition.outputs().stream().forEach(stepD -> outputsToFinalNames.put(stepD.getName(),stepD.getName()));

        stepDefinition.inputs().stream().forEach(stepD -> finalNamesToInput.put(stepD.getName(),stepD.getName()));
        stepDefinition.outputs().stream().forEach(stepD -> finalNamesToOutput.put(stepD.getName(),stepD.getName()));

    }

    @Override
    public String getFinalStepName() {
        return stepName;
    }

    @Override
    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }

    @Override
    public boolean skipIfFail() {
        return skipIfFail;
    }

    @Override
    public StepResult getStepResult() {
        return stepResult;
    }

    @Override
    public StepLogs getStepLogs() {
        return stepLogs;
    }

    @Override
    public void setStepLogs(StepLogs logs) {
        stepLogs = logs;
    }

    @Override
    public void setStepResult(StepResult result) {
        stepResult = result;
    }

    @Override
    public Map<String, String> getinputToFinalName() {
        return inputsToFinalNames;
    }

    @Override
    public Map<String, String> getoutputToFinalName() {
        return outputsToFinalNames;
    }

    @Override
    public Map<String, String> getFinalNameToInput() {
        return finalNamesToInput;
    }

    @Override
    public Map<String, String> getFinalNameToOutput() {
        return finalNamesToOutput;
    }

    @Override
    public void setDuration(Duration time){
        totalTime = time;
    }

    @Override
    public long getDuration(){
        return totalTime.toMillis();
    }

    @Override
    public void setSummaryLine(String line) {
        summaryLine = line;
    }

    @Override
    public String getSummaryLine() {
        return summaryLine;
    }

    @Override
    public boolean addLevelAlias(String name, String finalName) {
        if(inputsToFinalNames.containsKey(name)) {
            inputsToFinalNames.put(name, finalName);
            finalNamesToInput.put(finalName,name);
            return true;
        }
        else if(outputsToFinalNames.containsKey(name)) {
            outputsToFinalNames.put(name, finalName);
            finalNamesToOutput.put(finalName,name);
            return true;
        }
        else
            return false;
    }
}
