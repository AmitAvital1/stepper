package project.java.stepper.dd.impl;

import project.java.stepper.dd.api.DataDefinition;
import project.java.stepper.dd.impl.file.FileDataDefinition;
import project.java.stepper.dd.impl.list.ListDataDefinition;
import project.java.stepper.dd.impl.number.DoubleDataDefinition;
import project.java.stepper.dd.impl.number.IntegerDataDefinition;
import project.java.stepper.dd.impl.relation.RelationDataDefinition;
import project.java.stepper.dd.impl.string.StringDataDefinition;

public enum DataDefinitionRegistry implements DataDefinition{
    STRING(new StringDataDefinition()),
    DOUBLE(new DoubleDataDefinition()),
    INTEGER(new IntegerDataDefinition()),
    RELATION(new RelationDataDefinition()),
    LIST(new ListDataDefinition()),
    FILE(new FileDataDefinition())
    ;

    DataDefinitionRegistry(DataDefinition dataDefinition) {
        this.dataDefinition = dataDefinition;
    }

    private final DataDefinition dataDefinition;

    @Override
    public String getName() {
        return dataDefinition.getName();
    }

    @Override
    public boolean isUserFriendly() {
        return dataDefinition.isUserFriendly();
    }

    @Override
    public Class<?> getType() {
        return dataDefinition.getType();
    }
}
