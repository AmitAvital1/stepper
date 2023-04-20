package project.java.stepper.dd.impl.mapping;


import project.java.stepper.dd.api.AbstractDataDefinition;

public class MappingDataDefinition extends AbstractDataDefinition {
    public MappingDataDefinition() {
        super("Mapping", false, MappingData.class);
    }
}
