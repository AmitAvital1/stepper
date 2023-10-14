package project.java.stepper.step.impl;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.json.JsonData;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonValueModifierStep extends AbstractStepDefinition {
    public JsonValueModifierStep() {
        super("Json Value Modifier", true);

        addInput(new DataDefinitionDeclarationImpl("JSON", DataNecessity.MANDATORY, "Json source", DataDefinitionRegistry.JSON, UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("JSON_PATH", DataNecessity.MANDATORY, "Data", DataDefinitionRegistry.STRING, UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("VALUE", DataNecessity.MANDATORY, "New value to change", DataDefinitionRegistry.STRING, UIDDPresent.NA));

        addOutput(new DataDefinitionDeclarationImpl("NEW_JSON_STRING", DataNecessity.NA, "New Json", DataDefinitionRegistry.STRING, UIDDPresent.NA));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) throws NoStepInput {
        JsonData json = context.getDataValue("JSON", JsonData.class);
        String json_path = context.getDataValue("JSON_PATH", String.class);
        String newValue = context.getDataValue("VALUE", String.class);
        List<String> json_paths = Arrays.asList(json_path.split("\\|"));
        String jsonString = json.toString();

        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res;
        logs.addLogLine("About change data from the Json");
        try {
            // Parse the JSON string
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
            logs.addLogLine("About change data from the Json");
            for(String jsonPath : json_paths){
                if(jsonPath.length() == 0)
                    continue;
                // Extract the data using JsonPath
                Object resultVal = JsonPath.read(document, jsonPath);
                jsonString = JsonPath.parse(jsonString).set(jsonPath,newValue).jsonString();
                if(resultVal.toString().length() == 0)
                    logs.addLogLine("No value found for json path " + jsonPath);
                else
                    logs.addLogLine("Data value changed from " + resultVal + " To " + newValue);
            }
            context.storeDataValue("NEW_JSON_STRING", jsonString);
            context.addStepSummaryLine("Data successfully changed");
            res = StepResult.SUCCESS;
        } catch (InvalidPathException e) {
            context.addStepSummaryLine("Failure cause Invalid JsonPath expression: " + json_path);
            res = StepResult.FAILURE;
        }
        context.addStepLog(logs);
        return res;
    }
}
