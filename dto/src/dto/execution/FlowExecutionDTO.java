package dto.execution;

import dto.DataDefinitionDeclarationDTO;
import dto.FlowDefinitionDTO;
import dto.StepUsageDeclarationImplDTO;
import javafx.beans.property.IntegerProperty;
import project.java.stepper.dd.impl.file.FileData;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.FlowExecutionResult;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import project.java.stepper.step.api.DataNecessity;

import java.util.*;
import java.util.stream.Collectors;

public class FlowExecutionDTO {
    private final String uniqueId;
    private final FlowDefinitionDTO flowDefinition;
    private final StepExecutionContextDTO flowContexts;
    private final long totalTime;
    private final String startedTime;
    private final FlowExecutionResult flowExecutionResult;
    private final Map<String, Object> startersFreeInputForContext;
    private final List<flowOutputsDataDTO> outputsStepData;
    private Integer stepFinished = new Integer(0);
    private Map<String, String> formalOutputNameToClass = new HashMap<>();
    private String usernameExe;

    private List<String> flowsContinuation;

    private final List<String> AllFreeInputsWithDataToPrintList;
    private final List<String> AllOutPutsWithDataToPrintList;

    public FlowExecutionDTO(FlowExecution flowExecution,String username){
        this(flowExecution);
        usernameExe = username;
    }

    public FlowExecutionDTO(FlowExecution flowExecution,List<String> contPermissions,String username){
        this(flowExecution,contPermissions);
        usernameExe = username;
    }

    public FlowExecutionDTO(FlowExecution flowExecution){
        this.uniqueId = flowExecution.getUniqueId();
        this.flowDefinition = new FlowDefinitionDTO(flowExecution.getFlowDefinition());
        this.flowContexts = new StepExecutionContextDTO(flowExecution.getFlowContexts());
        this.totalTime = flowExecution.getDuration();
        this.startedTime = flowExecution.getStartedTime();
        this.flowExecutionResult = flowExecution.getFlowExecutionResult();
        this.startersFreeInputForContext = flowExecution.getStartersFreeInputForContext();
        this.outputsStepData = convertoutputsStepData(flowExecution.getOutputsStepData());
        this.stepFinished = flowExecution.getStepFinishedProperty().get();
        this.flowsContinuation = flowExecution.getFlowDefinition().getFlowsContinuations().stream().map(continuationFlowDetails -> continuationFlowDetails.getTargetFlow().getName()).collect(Collectors.toList());
        this.AllFreeInputsWithDataToPrintList = flowExecution.getAllFreeInputsWithDataToPrintList();
        this.AllOutPutsWithDataToPrintList = flowExecution.getAllOutPutsWithDataToPrintList();

    }
    public FlowExecutionDTO(FlowExecution flowExecution,List<String> contPermissions){
        this.uniqueId = flowExecution.getUniqueId();
        this.flowDefinition = new FlowDefinitionDTO(flowExecution.getFlowDefinition());
        this.flowContexts = new StepExecutionContextDTO(flowExecution.getFlowContexts());
        this.totalTime = flowExecution.getDuration();
        this.startedTime = flowExecution.getStartedTime();
        this.flowExecutionResult = flowExecution.getFlowExecutionResult();
        this.startersFreeInputForContext = flowExecution.getStartersFreeInputForContext();
        this.outputsStepData = convertoutputsStepData(flowExecution.getOutputsStepData());
        this.stepFinished = flowExecution.getStepFinishedProperty().get();
        this.AllFreeInputsWithDataToPrintList = flowExecution.getAllFreeInputsWithDataToPrintList();
        this.AllOutPutsWithDataToPrintList = flowExecution.getAllOutPutsWithDataToPrintList();

        List<String> allCont =  flowExecution.getFlowDefinition().getFlowsContinuations().stream().map(continuationFlowDetails -> continuationFlowDetails.getTargetFlow().getName()).collect(Collectors.toList());
        List<String> userCont = new ArrayList<>();
        allCont.stream().forEach(cont -> {
            if(contPermissions.contains(cont))
                userCont.add(cont);
        });
        flowsContinuation = userCont;
    }
    private List<flowOutputsDataDTO> convertoutputsStepData(List<FlowExecution.flowOutputsData> outputsStepData) {
        List<flowOutputsDataDTO> res = new ArrayList<>();
        for(FlowExecution.flowOutputsData data : outputsStepData){
            res.add(new flowOutputsDataDTO(data));
        }
        return res;
    }


