package project.java.stepper.step.impl;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.minidev.json.JSONArray;
import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.json.JsonData;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

public class JsonDataExtractor extends AbstractStepDefinition {
    public JsonDataExtractor() {
        super("Json Data Extractor", true);

        addInput(new DataDefinitionDeclarationImpl("JSON", DataNecessity.MANDATORY, "Json source", DataDefinitionRegistry.JSON, UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("JSON_PATH", DataNecessity.MANDATORY, "Data", DataDefinitionRegistry.STRING, UIDDPresent.NA));

        addOutput(new DataDefinitionDeclarationImpl("VALUE", DataNecessity.NA, "Data value", DataDefinitionRegistry.STRING, UIDDPresent.NA));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) throws NoStepInput {
        JsonData json = context.getDataValue("JSON", JsonData.class);
        String json_path = context.getDataValue("JSON_PATH", String.class);

        String jsonString = json.toString();

        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res;

        try {
            // Parse the JSON string
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);

            // Extract the data using JsonPath
            JSONArray extractedData = JsonPath.read(document, json_path);

            // Append the data with commas
            StringBuilder result = new StringBuilder();
            logs.addLogLine("About extract data from the Json");
            for (int i = 0; i < extractedData.size(); i++) {
                result.append(extractedData.get(i));
                if (i < extractedData.size() - 1) {
                    result.append(", ");
                }
            }
            if(extractedData.size() == 0)
                logs.addLogLine("No value found for json path " + json_path);
            else
                logs.addLogLine("Extracting data <json path>. Value: " + result.toString());

            context.storeDataValue("VALUE", result.toString());
            context.addStepSummaryLine("Data successfully extracted");
            res = StepResult.SUCCESS;
        } catch (PathNotFoundException e) {
            context.addStepSummaryLine("Failure cause Invalid JsonPath expression: " + json_path);
            res = StepResult.FAILURE;
        }
        context.addStepLog(logs);
        return res;
    }
}
