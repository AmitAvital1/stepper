package dto;

import project.java.stepper.dd.api.DataDefinition;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowDefinitionDTO {

    private final String name;
    private final String description;
    private final boolean readOnly;
    private final List<StepUsageDeclarationImplDTO> steps;
    private final Map<String, DataDefinitionDeclarationDTO> freeInputFinalNameToDD;
    private final Map<String, DataDefinitionDeclarationDTO> formalFinalOutPutNameToDD;
    private final Map<String, Object> initialValues;


    public FlowDefinitionDTO(FlowDefinition flow){
        name = flow.getName();
        description = flow.getDescription();
        readOnly = flow.isReadOnly();
        steps = converStepsToDTO(flow.getFlowSteps());
        //stepToFreeInputFinalNameToDD = convertStepToFreeInputFinalNameToDD(flow.getFlowFreeInputs());
        freeInputFinalNameToDD = castToDDDeclerationImpl(flow.getFreeInputFinalNameToDD());
        formalFinalOutPutNameToDD = castToDDDeclerationImpl(flow.getFormalOutput());
        initialValues = flow.getInitialValues();
    }

    private Map<StepUsageDeclarationImplDTO, List<DataDefinitionDeclarationDTO>> convertStepToFreeInputFinalNameToDD(Map<StepUsageDeclaration, List<DataDefinitionDeclaration>> flowFreeInputs) {
        Map<StepUsageDeclarationImplDTO,List<DataDefinitionDeclarationDTO>> res = new HashMap<>();
        for(Map.Entry<StepUsageDeclaration, List<DataDefinitionDeclaration>> entry : flowFreeInputs.entrySet()) {
            List<DataDefinitionDeclarationDTO> lst = new ArrayList<>();
            for(DataDefinitionDeclaration dd : entry.getValue())
                lst.add(new DataDefinitionDeclarationDTO(dd));

            res.put(new StepUsageDeclarationImplDTO(entry.getKey()), lst);
        }
        return res;
    }

    private Map<String, DataDefinitionDeclarationDTO> castToDDDeclerationImpl(Map<String, DataDefinitionDeclaration> freeInputFinalNameToDD) {
        Map<String, DataDefinitionDeclarationDTO> res = new HashMap<>();
        for(Map.Entry<String, DataDefinitionDeclaration> entry : freeInputFinalNameToDD.entrySet()) {
            res.put(entry.getKey(), new DataDefinitionDeclarationDTO(entry.getValue()));
        }
        return res;
    }

    private List<StepUsageDeclarationImplDTO> converStepsToDTO(List<StepUsageDeclaration> flowSteps) {
        List<StepUsageDeclarationImplDTO> res = new ArrayList<>();
        for(StepUsageDeclaration step : flowSteps){
            res.add(new StepUsageDeclarationImplDTO(step));
        }
        return res;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public List<StepUsageDeclarationImplDTO> getFlowSteps() {
        return steps;
    }

    public Map<String, DataDefinitionDeclarationDTO> getFreeInputFinalNameToDD() {
        return freeInputFinalNameToDD;
    }

    public Map<String, DataDefinitionDeclarationDTO> getFormalOutput() {
        return formalFinalOutPutNameToDD;
    }


    public Map<String, Object> getInitialValues() {
        return initialValues;
    }
}
