package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.file.FileData;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CollectFilesInFolderStep extends AbstractStepDefinition {
    public CollectFilesInFolderStep() {
        super("Collect Files In Folder", true);

        addInput(new DataDefinitionDeclarationImpl("FOLDER_NAME", DataNecessity.MANDATORY, "Folder name to scan", DataDefinitionRegistry.STRING,UIDDPresent.FOLDER_DIALOG));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.OPTIONAL, "Filter only these files", DataDefinitionRegistry.STRING,UIDDPresent.NA));

        addOutput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.NA, "Files list", DataDefinitionRegistry.LIST, UIDDPresent.NA));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", DataNecessity.NA, "Total files found", DataDefinitionRegistry.INTEGER,UIDDPresent.NA));
    }


    @Override
    public StepResult invoke(StepExecutionContext context) throws NoStepInput {

        String filePath = context.getDataValue("FOLDER_NAME", String.class);
        Optional<String> maybeFilter = Optional.ofNullable(context.getDataValue("FILTER", String.class));
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        String filter = maybeFilter.orElse(""); // "" says not filter

        ListData<FileData> filesList = new ListData();

        try {
            List<Path> fileList = Files.walk(new File(filePath).toPath())
                    .filter(Files::isRegularFile)
                    .filter((path) -> path.toString().endsWith(filter))
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toList());
            logs.addLogLine("Reading folder " + filePath + (filter != "" ? (" content with filter " + filter) : ""));
            for (Path path : fileList) {
                FileData f = new FileData(path.toString());
                filesList.addData(f);
            }
            context.storeDataValue("FILES_LIST", filesList);
            context.storeDataValue("TOTAL_FOUND", filesList.size());
            logs.addLogLine("Found " +  filesList.size() + " files in folder" + (filter != "" ? (" matching the filter") : ""));
            context.addStepLog(logs);
            if(filesList.size() == 0)
            {
                context.addStepSummaryLine("Step finish with no files to collect");
                logs.addLogLine("STEP WARNING:There are no files exist in the folder path");
                context.addStepLog(logs);
                return StepResult.WARNING;
            }
            else{
                context.addStepSummaryLine("Finish to collect all " + filesList.size() + " files");
                context.addStepLog(logs);
                return StepResult.SUCCESS;
            }
        }
        catch (IOException e)
        {
            context.addStepSummaryLine("Failure because there is no folder in the path");
            logs.addLogLine("STEP FAILURE: There is no folder or wrong path folder");
            context.addStepLog(logs);
            return StepResult.FAILURE;
        }
    }
}
