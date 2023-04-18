package project.java.stepper.flow.execution.context;

import project.java.stepper.dd.api.DataDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.*;

public class StepExecutionContextImpl implements StepExecutionContext {

    private final Map<String, Object> dataValues;
    private StepUsageDeclaration currentWorkingStep;
    private List<StepLogs> flowLogs;

    public StepExecutionContextImpl() {
        dataValues = new HashMap<>();
        flowLogs = new ArrayList<>();
    }

    @Override
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) {
       //Find of there is an input match
        DataDefinitionDeclaration theExpectedDataDefinition = null;
        Optional<DataDefinitionDeclaration> maybeTheExpectedDataDefinition =
                currentWorkingStep.getStepDefinition().inputs().stream()
                .filter((input) -> input.getName() == dataName)
                .findFirst();

        if(maybeTheExpectedDataDefinition.isPresent()){
            theExpectedDataDefinition = maybeTheExpectedDataDefinition.get();
            if (expectedDataType.isAssignableFrom(theExpectedDataDefinition.dataDefinition().getType())) {
                Object aValue = dataValues.get(dataName);
                return expectedDataType.cast(aValue);
            }
        }
        else{
               //Handle there is no input;
        }
        return null;
    }

    @Override
    public boolean storeDataValue(String dataName, Object value) {
        dataValues.put(dataName, value);
       /* DataDefinition theData = null;

        // we have the DD type so we can make sure that its from the same type
        if (theData.getType().isAssignableFrom(value.getClass())) {
            dataValues.put(dataName, value);
        } else {
            // error handling of some sort...
        }*/

        return true;
    }
    public void updateCurrentWorkingStep(StepUsageDeclaration newStep){
        currentWorkingStep = newStep;
    }
    public StepUsageDeclaration getCurrentWorkingStep(){
        return currentWorkingStep;
    }
    public void addStepLog(StepLogs stepLogsToAdd) {
        flowLogs.add(stepLogsToAdd);
    }

}
