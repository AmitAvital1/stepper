package menu;

import java.util.UUID;
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
    private List<StepAndDD> freeInputListToPrint;
    FLowExecutor fLowExecutor;
    private class StepAndDD{
        public StepUsageDeclaration step;
        public DataDefinitionDeclaration data;
        public StepAndDD(StepUsageDeclaration s,DataDefinitionDeclaration d){step = s; data = d;}
    }

    public FlowMainMenu(){
        flowExecutions = new ArrayList<>();
        fLowExecutor = new FLowExecutor();
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
                        System.out.println("Please add:" + freeInputListToPrint.get(userChoice - 1).data.userString());
                        String input = scanner.nextLine();
                        flow.addFreeInputForStart(freeInputListToPrint.get(userChoice - 1).step, freeInputListToPrint.get(userChoice - 1).data, input);
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
                        "(" + dd.necessity() + ")" + (flow.getStartersFreeInputForContext().containsKey(key.getinputToFinalName().get(dd.getName())) ? "(ADDED)" : "(NOT ADDED)"));
                index++;
                freeInputListToPrint.add(new StepAndDD(key,dd));
            }
        }
        return index;
    }
    public void FlowDecDefenitionsMenu(FlowDefinition flow) {
        int counter = 1;
        System.out.println("Flow name:" + flow.getName());
        System.out.println("Description:" + flow.getDescription());
        System.out.println("Formal outputs:");
        for(Map.Entry<String, DataDefinitionDeclaration> entry : flow.getFormalOutput().entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclaration value = entry.getValue();
            System.out.println(counter + "." + key + ":" + value.userString());
            counter++;
        }
        counter = 1;
        System.out.println("Read only:" + flow.isReadOnly());
        System.out.println("Steps: ");
        for(StepUsageDeclaration step : flow.getFlowSteps())
        {
            if(step.getFinalStepName().equals(step.getStepDefinition().name()))
                System.out.println(counter + "." + step.getFinalStepName() + ", Read Only:" + step.getStepDefinition().isReadonly());
            else
                System.out.println(counter + "." + step.getStepDefinition().name() + " Alias to:" + step.getFinalStepName() + ", Read Only:" + step.getStepDefinition().isReadonly());
            counter++;
        }
        counter = 1;
        System.out.println("Free inputs: ");
        for(Map.Entry<StepUsageDeclaration, List<DataDefinitionDeclaration>> entry : flow.getFlowFreeInputs().entrySet()) {
            StepUsageDeclaration key = entry.getKey();
            List<DataDefinitionDeclaration> value = entry.getValue();
            for(DataDefinitionDeclaration data : value) {
                System.out.println(counter + "." + key.getinputToFinalName().get(data.getName()) + ":" + data.userString() + "(" + data.necessity() + ")");
                System.out.println("Data type:" + data.dataDefinition().getName());
                counter++;
            }
        }
        counter = 1;
    }

    public void FlowDefenitionsMenu(List<FlowDefinition> flows) {
        if(flows.size() == 0) {
            System.out.println("There are no loaded flows.");
            return;
        }

        int userChoice = 0;
        Scanner scanner = new Scanner(System.in);
        do{
            System.out.println("Flows definitions menu:");
            for(int i = 0; i < flows.size(); i++)
            {
                System.out.print(i+1 + ".");
                System.out.println(flows.get(i).getName());
            }
            System.out.println("\n0.EXIT");
            try {
                userChoice = scanner.nextInt();
                if(userChoice < 0 || userChoice > flows.size())
                    throw new InvalidChoiseExeption("Invalid choose.");
                if(userChoice != 0)
                    FlowDecDefenitionsMenu(flows.get(userChoice-1));
                //Do some
            } catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e){
                if(e.getClass() == InvalidChoiseExeption.class)
                    System.out.println(((InvalidChoiseExeption) e).getMessage());
                else
                    System.out.println("Please enter only numbers");
                userChoice = -1;
            }

        }while(userChoice != 0);
    }
    public void FlowsExicuteMenu(List<FlowDefinition> flows) {
        if(flows.size() == 0) {
            System.out.println("There are no loaded flows.");
            return;
        }

        int userChoice = 0;
        Scanner scanner = new Scanner(System.in);
        do{
            System.out.println("Please choose flow to execute:");
            for(int i = 0; i < flows.size(); i++)
            {
                System.out.print(i+1 + ".");
                System.out.println(flows.get(i).getName());
            }
            System.out.println("\n0.EXIT");
            try {
                userChoice = scanner.nextInt();
                if(userChoice < 0 || userChoice > flows.size())
                    throw new InvalidChoiseExeption("Invalid choose.");
                if(userChoice != 0) {
                    UUID uuid = UUID.randomUUID();
                    FlowExecution flowExecution = new FlowExecution(uuid.toString(), flows.get(userChoice - 1));
                    showFlowDecMenu(flowExecution);
                }
            } catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e){
                if(e.getClass() == InvalidChoiseExeption.class)
                    System.out.println(((InvalidChoiseExeption) e).getMessage());
                else
                    System.out.println("Please enter only numbers");
                userChoice = -1;
            }

        }while(userChoice != 0);
    }
    private void executeFlow(FlowExecution flow) throws MissMandatoryInput {
        if(flow.validateToExecute()){
            fLowExecutor.executeFlow(flow);
            flowExecutions.add(flow);
        }
    }

    public void showExecutionsHistoty() {
        int i = 1;
        for(FlowExecution flow : flowExecutions){

        }
    }

}
