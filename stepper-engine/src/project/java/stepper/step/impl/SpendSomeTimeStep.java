package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;

public class SpendSomeTimeStep extends AbstractStepDefinition {
    public SpendSomeTimeStep() {
        super("Spend Some Time", true);

        addInput(new DataDefinitionDeclarationImpl("TIME_TO_SPEND", DataNecessity.MANDATORY, "Total sleeping time (sec)", DataDefinitionRegistry.STRING));
    }
    public StepResult invoke(StepExecutionContext context) {
        int seconds = Integer.parseInt(context.getDataValue("TIME_TO_SPEND", String.class));
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        if (seconds <= 0) {
            logs.addLogLine("STEP FAILURE:non natural number of seconds");
            return StepResult.FAILURE;
        }
        else {
            try {
                logs.addLogLine("About to sleep for " + seconds + " seconds…");
                Thread.sleep(seconds * 1000);
                logs.addLogLine("Done sleeping…");
                return StepResult.SUCCESS;
            }catch (InterruptedException e) {
                logs.addLogLine("STEP FAILURE:Error occured");
                return StepResult.FAILURE;
            }
        }

    }
}
