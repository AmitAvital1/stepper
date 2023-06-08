package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.file.FileData;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

import java.io.*;
import java.util.List;

public class FilesContentExtractorStep extends AbstractStepDefinition {
    public FilesContentExtractorStep() {
        super("Files Content Extractor", true);

        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to extract", DataDefinitionRegistry.LIST, UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("LINE", DataNecessity.MANDATORY, "Line number to extract", DataDefinitionRegistry.INTEGER, UIDDPresent.NA));

        addOutput(new DataDefinitionDeclarationImpl("DATA", DataNecessity.NA, "Data extraction", DataDefinitionRegistry.RELATION, UIDDPresent.NA));
    }
    @Override
    public StepResult invoke(StepExecutionContext context) throws NoStepInput {

        List<FileData> filesList = context.getDataValue("FILES_LIST", ListData.class).getList();
        Integer lineNumber = context.getDataValue("LINE", Integer.class);

        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        String[] colsName = {"No.", "File original name", "Extracted Content"};
        RelationData dataRelation = new RelationData(colsName);
        Integer columnCounter = 1;

        StepResult res = StepResult.SUCCESS;

        if (filesList.size() == 0) {//Do not have files to delete
            logs.addLogLine("no files to extract data");
            context.addStepSummaryLine("no files to extract data");
        }
        else {
            for (FileData fileData : filesList) {
                String fileName = new String(fileData.getFileName());
                String filePath = new String(fileData.getFilePath());
                File workingFile = new File(filePath);
                int extensionIndex = fileName.lastIndexOf('.');

                if(extensionIndex == -1)
                    extensionIndex = fileName.length();

                String nameWithoutExtension = fileName.substring(0, extensionIndex);
                String extension = fileName.substring(extensionIndex);

                logs.addLogLine("About to start work on file " + workingFile.getName());
                String resFile = "Not such line";

                if(!extension.equals(".txt")){
                    logs.addLogLine("Problem extracting line number " + lineNumber + " from file " + workingFile.getName());
                    resFile =  "Not txt file";
                }
                else if(!workingFile.exists()){
                    resFile = "File not found";
                }
                else {
                    try{
                        BufferedReader reader = new BufferedReader(new FileReader(filePath));
                        String line = null;
                        int currentLine = 1;
                        while ((line = reader.readLine()) != null) {
                            if (currentLine == lineNumber) {
                                resFile = line;
                                reader.close();
                                currentLine++;
                                break;
                            }
                            currentLine++;
                        }
                        if (lineNumber + 1 > currentLine) {
                            resFile = "Not such line";
                        }
                        reader.close();

                    }catch (IOException e) {
                        logs.addLogLine("Problem extracting line number " + lineNumber + " from file " + workingFile.getName());
                        resFile = "Problem extracting line number";
                    }
                }
                dataRelation.addRow(columnCounter.toString(),workingFile.getName(),resFile);
                columnCounter++;
            }
            context.addStepSummaryLine("finish with extracting the data");
        }
        context.storeDataValue("DATA",dataRelation);
        context.addStepLog(logs);
        return res;
    }
}
