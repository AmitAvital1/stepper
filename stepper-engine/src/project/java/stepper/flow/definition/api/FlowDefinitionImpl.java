package project.java.stepper.flow.definition.api;

import project.java.stepper.exceptions.CustomeMappingInvalid;
import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import project.java.stepper.step.api.DataNecessity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlowDefinitionImpl implements FlowDefinition {

    private final String name;
    private final String description;
    private boolean readOnly;
    private final List<StepUsageDeclaration> steps;
    private Map<StepUsageDeclaration,List<DataDefinitionDeclaration>> stepToFreeInputFinalNameToDD;
    private Map<String,DataDefinitionDeclaration> freeInputFinalNameToDD;
    private  Map<String,DataDefinitionDeclaration> formalFinalOutPutNameToDD;

    @Override
    public void addFormalOutput(String name, DataDefinitionDeclaration data) {
        formalFinalOutPutNameToDD.put(name,data);
    }

    public boolean isReadOnly(){return readOnly;}

    @Override
    public void setReadOnly(boolean bool) {
        readOnly = bool;
    }

    @Override
    public Map<String,DataDefinitionDeclaration> getFormalOutput() {
        return formalFinalOutPutNameToDD;
    }

    public FlowDefinitionImpl(String name, String description) {
        this.name = name;
        this.description = description;
        readOnly = true;
        steps = new ArrayList<>();
        formalFinalOutPutNameToDD = new HashMap<>();
    }

    public void validateFlowStructure() throws CustomeMappingInvalid {
        Map<String,DataDefinitionDeclaration> inputOnTheWay = new HashMap<>();
        freeInputFinalNameToDD = new HashMap<>();
        stepToFreeInputFinalNameToDD = new HashMap<>();
        for(StepUsageDeclaration step : steps) {
            List<DataDefinitionDeclaration> freeInputStepDD = new ArrayList<>();
            List<DataDefinitionDeclaration> stepInputs = step.getStepDefinition().inputs();
            for(DataDefinitionDeclaration data : stepInputs) {
                String customMappingData = step.thisInputHaveCustomeMapping(step.getinputToFinalName().get(data.getName()));
                if (customMappingData != null) {
                    if (!inputOnTheWay.containsKey(customMappingData))
                        throw new CustomeMappingInvalid("The input: " + step.getinputToFinalName().get(data.getName()) + " have no data to take as the custome mapping says.");
                } else {
                    boolean exist = false;
                    for (Map.Entry<String, DataDefinitionDeclaration> entry : inputOnTheWay.entrySet()) {
                        String key = entry.getKey();
                        DataDefinitionDeclaration value = entry.getValue();
                        if (key.equals(step.getinputToFinalName().get(data.getName())) && value.dataDefinition().getType() == data.dataDefinition().getType()) {
                            exist = true;
                        }
                    }
                    if (!exist) {
                        freeInputFinalNameToDD.put(step.getinputToFinalName().get(data.getName()), data);
                        freeInputStepDD.add(data);
                    }
                }
            }
            step.getStepDefinition().outputs().stream().forEach(outPut -> inputOnTheWay.put(step.getoutputToFinalName().get(outPut.getName()),outPut));
            if(freeInputStepDD.size() > 0)
                stepToFreeInputFinalNameToDD.put(step,freeInputStepDD);
        }
        //In the last over all dd to check if non freindly

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
}
