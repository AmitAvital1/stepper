package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.file.FileData;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.dd.impl.mapping.MappingData;
import project.java.stepper.dd.impl.mapping.MappingDataDefinition;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FilesDeleterStep extends AbstractStepDefinition {

    public FilesDeleterStep() {
        super("File Deleter", false);

        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to delete", DataDefinitionRegistry.LIST));

        addOutput(new DataDefinitionDeclarationImpl("DELETED_LIST", DataNecessity.NA, "Files list", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("DELETION_STATS", DataNecessity.NA, "Deletion summary results", DataDefinitionRegistry.MAPPING));
    }

    public StepResult invoke(StepExecutionContext context) {

        List<FileData> filesList = context.getDataValue("FILES_LIST", ListData.class).getList();
        List<String> listOfFailedDeletedFiles = new ArrayList<>();
        int numOfDeletedFiles = 0, numOfFailedDeletedFiles = 0;
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res = StepResult.SUCCESS;

        logs.addLogLine("About to start delete " + filesList.size() + " files");

        if (filesList.size() == 0) {//Do not have files to delete
            //Summary line
        }
        else {
            for (FileData fileData : filesList) {
                String filePath = fileData.getFilePath();
                File file = new File(filePath);
                if (file.exists()) {
                    file.delete();
                    if (file.exists()) {//check if the file deleted
                        res = StepResult.WARNING;
                        logs.addLogLine("Failed to delete file " + filePath);
                        numOfFailedDeletedFiles++;
                        listOfFailedDeletedFiles.add(filePath);
                        continue;
                    }
                    numOfDeletedFiles++;
                } else { //The file does not exist
                    res = StepResult.WARNING;
                    logs.addLogLine("Failed to delete file " + filePath);
                    numOfFailedDeletedFiles++;
                    listOfFailedDeletedFiles.add(filePath);
                }
            }
            if (res == StepResult.WARNING) {
                logs.addLogLine("WARNING: The delete request have finish with delete problem on some files");
            }
        }
        context.storeDataValue("DELETED_LIST", filesList);
        context.storeDataValue("DELETION_STATS", new MappingData<Integer, Integer>(numOfDeletedFiles, numOfFailedDeletedFiles));
        context.addStepLog(logs);
        return res;
    }
}
