package project.java.stepper.step.api;

import project.java.stepper.dd.api.DataDefinition;

import java.util.HashMap;
import java.util.Map;

public class DataDefinitionDeclarationImpl implements DataDefinitionDeclaration {

    private final String name;
    private final DataNecessity necessity;
    private final String userString;
    private final DataDefinition dataDefinition;

    public DataDefinitionDeclarationImpl(String name, DataNecessity necessity, String userString, DataDefinition dataDefinition) {
        this.name = name;
        this.necessity = necessity;
        this.userString = userString;
        this.dataDefinition = dataDefinition;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataNecessity necessity() {
        return necessity;
    }

    @Override
    public String userString() {
        return userString;
    }

    @Override
    public DataDefinition dataDefinition() {
        return dataDefinition;
    }
}
