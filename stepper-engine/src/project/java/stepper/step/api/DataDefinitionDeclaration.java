package project.java.stepper.step.api;

import project.java.stepper.dd.api.DataDefinition;

public interface DataDefinitionDeclaration {
    String getName();
    DataNecessity necessity();
    String userString();
    DataDefinition dataDefinition();

}
