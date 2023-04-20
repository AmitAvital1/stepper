package project.java.stepper.step;

import project.java.stepper.step.api.StepDefinition;
import project.java.stepper.step.impl.*;

public enum StepDefinitionRegistry {
    HELLO_WORLD(new HelloWorldStep()),
    PERSON_DETAILS(new PersonDetailsStep()),
    COLLECT_FILES_FOLDER_STEP(new CollectFilesInFolderStep()),
    SPEND_SOME_TIME_STEP(new SpendSomeTimeStep()),
    FILES_DELETER_STEP(new FilesDeleterStep()),
    FILES_RENAMER_STEP(new FilesRenamerStep()),
    FILES_CONTENT_EXTRACTOR_STEP(new FilesContentExtractorStep()),
    CSV_EXPORTER_STEP(new CSVExporterStep())
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }


    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }
}
