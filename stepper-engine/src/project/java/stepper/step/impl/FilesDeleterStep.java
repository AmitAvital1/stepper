package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.file.FileData;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.dd.impl.mapping.MappingData;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesDeleterStep extends AbstractStepDefinition {

    public FilesDeleterStep() {
        super("Files Deleter", false);

        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to delete", DataDefinitionRegistry.LIST, UIDDPresent.NA));

        addOutput(new DataDefinitionDeclarationImpl("DELETED_LIST", DataNecessity.NA, "Files list", DataDefinitionRegistry.LIST, UIDDPresent.NA));
        addOutput(new DataDefinitionDeclarationImpl("DELETION_STATS", DataNecessity.NA, "Deletion summary results", DataDefinitionRegistry.MAPPING, UIDDPresent.NA));
    }

    public StepResult invoke(StepExecutionContext context) throws NoStepInput {

        List<FileData> filesList = context.getDataValue("FILES_LIST", ListData.class).getList();
        ListData<FileData> listOfDeletedFiles = new ListData<>();
        List<String> listOfFailedDeletedFiles = new ArrayList<>();
        int numOfDeletedFiles = 0, numOfFailedDeletedFiles = 0;
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res = StepResult.SUCCESS;

        logs.addLogLine("About to start delete " + filesList.size() + " files");

        if (filesList.size() == 0) {//Do not have files to delete
            context.addStepSummaryLine("Finish with no files in the folder to delete");
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
                    listOfDeletedFiles.addData(fileData);
                    numOfDeletedFiles++;
                } else { //The file does not exist
                    res = StepResult.WARNING;
                    logs.addLogLine("Failed to delete file " + filePath + " because not exist");
                    numOfFailedDeletedFiles++;
                    listOfFailedDeletedFiles.add(filePath);
                }
            }
            if(numOfDeletedFiles == 0)
            {
                res = StepResult.FAILURE;
                context.addStepSummaryLine("The step failure due that none files are deleted");
                logs.addLogLine("FAILURE: Problem with deleting all the files");
            }
            else if (res == StepResult.WARNING) {
                context.addStepSummaryLine("The delete request have finish with delete problem on some files");
                logs.addLogLine("WARNING: The delete request have finish with delete problem on some files");
            }
            else context.addStepSummaryLine("The step finish with deleting the files");
        }

        context.storeDataValue("DELETED_LIST", listOfDeletedFiles);
        context.storeDataValue("DELETION_STATS", new MappingData<Integer, Integer>(numOfDeletedFiles, numOfFailedDeletedFiles));
        context.addStepLog(logs);
        return res;
    }
}
