package dto;

import project.java.stepper.step.api.StepDefinition;

public class StepDefinitionDTO {
    private final String stepName;
    private final boolean readonly;

    public StepDefinitionDTO(StepDefinition stepDefinition) {
        this.stepName = stepDefinition.name();
        this.readonly = stepDefinition.isReadonly();
    }

    public String name() {
        return stepName;
    }

    public boolean isReadonly() {
        return readonly;
    }
}
