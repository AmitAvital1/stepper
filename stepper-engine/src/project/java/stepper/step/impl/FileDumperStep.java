package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileDumperStep extends AbstractStepDefinition {
    public FileDumperStep() {
        super("File Dumper", true);

        addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY, "Content", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILE_NAME", DataNecessity.MANDATORY, "Target file path", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "File Creation Result", DataDefinitionRegistry.STRING));
    }
    @Override
    public StepResult invoke(StepExecutionContext context) {
        String content = context.getDataValue("CONTENT", String.class);
        String fileNameWithPath = context.getDataValue("FILE_NAME", String.class);
        String result = "SUCCESS";
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res = StepResult.SUCCESS;

        File file = new File(fileNameWithPath);
        String fileName = file.getName();
        if (file.exists()) {
            res = StepResult.FAILURE;
            logs.addLogLine(fileName + " is already exist");
            context.addStepSummaryLine(fileName + " is already exist. Cannot override him");
            result = "Failure: " + fileName + " is already exist. Cannot override him";
        }
        if(content.length() == 0) {
            res = StepResult.WARNING;
            logs.addLogLine("No content to write to " + fileName);
            context.addStepSummaryLine("No content to write to " + fileName);
            result = "Warning: No content to write to the file";
        }
            try {
                logs.addLogLine("About to create file named " + fileName);
                if (!file.createNewFile()) {
                    res = StepResult.FAILURE;
                    logs.addLogLine("Failure: An error occurred while creating the file");
                    context.addStepSummaryLine("Failure: An error occurred while creating the file");
                    result = "Failure: An error occurred while creating the file";
                }
            }
            catch (IOException e) {
                res = StepResult.FAILURE;
                logs.addLogLine("Failure: An error occurred while creating the file");
                context.addStepSummaryLine("Failure: An error occurred while creating the file");
                result = "Failure: An error occurred while creating the file";
            }
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(content);
                writer.close();
            } catch (IOException e) {
                res = StepResult.FAILURE;
                logs.addLogLine("WARNING: An error occurred while writing to the file.");
                context.addStepSummaryLine("WARNING: An error occurred while writing to the file.");
                result = "WARNING: An error occurred while writing to the file.";
            }


        context.storeDataValue("RESULT", result);
        context.addStepLog(logs);
        return res;
    }
}
