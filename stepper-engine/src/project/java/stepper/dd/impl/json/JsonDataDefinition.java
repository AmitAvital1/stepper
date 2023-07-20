package project.java.stepper.dd.impl.json;

import project.java.stepper.dd.api.AbstractDataDefinition;
import project.java.stepper.dd.impl.file.FileData;

public class JsonDataDefinition extends AbstractDataDefinition {
    public JsonDataDefinition() {
        super("Json", true, JsonData.class);
    }
}
