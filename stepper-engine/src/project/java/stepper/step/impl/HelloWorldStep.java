package project.java.stepper.step.impl;

import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.StepResult;

public class HelloWorldStep extends AbstractStepDefinition {

    public HelloWorldStep() {
        super("Hello World", true);

        // no inputs

        // no outputs
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        System.out.println("Hello world !");
        return StepResult.SUCCESS;
    }
}
