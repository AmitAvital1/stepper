package project.java.stepper.dd.impl.enumerator;

import project.java.stepper.dd.api.AbstractDataDefinition;

public class ZipEnumeratorDefinition extends AbstractDataDefinition {
    public ZipEnumeratorDefinition() {
        super("Enumerator", true, ZipEnum.class);
    }
}
