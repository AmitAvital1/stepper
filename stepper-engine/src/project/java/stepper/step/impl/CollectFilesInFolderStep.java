package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.file.FileData;
import project.java.stepper.dd.impl.list.ListData;
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

public class CollectFilesInFolderStep extends AbstractStepDefinition {
    public CollectFilesInFolderStep() {
        super("Collect Files In Folder", true);

        addInput(new DataDefinitionDeclarationImpl("FOLDER_NAME", DataNecessity.MANDATORY, "Folder name to scan", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.OPTIONAL, "Filter only these files", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.NA, "Files list", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", DataNecessity.NA, "Total files found", DataDefinitionRegistry.INTEGER));
    }


    @Override
    public StepResult invoke(StepExecutionContext context) {

        String filePath = context.getDataValue("FOLDER_NAME", String.class);
        Optional<String> maybeFilter = Optional.ofNullable(context.getDataValue("FILTER", String.class));

        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        String filter = maybeFilter.orElse(""); // "" says not filter

        try {
            logs.addLogLine("Reading folder " + filePath + (filter != "" ? (" content with filter" + filter) : ""));
            List<Path> fileList = Files.walk(new File(filePath).toPath())
                    .filter(Files::isRegularFile)
                    .filter((path) -> path.toString().endsWith(filter))
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toList());
            ListData<FileData> filesList = new ListData();
            for (Path path : fileList) {
                FileData f = new FileData(filePath.toString());
                filesList.addData(f);
            }
            context.storeDataValue("FILES_LIST", filesList);
            context.storeDataValue("TOTAL_FOUND", filesList.size());
            logs.addLogLine("Found " +  filesList.size() + "files in folder matching the filter");
            context.addStepLog(logs);
            if(filesList.size() == 0)
            {
                //System.out.println("WARNING:THERE ARE NO FILES EXISST IN THE FOLDER PATH");
                logs.addLogLine("STEP WARNING:There are no files exist in the folder path");
                return StepResult.WARNING;
            }
            else
                return StepResult.SUCCESS;
        }
        catch (IOException e)
        {
           // System.out.println("FAILUR:THERE IS NO FOLDER OR WRONG PATH FOLDER");
            logs.addLogLine("STEP FAILUR: There is no folder or wrong path folder");
            return StepResult.FAILURE;
        }
    }
}
