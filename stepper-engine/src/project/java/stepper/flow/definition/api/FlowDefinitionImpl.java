package project.java.stepper.flow.definition.api;

import javafx.util.Pair;
import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FlowDefinitionImpl implements FlowDefinition {

    private final String name;
    private final String description;
    private final List<String> flowOutputs;
    private final List<StepUsageDeclaration> steps;
    private List<DataDefinitionDeclaration> freeInputsDataDefinitionDeclaration;
    private Map<StepUsageDeclaration,List<DataDefinitionDeclaration>> freeInputsStepToDataDefinitionDeclaration;
    private Map<String, Object> startersFreeInputForContext;

    public FlowDefinitionImpl(String name, String description) {
        this.name = name;
        this.description = description;
        flowOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        startersFreeInputForContext = new HashMap<>();
    }

    public void addFlowOutput(String outputName) {
        flowOutputs.add(outputName);
    }

    @Override
    public void validateFlowStructure() {
        freeInputsStepToDataDefinitionDeclaration = new HashMap<>();
        freeInputsDataDefinitionDeclaration = new ArrayList<>();
        List<DataDefinitionDeclaration> inputsFlow = new ArrayList<>();
        for(StepUsageDeclaration step : steps) {
            List<DataDefinitionDeclaration> dataDefinitionDeclarationStream =
                    step.getStepDefinition().inputs().stream()
                            .filter(input ->  (inputsFlow.stream().allMatch(i -> (i.getName() != input.getName()) || i.dataDefinition().getType() != input.dataDefinition().getType() )))
                            .collect(Collectors.toList());

            if(dataDefinitionDeclarationStream.size() > 0) {
                dataDefinitionDeclarationStream.forEach(freeInput -> freeInputsDataDefinitionDeclaration.add(freeInput));
                freeInputsStepToDataDefinitionDeclaration.put(step, dataDefinitionDeclarationStream);
            }

            dataDefinitionDeclarationStream =  step.getStepDefinition().outputs().stream()
                    .collect(Collectors.toList());

            dataDefinitionDeclarationStream.forEach(theOutput -> inputsFlow.add(theOutput));

        }
        /*System.out.println("The free inputs is:");
        freeInputsStepToDataDefinitionDeclaration.forEach((theStep, freeInput) -> {
            if (freeInput.size() > 0) {
                System.out.println("Step:" + theStep.getFinalStepName());
                freeInput.forEach(i -> System.out.println(i.getName() + ":" + i.userString() + " " + i.necessity()));
            }
        });*/
    }

    @Override
    public Map<StepUsageDeclaration,List<DataDefinitionDeclaration>> getFlowFreeInputs() {
        return freeInputsStepToDataDefinitionDeclaration;
    }

    @Override
    public Map<String, Object> getStartersFreeInputForContext() {
        return startersFreeInputForContext;
    }

    @Override
    public boolean addFreeInputForStart(DataDefinitionDeclaration dataDefinitionDeclaration, Object data) {
        //Need to check the exist type;
        startersFreeInputForContext.put(dataDefinitionDeclaration.getName(),data);
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<StepUsageDeclaration> getFlowSteps() {
        return steps;
    }

    @Override
    public List<String> getFlowFormalOutputs() {
        return flowOutputs;
    }
}
