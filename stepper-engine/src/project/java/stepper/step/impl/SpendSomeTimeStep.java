package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

public class SpendSomeTimeStep extends AbstractStepDefinition {
    public SpendSomeTimeStep() {
        super("Spend Some Time", true);

        addInput(new DataDefinitionDeclarationImpl("TIME_TO_SPEND", DataNecessity.MANDATORY, "Total sleeping time (sec)", DataDefinitionRegistry.INTEGER, UIDDPresent.NA));
    }
    public StepResult invoke(StepExecutionContext context) throws NoStepInput {
        int seconds = context.getDataValue("TIME_TO_SPEND", Integer.class);
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        if (seconds <= 0) {
            context.addStepSummaryLine("Failure:there is non natural number of seconds in the input");
            logs.addLogLine("STEP FAILURE:non natural number of seconds");
            context.addStepLog(logs);
            return StepResult.FAILURE;
        }
        else {
            try {
                logs.addLogLine("About to sleep for " + seconds + " seconds…");
                Thread.sleep(seconds * 1000);
                logs.addLogLine("Done sleeping…");
                context.addStepSummaryLine("Finish with sleep of " + seconds + " seconds");
                context.addStepLog(logs);
                return StepResult.SUCCESS;
            }catch (InterruptedException e) {
                context.addStepSummaryLine("Failure:Error occured");
                logs.addLogLine("STEP FAILURE:Error occured");
                context.addStepLog(logs);
                return StepResult.FAILURE;
            }
        }

    }
}