    public static class flowOutputsDataDTO{
        private final String finalName;
        private StepUsageDeclarationImplDTO outputStep;
        private final DataDefinitionDeclarationDTO outputDD;
        private Object data;
        boolean createdFromFlow = true;
        private RelationData itsRelation;

        public flowOutputsDataDTO(FlowExecution.flowOutputsData data){
            this.finalName = data.getFinalName();
            this.outputStep = new StepUsageDeclarationImplDTO(data.getOutputStep());
            this.outputDD = new DataDefinitionDeclarationDTO(data.getOutputDD());
            if(data.getCreatedFromFlow()) {
                if (data.getOutputDD().dataDefinition().getType() == ListData.class) {
                    if (((ListData) data.getData()).getList().size() > 0) {
                        if (((ListData) data.getData()).getList().get(0).getClass() == FileData.class) {
                            List<String> arr = new ArrayList<>();
                            for (Object var : ((ListData) data.getData()).getList())
                                arr.add(var.toString());
                            this.data = arr;
                        } else
                            this.data = ((ListData) data.getData()).getList().toString();
                    } else
                        this.data = ((ListData) data.getData()).getList();
                } else if (data.getOutputDD().dataDefinition().getType() == RelationData.class) {
                    itsRelation = ((RelationData) data.getData());
                    this.data = data.getData().toString();
                }
                else
                    this.data = data.getData().toString();
            }
            else {
                this.data = data.getData().toString();
            }

            createdFromFlow = data.getCreatedFromFlow();
        }
        public boolean isOutputExist(String finalName,DataDefinitionDeclarationDTO dd){
            if(this.finalName.equals(finalName) && this.outputDD.dataDefinition().getName().equals(dd.dataDefinition().getName()))
                return true;
            else return false;
        }
        public boolean getCreatedFromFlow(){return createdFromFlow;}

        public String getFinalName() {
            return finalName;
        }

        public StepUsageDeclarationImplDTO getOutputStep() {
            return outputStep;
        }

        public DataDefinitionDeclarationDTO getOutputDD() {
            return outputDD;
        }

        public Object getData() {
            return data;
        }
    }


    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }
    public String getUniqueId() {
        return uniqueId;
    }
    public long getDuration(){
        return totalTime;
    }
    public FlowDefinitionDTO getFlowDefinition() {
        return flowDefinition;
    }
    public Integer getStepFinishedProperty() {
        return stepFinished;
    }

    public StepExecutionContextDTO getFlowContexts(){return  flowContexts;}

    public Map<String,Object> getFormalOutPutsData(){
        Map<String,Object> formalOutputToData = new HashMap<>();
        for (Map.Entry<String, DataDefinitionDeclarationDTO> entry : flowDefinition.getFormalOutput().entrySet()) {
            String newVal = entry.getKey() + "-" + entry.getValue().userString();
            Object key = outputsStepData.stream().filter(output -> output.finalName.equals(entry.getKey())).findFirst().get().data;
            formalOutputToData.put(newVal,key);
            if(outputsStepData.stream().filter(output -> output.finalName.equals(entry.getKey())).findFirst().get().createdFromFlow)
                formalOutputNameToClass.put(newVal,entry.getValue().dataDefinition().getType());
            else
                formalOutputNameToClass.put(newVal,"String");

            if(formalOutputNameToClass.get(newVal).equals("RelationData"))
                formalOutputToData.put(newVal,outputsStepData.stream().filter(output -> output.finalName.equals(entry.getKey())).findFirst().get().itsRelation);
        }
        return formalOutputToData;
    }
    public Map<String, Object> getStartersFreeInputForContext() {
        return startersFreeInputForContext;
    }
    public String getTypeOfFormalOutPut(String output){
        return formalOutputNameToClass.get(output);
    }
    public List<String> getFlowsContinuation() {
        return flowsContinuation;
    }
    public void setFlowsContinuation(List<String> flowsContinuation) {
        this.flowsContinuation = flowsContinuation;
    }
    public String getStartedTime() {
        return startedTime;
    }
    public List<String> getAllFreeInputsWithDataToPrintList(){
        return AllFreeInputsWithDataToPrintList;
    }
    public List<FlowExecutionDTO.flowOutputsDataDTO> getOutputsStepData(){return outputsStepData; }
    public List<String> getAllOutPutsWithDataToPrintList() {
        return AllOutPutsWithDataToPrintList;
    }
    public String getUsernameExe() {
        return usernameExe;
    }

    public void setUsernameExe(String usernameExe) {
        this.usernameExe = usernameExe;
    }
}
