package project.java.stepper.main;

import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.FlowDefinitionImpl;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.runner.FLowExecutor;
import project.java.stepper.step.StepDefinitionRegistry;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import project.java.stepper.step.api.DataNecessity;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        FlowDefinition flow1 = new FlowDefinitionImpl("Flow 1", "Checker");
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.COLLECT_FILES_FOLDER_STEP.getStepDefinition()));
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.FILES_RENAMER_STEP.getStepDefinition()));
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.CSV_EXPORTER_STEP.getStepDefinition()));



        flow1.validateFlowStructure();
        Map<StepUsageDeclaration, List<DataDefinitionDeclaration>> freeInputs = flow1.getFlowFreeInputs();
        if(freeInputs.size() > 0)
        {
            freeInputs.forEach((step,ddList) -> {
                for(DataDefinitionDeclaration dd : ddList)
                {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("The step:" + step.getFinalStepName() + " have an free input - " + dd.userString());
                    if(dd.necessity() == DataNecessity.MANDATORY) {
                        System.out.println("This input is MANDATORY please supply: " + dd.userString());
                        String input = scanner.nextLine();
                        flow1.addFreeInputForStart(dd,input);
                    }

                    else {
                        System.out.println("This input is OPTIONAL please supply: " + dd.userString() + " or press enter to continue");
                        String input = scanner.nextLine();
                        String tempInput = input.trim();
                        if (tempInput.isEmpty())
                            continue;
                        else {
                            flow1.addFreeInputForStart(dd,input);
                        }

                    }
                }
            });
        }
        FLowExecutor fLowExecutor = new FLowExecutor();
        FlowExecution flow2Execution1 = new FlowExecution("1", flow1);
        fLowExecutor.executeFlow(flow2Execution1);

        /*FlowDefinition flow1 = new FlowDefinitionImpl("Flow 1", "Hello world");
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.HELLO_WORLD.getStepDefinition()));
        flow1.validateFlowStructure();

        FlowDefinition flow2 = new FlowDefinitionImpl("Flow 2", "show two person details");
        flow2.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.HELLO_WORLD.getStepDefinition()));
        flow2.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.PERSON_DETAILS.getStepDefinition(), "Person 1 Details"));
        flow2.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.PERSON_DETAILS.getStepDefinition(), "Person 2 Details"));
        flow2.getFlowFormalOutputs().add("DETAILS");
        flow2.validateFlowStructure();

        FLowExecutor fLowExecutor = new FLowExecutor();

        FlowExecution flow2Execution1 = new FlowExecution("1", flow2);
        // collect all user inputs and store them on the flow execution object
        fLowExecutor.executeFlow(flow2Execution1);

        FlowExecution flow2Execution2 = new FlowExecution("2", flow2);
        // collect all user inputs and store them on the flow execution object
        fLowExecutor.executeFlow(flow2Execution1);*/

    }
}
