package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.enumerator.ZipEnum;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipperStep extends AbstractStepDefinition {
    public ZipperStep() {
        super("Zipper", false);

        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("OPERATION", DataNecessity.MANDATORY, "Operation type", DataDefinitionRegistry.ZIPENUMERATOR));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Zip operation result", DataDefinitionRegistry.STRING));
    }
    public synchronized StepResult invoke(StepExecutionContext context) throws NoStepInput {
        String source = context.getDataValue("SOURCE", String.class);
        ZipEnum operation = context.getDataValue("OPERATION", ZipEnum.class);
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res;

        if (operation == ZipEnum.ZIP) {
            if (isDirectory(source)) {
                String zipFilePath = source + ".zip";
                logs.addLogLine("About to perform operation ZIP" + " on source " + source);
                try {
                    zipFolder(source, zipFilePath);
                    logs.addLogLine("Folder zipped successfully: " + zipFilePath);
                    context.addStepSummaryLine("Folder zipped successfully: " + zipFilePath);
                    res = StepResult.SUCCESS;
                }catch (IOException e) {
                    logs.addLogLine("Error occurred during the zip");
                    context.addStepSummaryLine("Step failed cause error occurred during the zip");
                    res = StepResult.FAILURE;
                }
            }
            else {
                File file = new File(source);
                if (file.exists()) {
                    logs.addLogLine("About to perform operation ZIP" + " on source " + source);
                    String fileName = file.getName();
                    String baseName = getBaseName(fileName);
                    String zipFilePath = file.getParent() + File.separator + baseName + ".zip";
                    try (FileInputStream fis = new FileInputStream(file);
                         FileOutputStream fos = new FileOutputStream(zipFilePath);
                         ZipOutputStream zos = new ZipOutputStream(fos)) {

                        // Create a new ZIP entry using the file name without extension
                        ZipEntry zipEntry = new ZipEntry(fileName);
                        zos.putNextEntry(zipEntry);

                        // Read and write the file's contents to the ZIP file
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            zos.write(buffer, 0, bytesRead);
                        }
                        zos.closeEntry();

                        logs.addLogLine("File zipped successfully: " + zipFilePath);
                        context.addStepSummaryLine("File zipped successfully: " + zipFilePath);
                        res = StepResult.SUCCESS;
                    } catch (IOException e) {
                        logs.addLogLine("Error occurred during the zip");
                        context.addStepSummaryLine("Step failed cause error occurred during the zip");
                        res = StepResult.FAILURE;
                    }
                } else {
                    logs.addLogLine("Invalid source for zip operation: " + source);
                    context.addStepSummaryLine("Step failed cause invalid source for zip operation");
                    res = StepResult.FAILURE;
                }
            }
        }else if (operation == ZipEnum.UNZIP) {
            if (isZipFile(source)) {
                logs.addLogLine("About to perform operation UNZIP" + " on source " + source);
                try {
                    unzipFolder(source);
                    logs.addLogLine("Folder unzipped successfully: " + getDestinationFolder(source));
                    context.addStepSummaryLine("Folder unzipped successfully: " + getDestinationFolder(source));
                    res = StepResult.SUCCESS;
                }catch (IOException e) {
                    logs.addLogLine("Error occurred during the unzip");
                    context.addStepSummaryLine("Step failed cause error occurred during the unzip");
                    res = StepResult.FAILURE;
                }
            } else {
                logs.addLogLine("Invalid source for unzip operation: " + source);
                context.addStepSummaryLine("Step failed cause invalid source for unzip operation");
                res = StepResult.FAILURE;
            }
        } else {
            logs.addLogLine("Invalid operation");
            context.addStepSummaryLine("Step failed cause invalid operation");
            res = StepResult.FAILURE;
        }
        context.storeDataValue("RESULT", res.toString());
        context.addStepLog(logs);
        return res;
    }
    private static boolean isDirectory(String source) {
        File file = new File(source);
        return file.isDirectory();
    }
    private static boolean isZipFile(String source) {
        return source.toLowerCase().endsWith(".zip");
    }
    private static void zipFolder(String source, String zipFilePath) throws IOException {
            File folder = new File(source);

            FileOutputStream fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zos = new ZipOutputStream(fos);

            zipFolderRecursive(folder, folder.getName(), zos);

            zos.close();
            fos.close();

    }
    private static void zipFolderRecursive(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipFolderRecursive(file, parentFolder + "/" + file.getName(), zos);
            } else {
                FileInputStream fis = new FileInputStream(file);

                String entryPath = parentFolder + "/" + file.getName();
                ZipEntry zipEntry = new ZipEntry(entryPath);
                zos.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                fis.close();
                zos.closeEntry();
            }
        }
    }
    private static void unzipFolder(String source) throws IOException {

            File zipFile = new File(source);
            String destinationFolder = getDestinationFolder(source);

            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                String entryName = zipEntry.getName();
                File newFile = new File(destinationFolder, entryName);

                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                    fos.close();
                }

                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
            zis.close();
    }
    private static String getDestinationFolder(String source) {
        File zipFile = new File(source);
        String parentFolder = zipFile.getParent();
        return parentFolder != null ? parentFolder : "";
    }
    private static String getBaseName(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }
}
