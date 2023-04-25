package project.java.stepper.step;

import project.java.stepper.step.api.StepDefinition;
import project.java.stepper.step.impl.*;

import java.util.Arrays;
import java.util.Optional;

public enum StepDefinitionRegistry {
    HELLO_WORLD(new HelloWorldStep()),
    PERSON_DETAILS(new PersonDetailsStep()),
    COLLECT_FILES_FOLDER_STEP(new CollectFilesInFolderStep()),
    SPEND_SOME_TIME_STEP(new SpendSomeTimeStep()),
    FILES_DELETER_STEP(new FilesDeleterStep()),
    FILES_RENAMER_STEP(new FilesRenamerStep()),
    FILES_CONTENT_EXTRACTOR_STEP(new FilesContentExtractorStep()),
    CSV_EXPORTER_STEP(new CSVExporterStep()),
    PROPERTIES_EXPORTER_STEP(new PropertiesExporterStep()),
    FILE_DUMPER_STEP(new FileDumperStep())
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }


    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }

    public static StepDefinitionRegistry getStepRegistryByName(String stepName){
        Optional<StepDefinitionRegistry> stepDR = Arrays.stream(StepDefinitionRegistry.values()).filter(step -> stepName.equals(step.getStepDefinition().name())).findFirst();
        return stepDR.get();
    }
}
