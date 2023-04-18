package project.java.stepper.step;

import project.java.stepper.step.api.StepDefinition;
import project.java.stepper.step.impl.CollectFilesInFolderStep;
import project.java.stepper.step.impl.HelloWorldStep;
import project.java.stepper.step.impl.PersonDetailsStep;
import project.java.stepper.step.impl.SpendSomeTimeStep;

public enum StepDefinitionRegistry {
    HELLO_WORLD(new HelloWorldStep()),
    PERSON_DETAILS(new PersonDetailsStep()),
    COLLECT_FILES_FOLDER_STEP(new CollectFilesInFolderStep()),
    SPEND_SOME_TIME_STEP(new SpendSomeTimeStep())
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }


    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }
}
