package project.java.stepper.dd.impl.SqlFilter;

public enum SqlOperation {
    EQUAL,LIKE;

    @Override
    public String toString() {
        if(this == EQUAL)
            return "=";
        else if(this == LIKE)
            return "LIKE";
        else return null;
    }
}
