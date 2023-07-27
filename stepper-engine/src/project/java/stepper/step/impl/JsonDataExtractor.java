package project.java.stepper.step.impl;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.minidev.json.JSONArray;
import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.json.JsonData;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        List<String> json_paths = Arrays.asList(json_path.split("\\|"));
        String jsonString = json.toString();

        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res;
        StringBuilder result = new StringBuilder();

        try {
            // Parse the JSON string
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
            logs.addLogLine("About extract data from the Json");
            for(String jsonPath : json_paths){
                if(jsonPath.length() == 0)
                    continue;
                // Extract the data using JsonPath
                Object resultVal = JsonPath.read(document, jsonPath);

                List<Object> extractedData = new ArrayList<>();

                if (resultVal instanceof List<?>) {
                    // The result is a list of values
                    extractedData = (List<Object>) resultVal;
                } else {
                    // The result is a single value
                    extractedData.add(resultVal);
                }
                // Append the data with commas
                StringBuilder tmp = new StringBuilder();
                for (int i = 0; i < extractedData.size(); i++) {
                    tmp.append(extractedData.get(i).toString());
                    if (i < extractedData.size() - 1) {
                        tmp.append(", ");
                    }
                }
                if(extractedData.size() == 0)
                    logs.addLogLine("No value found for json path " + jsonPath);
                else {
                    logs.addLogLine("Extracting data " + jsonPath + ". Value: " + tmp.toString());
                    if(result.toString().length() > 0)
                        result.append(", " + tmp);
                    else
                        result.append(tmp);
                }
            }
            context.storeDataValue("VALUE", result.toString());
            context.addStepSummaryLine("Data successfully extracted");
            res = StepResult.SUCCESS;
        } catch (InvalidPathException e) {
            context.addStepSummaryLine("Failure cause Invalid JsonPath expression: " + json_path);
            res = StepResult.FAILURE;
        }
        context.addStepLog(logs);
        return res;
    }
}
