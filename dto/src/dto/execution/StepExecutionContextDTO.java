package dto.execution;

import dto.StepUsageDeclarationImplDTO;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.StepExecutionContextImpl;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.StepResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class StepExecutionContextDTO {
    private List<StepLogs> flowLogs;
    private List<String> stepSummaryLine;
    private final List<FlowExecutionDTO.flowOutputsDataDTO> outputsData;
    private List<stepDataDTO> flowStepsData;

    public StepExecutionContextDTO(StepExecutionContext context) {
        flowLogs = context.getFlowLogs();
        stepSummaryLine = context.getStepSummaryLine();
        outputsData = convertOutPutsData(context.getFlowOutputsData());
        flowStepsData = convertFlowStepData(context.getRegFlowStepsData());
    }

    private List<stepDataDTO> convertFlowStepData(List<StepExecutionContextImpl.stepData> regFlowStepsData) {
        List<stepDataDTO> res = new ArrayList<>();
        for(StepExecutionContextImpl.stepData data : regFlowStepsData){
            res.add(new stepDataDTO(data));
        }
        return res;
    }

    private List<FlowExecutionDTO.flowOutputsDataDTO> convertOutPutsData(List<FlowExecution.flowOutputsData> flowOutputsData) {
        List<FlowExecutionDTO.flowOutputsDataDTO> res = new ArrayList<>();
        for(FlowExecution.flowOutputsData data : flowOutputsData){
            res.add(new FlowExecutionDTO.flowOutputsDataDTO(data));
        }
        return res;
    }
    public class stepDataDTO {
        public StepUsageDeclarationImplDTO step;
        public String stepSummaryLine;
        public StepLogs logs;
        public Duration time;
        public StepResult result;

        public stepDataDTO(StepExecutionContextImpl.stepData stepData){
            this.step = new StepUsageDeclarationImplDTO(stepData.step);
            this.result = stepData.result;
            this.stepSummaryLine = stepData.stepSummaryLine;
            this.logs = stepData.logs;
            this.time = stepData.time;
        }
    }
    public StepExecutionContextDTO.stepDataDTO getStepData(StepUsageDeclarationImplDTO step){
        for(StepExecutionContextDTO.stepDataDTO data : flowStepsData){
            if(data.step.equals(step))
                return data;
        }
        return null;
    }
}
