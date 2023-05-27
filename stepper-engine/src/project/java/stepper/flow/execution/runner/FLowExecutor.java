package project.java.stepper.flow.execution.runner;

import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.FlowExecutionResult;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.StepExecutionContextImpl;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.StepResult;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;

public class FLowExecutor implements Runnable {

    private FlowExecution flowExecution;
    private StepExecutionContext context;

    public FLowExecutor(FlowExecution flowExecution){
        this.flowExecution = flowExecution;
        context = new StepExecutionContextImpl(); // actual object goes here...
        flowExecution.setFlowContexts(context);
    }

    public void executeFlow(FlowExecution flowExecution) {
        boolean theStepFinishWithFailure = false;
        boolean stopTheFlow = false;
        Instant flowStartTime = Instant.now();
        LocalTime time = LocalTime.now();
        String formattedTime = time.getHour() + ":" + time.getMinute() + ":" + time.getSecond();
        flowExecution.setStartedTime(formattedTime);

        flowExecution.setFlowContexts(context);
        context.updateCurrentWorkingStep(null);
        flowExecution.getStartersFreeInputForContext().forEach((key,val) -> context.storeDataValue(key,val));

        // start actual execution
        for (int i = 0; i < flowExecution.getFlowDefinition().getFlowSteps().size() && !stopTheFlow; i++) {
            StepUsageDeclaration stepUsageDeclaration = flowExecution.getFlowDefinition().getFlowSteps().get(i);
            context.updateCurrentWorkingStep(stepUsageDeclaration);

            Instant stepStartTime = Instant.now();
            //System.out.println("Starting to execute step: " + stepUsageDeclaration.getFinalStepName());
            StepResult stepResult;
            try{
                stepResult = stepUsageDeclaration.getStepDefinition().invoke(context);
            }
            catch (NoStepInput e){
                stepResult = StepResult.FAILURE;
                StepLogs log = new StepLogs(stepUsageDeclaration.getFinalStepName());
                context.addStepSummaryLine("Failed:" + e.getMessage());
                log.addLogLine("FAILED " + e.getMessage());
                context.addStepLog(log);
            }
            Instant stepEndTime = Instant.now();
            Duration duration = Duration.between(stepStartTime, stepEndTime);
           // System.out.println("Done executing step: " + stepUsageDeclaration.getFinalStepName() + ". Result: " + stepResult);


            if(!stepUsageDeclaration.skipIfFail() && stepResult == StepResult.FAILURE){
                //context.addStepSummaryLine("The step failed before finish: " + stepUsageDeclaration.getFinalStepName() + " FAILED");
                StepLogs log = new StepLogs(stepUsageDeclaration.getFinalStepName());
                log.addLogLine("FAILED and stopped the flow");
                context.addStepLog(log);
                flowExecution.setFlowExecutionResult(FlowExecutionResult.FAILURE);
                theStepFinishWithFailure = true;
                stopTheFlow = true;
                flowExecution.addStepFinished(flowExecution.getFlowDefinition().getFlowSteps().size());

            }
            else if(stepResult == StepResult.FAILURE){
                flowExecution.setFlowExecutionResult(FlowExecutionResult.WARNING);
                theStepFinishWithFailure = true;
            }
            context.addStepData(stepUsageDeclaration,context.getLastStepSummaryLine(),context.getLastStepLogs(),duration,stepResult);//Add all step datas
            flowExecution.getFlowDefinition().addFlowRunStepStats(stepUsageDeclaration,duration);
            flowExecution.addStepFinished(1);
        }
        if(!theStepFinishWithFailure)
            flowExecution.setFlowExecutionResult(FlowExecutionResult.SUCCESS);

        Instant flowEndTime = Instant.now();
        Duration duration = Duration.between(flowStartTime, flowEndTime);
        flowExecution.setDuration(duration);
       // System.out.println("End execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]. Status: " + flowExecution.getFlowExecutionResult());

        //Inject all the data to the flow execution
        flowExecution.setAllDataValues(context.getDataValuesMap());
        flowExecution.addFlowOutputsData(context.getFlowOutputsData());
        flowExecution.getFlowDefinition().addFlowRunStats(duration);
    }

    @Override
    public void run() {
        executeFlow(flowExecution);
    }
}
