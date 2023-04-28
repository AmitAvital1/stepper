package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.file.FileData;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.dd.impl.mapping.MappingData;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FilesRenamerStep extends AbstractStepDefinition {

    public FilesRenamerStep() {
        super("Files Renamer", false);

        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to rename", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("PREFIX", DataNecessity.OPTIONAL, "Add this prefix", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("SUFFIX", DataNecessity.OPTIONAL, "Append this suffix", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("RENAME_RESULT", DataNecessity.NA, "Rename operation summary", DataDefinitionRegistry.RELATION));
    }

    public StepResult invoke(StepExecutionContext context) {

        List<FileData> filesList = context.getDataValue("FILES_LIST", ListData.class).getList();
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        RelationData renameResult = new RelationData();
        Integer columnCounter = 1;
        StepResult res = StepResult.SUCCESS;
        List<String> filesFailedList = new ArrayList<>();

        if (filesList.size() == 0) {//Do not have files to delete
            context.addStepSummaryLine("no files to change");
            logs.addLogLine("no files to change");
        }
        else {
            Optional<String> maybePrefix = Optional.ofNullable(context.getDataValue("PREFIX", String.class));
            Optional<String> maybeSuffix = Optional.ofNullable(context.getDataValue("SUFFIX", String.class));

            logs.addLogLine("About to start rename " + filesList.size() + " files. " + (maybePrefix.isPresent() ? "Adding prefix: " + maybePrefix.get() + ";" : "") + (maybeSuffix.isPresent() ? "Adding suffix: " + maybeSuffix.get() : ""));

            for (FileData fileData : filesList) {
                String fileName = new String(fileData.getFileName());
                String filePath = new String(fileData.getFilePath());
                int extensionIndex = fileName.lastIndexOf('.');
                String nameWithoutExtension = fileName.substring(0, extensionIndex);
                String extension = fileName.substring(extensionIndex);
                String newName = maybePrefix.orElse("") + nameWithoutExtension + maybeSuffix.orElse("") + extension;
                File oldFile = new File(fileData.getFilePath());
                File newFile = new File(oldFile.getParentFile() + "\\" + newName);
                if (oldFile.renameTo(newFile)) {
                    renameResult.addData(columnCounter.toString(), columnCounter.toString());
                    renameResult.addData(columnCounter.toString(), oldFile.getName());
                    renameResult.addData(columnCounter.toString(), newFile.getName());
                    columnCounter++;
                } else {
                    filesFailedList.add(oldFile.getName());
                    logs.addLogLine("Problem renaming file " + oldFile.getName());
                    res = StepResult.WARNING;
                }
            }
            if(res == StepResult.SUCCESS)
                context.addStepSummaryLine("Success with renaming the files");
        }
        if(res == StepResult.WARNING)
            context.addStepSummaryLine("problem on renaming files: " + filesFailedList.toString());

        context.storeDataValue("RENAME_RESULT", renameResult);
        context.addStepLog(logs);
        return res;
    }
}
