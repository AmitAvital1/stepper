package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
        try {
            List<Path> fileList = Files.walk(new File(filePath).toPath())
                    .filter(Files::isRegularFile)
                    .map(Path::toAbsolutePath)
                    .collect(Collectors.toList());

        }
        catch (IOException e)
        {

            return StepResult.FAILURE;
        }
        return null;
    }
}
