package dto;

import dto.execution.SqlFilterDTO;
import project.java.stepper.dd.api.DataDefinition;
import project.java.stepper.dd.impl.SqlFilter.SqlFilter;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.UIDDPresent;

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
    private final Map<String, SqlFilterDTO> initialValueForSqlFilter = new HashMap<>();


    public FlowDefinitionDTO(FlowDefinition flow){
        name = flow.getName();
        description = flow.getDescription();
        readOnly = flow.isReadOnly();
        steps = converStepsToDTO(flow.getFlowSteps());
        freeInputFinalNameToDD = castToDDDeclerationImpl(flow.getFreeInputFinalNameToDD());
        formalFinalOutPutNameToDD = castToDDDeclerationImpl(flow.getFormalOutput());
        initialValues = getInitialsValues(flow);
        
    }
    private Map<String, Object> getInitialsValues(FlowDefinition flow){
        Map<String, Object> initValues = new HashMap<>();
        for (Map.Entry<String, Object> initVals : flow.getInitialValues().entrySet()) {
            String key = initVals.getKey();
            Object value = initVals.getValue();
            if(flow.getFreeInputFinalNameToDD().get(key).UIPresent() == UIDDPresent.SQL_FILTER){
                SqlFilter filter = (SqlFilter)value;
                SqlFilterDTO filterDTO = new SqlFilterDTO();
                for (String filter_key : filter.getKeys()) {
                    filterDTO.addKey(filter_key,filter.getOperation(filter_key) != null ? filter.getOperation(filter_key).toString() : null ,filter.getValue(filter_key));
                }
                initialValueForSqlFilter.put(key,filterDTO);
            }
            initValues.put(key,value);
        }
        return initValues;
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
    public Map<String, SqlFilterDTO> getInitialValueForSqlFilter() {
        return initialValueForSqlFilter;
    }
}
