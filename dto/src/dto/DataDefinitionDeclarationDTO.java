package dto;

import project.java.stepper.step.api.DataDefinitionDeclaration;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.UIDDPresent;

public class DataDefinitionDeclarationDTO {
    private final String name;
    private final DataNecessity necessity;
    private final String userString;
    private final DataDefinitionDTO dataDefinition;
    private final UIDDPresent UIPresent;

    public DataDefinitionDeclarationDTO(DataDefinitionDeclaration dd){
        name = dd.getName();
        necessity = dd.necessity();
        userString = dd.userString();
        dataDefinition = new DataDefinitionDTO(dd.dataDefinition());
        UIPresent = dd.UIPresent();
    }

    public String getName() {
        return name;
    }

    public DataNecessity necessity() {
        return necessity;
    }

    public String userString() {
        return userString;
    }

    public DataDefinitionDTO dataDefinition() {
        return dataDefinition;
    }

    public UIDDPresent UIPresent() {
        return UIPresent;
    }
}
