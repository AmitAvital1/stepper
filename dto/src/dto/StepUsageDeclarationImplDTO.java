package dto;

import project.java.stepper.dd.api.DataDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.step.api.StepDefinition;

import java.util.HashMap;
import java.util.Map;

public class StepUsageDeclarationImplDTO {
    private final StepDefinitionDTO stepDefinition;
    private final boolean skipIfFail;
    private final String stepName;
    private final Map<String,String> inputsToFinalNames;
    private final Map<String,String> outputsToFinalNames;
    private final Map<String,String> finalNamesToInput;
    private final Map<String,String> finalNamesToOutput;

    private final Map<String, DataDefinitionDTO> finalNamesOutputsToDD;
    private final Map<String, DataDefinitionDTO> finalNamesInputsToDD;


    public StepUsageDeclarationImplDTO(StepUsageDeclaration step) {
        stepDefinition = new StepDefinitionDTO(step.getStepDefinition());
        this.skipIfFail = step.skipIfFail();;
        this.stepName = step.getFinalStepName();
        this.inputsToFinalNames = step.getinputToFinalName();
        this.outputsToFinalNames = step.getoutputToFinalName();
        this.finalNamesToInput = step.getFinalNameToInput();
        this.finalNamesToOutput = step.getFinalNameToOutput();

        this.finalNamesOutputsToDD = convertfinalNamesOutputsToDD(step.getFinalNamesOutputsToDD());
        this.finalNamesInputsToDD = convertfinalNamesOutputsToDD(step.getFinalNamesInputsToDD());
    }

    private Map<String, DataDefinitionDTO> convertfinalNamesOutputsToDD(Map<String, DataDefinition> toConvert) {
        Map<String, DataDefinitionDTO> res = new HashMap<>();
        for(Map.Entry<String, DataDefinition> entry : toConvert.entrySet()) {
            res.put(entry.getKey(), new DataDefinitionDTO(entry.getValue()));
        }
        return res;
    }

    public StepDefinitionDTO getStepDefinition() {
        return stepDefinition;
    }

    public boolean isSkipIfFail() {
        return skipIfFail;
    }

    public String getFinalStepName() {
        return stepName;
    }

    public Map<String, String> getinputToFinalName() {
        return inputsToFinalNames;
    }

    public Map<String, String> getoutputToFinalName() {
        return outputsToFinalNames;
    }

    public Map<String, String> getFinalNameToInput() {
        return finalNamesToInput;
    }

    public Map<String, String> getFinalNameToOutput() {
        return finalNamesToOutput;
    }

    public Map<String, DataDefinitionDTO> getFinalNamesOutputsToDD() {
        return finalNamesOutputsToDD;
    }

    public Map<String, DataDefinitionDTO> getFinalNamesInputsToDD() {
        return finalNamesInputsToDD;
    }
}
