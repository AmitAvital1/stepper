package project.java.stepper.step.impl;

import com.google.gson.JsonSyntaxException;
import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.json.JsonData;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

public class ToJsonStep extends AbstractStepDefinition {
    public ToJsonStep() {
        super("To Json", true);

        addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY, "Content", DataDefinitionRegistry.STRING, UIDDPresent.NA));

        addOutput(new DataDefinitionDeclarationImpl("JSON", DataNecessity.NA, "Json representation", DataDefinitionRegistry.JSON, UIDDPresent.NA));
    }
    @Override
    public StepResult invoke(StepExecutionContext context) throws NoStepInput {
        String content = context.getDataValue("CONTENT", String.class);
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res = StepResult.SUCCESS;

        try{
            JsonData json = new JsonData(content);
            context.storeDataValue("JSON", json);
            logs.addLogLine("Content is JSON string. Converting it to json…");
            context.addStepSummaryLine("Content has converted to json");
        }catch (JsonSyntaxException e){
            logs.addLogLine("Content is not a valid JSON representation");
            context.addStepSummaryLine("Step Failed cause Content is not a valid JSON representation");
            res = StepResult.FAILURE;
        }
        context.addStepLog(logs);
        return res;
    }

}
