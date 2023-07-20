package project.java.stepper.dd.impl.enumerator;

public enum ProtocolEnum {
    HTTP,HTTPS;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
