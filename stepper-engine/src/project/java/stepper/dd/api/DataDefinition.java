package project.java.stepper.dd.api;

import project.java.stepper.exceptions.StepperExeption;

public interface DataDefinition {
    String getName();
    boolean isUserFriendly();
    Class<?> getType();
    <T> T convertUserInputToDataType(String input, Class<T> expectedDataType) throws StepperExeption;
}
