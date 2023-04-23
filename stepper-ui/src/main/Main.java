package main;

import exeptions.InvalidChoiseExeption;
import menu.FlowMainMenu;
import menu.methods.impl.loadxml.LoadDataFromXml;
import project.java.stepper.flow.definition.api.FlowDefinition;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        FlowMainMenu menu = new FlowMainMenu();
        int userChoice = 0;
        String userInput;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a xml native to import flows");
        String input = scanner.nextLine();
        List<FlowDefinition> flows = LoadDataFromXml.LoadData(input);
        do{
            System.out.println("Please choose the flow you want to work on:");
            for(int i = 0; i < flows.size(); i++)
            {
                System.out.print(i+1 + ".");
                System.out.println(flows.get(i).getName());
            }
            System.out.println("\n\n\n 0.EXIT");
            System.out.println("Please choose option (num):");
            try {
                userChoice = scanner.nextInt();
                if(userChoice < 0 || userChoice > flows.size())
                    throw new InvalidChoiseExeption("Invalid choose.");
                if(userChoice != 0)
                    menu.showFlowMenu(flows.get(userChoice-1));
                //Do some
            } catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e){
                if(e.getClass() == InvalidChoiseExeption.class)
                    System.out.println(((InvalidChoiseExeption) e).getMessage());
                else
                    System.out.println("Please enter a number");
                userChoice = -1;
            }

        }while(userChoice != 0);
        /*FlowMainMenu menu = new FlowMainMenu(flows);
        menu.welcome();
        do {
            menu.printCurrentMenu();
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please choose option (num):");
            try {
                userChoice = scanner.nextInt();
                menu.getUserInputAndInvoke(userChoice);
            } catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e){
                if(e.getClass() == InvalidChoiseExeption.class)
                    System.out.println(((InvalidChoiseExeption) e).getMessage());
                else
                    System.out.println("Please enter a number");
                userChoice = -1;
            }

        }while(userChoice != 0);*/

       /* FlowDefinition flow1 = new FlowDefinitionImpl("Flow 1", "Checker");
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.COLLECT_FILES_FOLDER_STEP.getStepDefinition()));
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.FILES_RENAMER_STEP.getStepDefinition()));
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.FILE_DUMPER_STEP.getStepDefinition()));



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
