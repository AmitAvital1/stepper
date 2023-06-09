package project.java.stepper.flow.definition.api;

import project.java.stepper.dd.api.DataDefinition;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import project.java.stepper.step.api.StepDefinition;

import java.util.HashMap;
import java.util.Map;

public class StepUsageDeclarationImpl implements StepUsageDeclaration {
    private final StepDefinition stepDefinition;
    private final boolean skipIfFail;
    private final String stepName;
    private final Map<String,String> inputsToFinalNames;
    private final Map<String,String> outputsToFinalNames;
    private final Map<String,String> finalNamesToInput;
    private final Map<String,String> finalNamesToOutput;

    private final Map<String, DataDefinition> finalNamesOutputsToDD;
    private final Map<String, DataDefinition> finalNamesInputsToDD;

    private final Map<String,String> customeMapInput;

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
        finalNamesOutputsToDD = new HashMap<>();
        finalNamesInputsToDD = new HashMap<>();

        stepDefinition.inputs().stream().forEach(stepD -> inputsToFinalNames.put(stepD.getName(),stepD.getName()));
        stepDefinition.outputs().stream().forEach(stepD -> outputsToFinalNames.put(stepD.getName(),stepD.getName()));

        stepDefinition.inputs().stream().forEach(stepD -> finalNamesToInput.put(stepD.getName(),stepD.getName()));
        stepDefinition.outputs().stream().forEach(stepD -> finalNamesToOutput.put(stepD.getName(),stepD.getName()));

        stepDefinition.inputs().stream().forEach(stepD -> finalNamesInputsToDD.put(stepD.getName(),stepD.dataDefinition()));
        stepDefinition.outputs().stream().forEach(stepD -> finalNamesOutputsToDD.put(stepD.getName(),stepD.dataDefinition()));

    }

    @Override
    public void addCustomeMapInput(String inputName,String inputTarget){customeMapInput.put(inputName,inputTarget);}

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
    public Map<String, DataDefinition> getFinalNamesOutputsToDD() {
        return finalNamesOutputsToDD;
    }
    @Override
    public Map<String, DataDefinition> getFinalNamesInputsToDD() {
        return finalNamesInputsToDD;
    }

    @Override
    public boolean addLevelAlias(String name, String finalName) {
        if(inputsToFinalNames.containsKey(name)) {
            inputsToFinalNames.put(name, finalName);
            finalNamesToInput.remove(name);
            finalNamesToInput.put(finalName,name);
            DataDefinition dd = finalNamesInputsToDD.remove(name);
            finalNamesInputsToDD.put(finalName,dd);
            return true;
        }
        else if(outputsToFinalNames.containsKey(name)) {
            outputsToFinalNames.put(name, finalName);
            finalNamesToOutput.remove(name);
            finalNamesToOutput.put(finalName,name);
            DataDefinition dd = finalNamesOutputsToDD.remove(name);
            finalNamesOutputsToDD.put(finalName,dd);
            return true;
        }
        else
            return false;
    }
    @Override
    public boolean equals(Object o) {

        if(o.getClass() != StepUsageDeclaration.class )
            return false;
        StepUsageDeclaration other = (StepUsageDeclaration)o;

        if(this.stepName.equals(other.getFinalStepName()) && this.stepDefinition.name().equals(other.getStepDefinition().name()))
            return true;

        return false;

    }
}
