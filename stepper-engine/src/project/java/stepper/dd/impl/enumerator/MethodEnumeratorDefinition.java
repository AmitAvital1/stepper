package project.java.stepper.dd.impl.enumerator;

import project.java.stepper.dd.api.AbstractDataDefinition;

public class MethodEnumeratorDefinition extends AbstractDataDefinition {
    public MethodEnumeratorDefinition() {
        super("Enumerator", true, MethodEnum.class);
    }
}
