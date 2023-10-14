package project.java.stepper.dd.impl.SqlFilter;

import project.java.stepper.dd.api.AbstractDataDefinition;
import project.java.stepper.dd.impl.mapping.MappingData;

public class SqlFilterDataDefinition extends AbstractDataDefinition {
    public SqlFilterDataDefinition() {
        super("Sql Filter", true, SqlFilter.class);
    }
}
