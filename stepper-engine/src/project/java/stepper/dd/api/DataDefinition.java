package project.java.stepper.dd.api;

public interface DataDefinition {
    String getName();
    boolean isUserFriendly();
    Class<?> getType();
    <T> T convertUserInputToDataType(String input, Class<T> expectedDataType);
}
