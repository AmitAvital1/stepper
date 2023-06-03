package project.java.stepper.flow.definition.api;

import project.java.stepper.dd.api.DataDefinition;
import project.java.stepper.step.api.StepDefinition;

import java.util.Map;

public interface StepUsageDeclaration {
    String getFinalStepName();
    StepDefinition getStepDefinition();
    boolean skipIfFail();
    Map<String,String> getinputToFinalName();
    Map<String,String> getoutputToFinalName();
    Map<String,String> getFinalNameToInput();
    Map<String,String> getFinalNameToOutput();
    Map<String, DataDefinition> getFinalNamesOutputsToDD();
    boolean addLevelAlias(String name,String finalName);
    void addCustomeMapInput(String inputName,String inputTarget);
    String thisInputHaveCustomeMapping(String data);
    boolean equals(Object o);
}
