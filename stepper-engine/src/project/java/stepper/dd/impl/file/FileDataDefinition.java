package project.java.stepper.dd.impl.file;

import project.java.stepper.dd.api.AbstractDataDefinition;

public class FileDataDefinition extends AbstractDataDefinition {
    public FileDataDefinition() {
        super("File", false, FileData.class);
    }
}
