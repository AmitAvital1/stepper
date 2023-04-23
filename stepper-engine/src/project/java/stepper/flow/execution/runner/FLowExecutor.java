package project.java.stepper.flow.execution.runner;

import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.FlowExecutionResult;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.StepExecutionContextImpl;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.StepResult;

import java.time.Duration;
import java.time.Instant;

public class FLowExecutor {

    public void executeFlow(FlowExecution flowExecution) {
        Instant startTime = Instant.now();
        System.out.println("Starting execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]");

        StepExecutionContext context = new StepExecutionContextImpl(); // actual object goes here...
        flowExecution.getFlowDefinition().getStartersFreeInputForContext().forEach((key,val) -> context.storeDataValue(key,val));

        // start actual execution
        for (int i = 0; i < flowExecution.getFlowDefinition().getFlowSteps().size(); i++) {
            StepUsageDeclaration stepUsageDeclaration = flowExecution.getFlowDefinition().getFlowSteps().get(i);
            context.updateCurrentWorkingStep(stepUsageDeclaration);
            System.out.println("Starting to execute step: " + stepUsageDeclaration.getFinalStepName());
            StepResult stepResult = stepUsageDeclaration.getStepDefinition().invoke(context);
            System.out.println("Done executing step: " + stepUsageDeclaration.getFinalStepName() + ". Result: " + stepResult);
            if(!stepUsageDeclaration.skipIfFail() && stepResult == StepResult.FAILURE){
                context.addStepSummaryLine("The step failed before finish: " + stepUsageDeclaration.getFinalStepName() + " FAILED");
                StepLogs log = new StepLogs(stepUsageDeclaration.getFinalStepName());
                log.addLogLine("FAILED and stopped the flow");
                context.addStepLog(log);
                flowExecution.setFlowExecutionResult(FlowExecutionResult.FAILURE);
                break;
            }
            flowExecution.setFlowExecutionResult(FlowExecutionResult.SUCCESS);
        }

        Instant endTime = Instant.now();
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("End execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]. Status: " + flowExecution.getFlowExecutionResult());
    }
}
