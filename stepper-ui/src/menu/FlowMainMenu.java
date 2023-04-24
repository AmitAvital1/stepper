package menu;

import exeptions.InvalidChoiseExeption;
import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.runner.FLowExecutor;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.*;

public class FlowMainMenu {
    private List<FlowDefinition> flows;
    List<FlowExecution> flowExecutions;
    private List<DataDefinitionDeclaration> freeInputListToPrint;


    public FlowMainMenu(){
        flowExecutions = new ArrayList<>();
    }


    public void showFlowMenu(FlowDefinition flow) {
        int userChoice = -1;
        Scanner scanner = new Scanner(System.in);
        do{
            System.out.println("Flow name:" + flow.getName());
            System.out.println("Tons of details on the flow");
            System.out.println("1.Execute flow");
        try {
            userChoice = scanner.nextInt();
            scanner.nextLine(); // consume the newline character
            if (userChoice == 1) {
                FLowExecutor fLowExecutor = new FLowExecutor();
                FlowExecution flowExecution = new FlowExecution("200", flow);
                showFlowDecMenu(flowExecution);
            }
            else if(userChoice == 0){
                break;
            }
            else
                throw new InvalidChoiseExeption("Invalid choose.");

        }catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e){
            if(e.getClass() == InvalidChoiseExeption.class || e.getClass() == MissMandatoryInput.class)
                System.out.println(e.getMessage());
            else
                System.out.println("Please enter a number");

            userChoice = -1;
        }
        }while(userChoice != 0);


    }
    public void showFlowDecMenu(FlowExecution flow) {
        int userChoice = -1;
        int index = 0;
        Scanner scanner = new Scanner(System.in);
        Map<StepUsageDeclaration, List<DataDefinitionDeclaration>> freeInputs = flow.getFlowDefinition().getFlowFreeInputs();
        do{
            System.out.println("Flow name:" + flow.getFlowDefinition().getName());

                index = printAllFreeInputsAndReturnSize(flow,freeInputs);

                System.out.println((index+1) + ". Execute flow");
                System.out.println("\n\n\n 0.EXIT");

                try {
                    userChoice = scanner.nextInt();
                    scanner.nextLine(); // consume the newline character
                    if (userChoice > 0 && userChoice <= freeInputListToPrint.size()) {
                        System.out.println("Please add:" + freeInputListToPrint.get(userChoice - 1).userString());
                        String input = scanner.nextLine();
                        flow.addFreeInputForStart(freeInputListToPrint.get(userChoice - 1), input);
                        userChoice = -1;
                    }
                    else if(userChoice == freeInputListToPrint.size() + 1){
                            executeFlow(flow);
                    }
                    else if(userChoice == 0){
                        break;
                    }
                    else
                        throw new InvalidChoiseExeption("Invalid choose.");

                }catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption | MissMandatoryInput e){
                    if(e.getClass() == InvalidChoiseExeption.class || e.getClass() == MissMandatoryInput.class)
                        System.out.println(e.getMessage());
                    else
                        System.out.println("Please enter a number");

                    userChoice = -1;
                }
        }while(userChoice != 0);

    }
    private int printAllFreeInputsAndReturnSize(FlowExecution flow, Map<StepUsageDeclaration, List<DataDefinitionDeclaration>> freeInputs) {
        int index = 0;
        if (freeInputs.size() > 0) {
            System.out.println("You have free inputs:");
        }
        index = 0;
        freeInputListToPrint = new ArrayList<>();
        for (Map.Entry<StepUsageDeclaration, List<DataDefinitionDeclaration>> entry : freeInputs.entrySet()) {
            StepUsageDeclaration key = entry.getKey();
            List<DataDefinitionDeclaration> value = entry.getValue();
            for (DataDefinitionDeclaration dd : value) {
                System.out.println((index + 1) + ". Step '" + key.getFinalStepName() + ", " + dd.userString() +
                        "(" + dd.necessity() + ")" + (flow.getStartersFreeInputForContext().containsKey(dd.getName()) ? "(ADDED)" : "(NOT ADDED)"));
                index++;
                freeInputListToPrint.add(dd);
            }
        }
        return index;
    }
    private void executeFlow(FlowExecution flow) throws MissMandatoryInput
    {
        if(flow.validateToExecute()){
            FLowExecutor fLowExecutor = new FLowExecutor();
            flowExecutions.add(flow);
            fLowExecutor.executeFlow(flow);
        }
    }

}
