package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.file.FileData;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;

public class HelloWorldStep extends AbstractStepDefinition {

    public HelloWorldStep() {
        super("Hello World", true);

        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Filter", DataDefinitionRegistry.LIST));

        // no outputs
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        ListData<FileData> filePath = context.getDataValue("FILES_LIST", ListData.class);
        System.out.println("Hello world !");
        return StepResult.SUCCESS;
    }
}
