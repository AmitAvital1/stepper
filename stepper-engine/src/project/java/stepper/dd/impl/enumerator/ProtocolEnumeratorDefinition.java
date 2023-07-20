package project.java.stepper.dd.impl.enumerator;

import project.java.stepper.dd.api.AbstractDataDefinition;

public class ProtocolEnumeratorDefinition extends AbstractDataDefinition {
    public ProtocolEnumeratorDefinition() {
        super("Enumerator", true, ProtocolEnum.class);
    }
}
