package menu;

import java.util.UUID;
import exeptions.InvalidChoiseExeption;
import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.runner.FLowExecutor;
import project.java.stepper.flow.statistics.FlowStats;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.*;

public class FlowMainMenu {
    private final List<FlowExecution> flowExecutions;//Save all the executions flows
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

    public void getFlowInputToExecute(FlowExecution flow) {
        int userChoice = -1;
        int index = 0;
        Scanner scanner = new Scanner(System.in);
        Map<StepUsageDeclaration, List<DataDefinitionDeclaration>> freeInputs = flow.getFlowDefinition().getFlowFreeInputs();
        do{
            System.out.println("\nFlow name:" + flow.getFlowDefinition().getName());

                index = printAllFreeInputsAndReturnSize(flow,freeInputs);

                System.out.println((index+1) + ". Execute flow");
                System.out.println("\n 0.EXIT");

                try {
                    userChoice = scanner.nextInt();
                    scanner.nextLine();
                    if (userChoice > 0 && userChoice <= freeInputListToPrint.size()) {
                        System.out.println("Please add:" + freeInputListToPrint.get(userChoice - 1).data.userString());
                        String input = scanner.nextLine();
                        flow.addFreeInputForStart(freeInputListToPrint.get(userChoice - 1).step, freeInputListToPrint.get(userChoice - 1).data, input);
                        userChoice = -1;
                    }
                    else if(userChoice == freeInputListToPrint.size() + 1){
                            executeFlow(flow);
                            break;
                    }
                    else if(userChoice == 0){
                        break;
                    }
                    else
                        throw new InvalidChoiseExeption("Invalid choose.");

                }catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption |
                        StepperExeption e){
                    if(e.getClass() != InputMismatchException.class)
                        System.out.println(e.getMessage());
                    else {
                        System.out.println("Please enter a number");
                        scanner.nextLine();
                    }

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
                        "(" + dd.necessity() + ")" + "[" + dd.dataDefinition().getName() + "]" + (flow.getStartersFreeInputForContext().containsKey(key.getinputToFinalName().get(dd.getName())) ? "(ADDED)" : "(NOT ADDED)"));
                index++;
                freeInputListToPrint.add(new StepAndDD(key,dd));
            }
        }
        return index;
    }
    public void FlowDecDefenitionsMenu(FlowDefinition flow) {
        int counter = 1;
        System.out.println("\n-Flow name:" + flow.getName());
        System.out.println("-Description:" + flow.getDescription());
        System.out.println("-Formal outputs:" + (flow.getFormalOutput().size() == 0 ? "Do not have formal outputs" : ""));
        for(Map.Entry<String, DataDefinitionDeclaration> entry : flow.getFormalOutput().entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclaration value = entry.getValue();
            System.out.println(counter + "." + key + ":" + value.userString());
            counter++;
        }
        counter = 1;
        System.out.println("-Read only:" + flow.isReadOnly());
        System.out.println("-Steps: ");
        for(StepUsageDeclaration step : flow.getFlowSteps())
        {
            if(step.getFinalStepName().equals(step.getStepDefinition().name()))
                System.out.println(counter + "." + step.getFinalStepName() + ", Read Only:" + step.getStepDefinition().isReadonly());
            else
                System.out.println(counter + "." + step.getStepDefinition().name() + " Alias to:" + step.getFinalStepName() + ", Read Only:" + step.getStepDefinition().isReadonly());
            counter++;
        }
        counter = 1;
        System.out.println("-Free inputs: ");
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
            System.out.println("\nFlows definitions menu:");
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
            scanner.nextLine();
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
            System.out.println("\nPlease choose flow to execute:");
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
                    getFlowInputToExecute(flowExecution);
                }
            } catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e){
                if(e.getClass() == InvalidChoiseExeption.class)
                    System.out.println(((InvalidChoiseExeption) e).getMessage());
                else
                    System.out.println("Please enter only numbers");
                userChoice = -1;
            }
            scanner.nextLine();
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
        int userChoice;
        if(flowExecutions.size() == 0){
            System.out.println("There are no flows that executed to get details");
            return;
        }
        do{
            System.out.println("\nPlease choose the flow to get details:");
            for(FlowExecution flow : flowExecutions){
                 System.out.println(i + "." + flow.getFlowDefinition().getName() + "-" + flow.getUniqueId() + "(" + flow.getStartedTime() + ")");
                 i++;
             }
            i=1;
            System.out.println("\n 0.EXIT");
            Scanner scanner = new Scanner(System.in);
            try {
                userChoice = scanner.nextInt();
                if (userChoice > 0 && userChoice <= flowExecutions.size()) {
                    FlowExecution flow = flowExecutions.get(userChoice-1);
                    System.out.println(flow.getUniqueId() + ":" + flow.getFlowDefinition().getName());
                    System.out.println("-Result:" + flow.getFlowExecutionResult() + ", Started Time:" + flow.getStartedTime());
                    System.out.println("-Free inputs details:" + (flow.getAllFreeInputsWithDataToPrintList().size() == 0 ? "No free inputs in the flow" : ""));
                    flow.getAllFreeInputsWithDataToPrintList().stream().forEach(System.out::println);
                    System.out.println("-All flow outputs:" + (flow.getAllOutPutsWithDataToPrintList().size() == 0 ? "No outputs in the flow" : ""));
                    flow.getAllOutPutsWithDataToPrintList().stream().forEach(System.out::println);
                    System.out.println("-All steps details:");
                    flow.getAllStepsWithDataToPrintList().stream().forEach(System.out::println);
                    userChoice = -1;
                }
                else if(userChoice == 0){
                    break;
                }
                else
                    throw new InvalidChoiseExeption("Invalid choose.");

            }catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e){
                if(e.getClass() == InvalidChoiseExeption.class )
                    System.out.println(e.getMessage());
                else
                    System.out.println("Please enter only a number");
                userChoice = -1;
            }
            scanner.nextLine(); // consume the newline character
        }while(userChoice != 0);
    }

    public void showFlowStatistics(List<FlowDefinition> flows){
        if(flows.size() == 0) {
            System.out.println("There are no loaded flows.");
            return;
        }
        int userChoice = 0;
        Scanner scanner = new Scanner(System.in);
        do{
            System.out.println("\nPlease choose flow to get statistics:");
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
                    FlowDefinition cFlow = flows.get(userChoice-1);
                    FlowStats flowStats = cFlow.getFlowStatistics();
                    if(flowStats.getExecutesRunTimes() == 0)
                        System.out.println(cFlow.getName() + " did not executed yet therefore have no statistics to present.");
                    else {
                        System.out.println(cFlow.getName() + " statistics:");
                        System.out.println("Numbers of executed times: " + flowStats.getExecutesRunTimes());
                        System.out.println("Average execution time: " + flowStats.getAvgExecutesRunTimes() + ".ms");
                        System.out.println("Flow steps stats:");
                        int index = 1;
                        for(StepUsageDeclaration step : cFlow.getFlowSteps()){
                            int stepTimesExecuted = flowStats.getStepTimesExecuted(step);
                            System.out.println(index + "." + step.getFinalStepName() + " Numbers of executed times[" + stepTimesExecuted + "]" +
                                    " Average execution time: " + (stepTimesExecuted > 0 ? (flowStats.getStepAverageTimeExecuted(step) + ".ms") : "NA"));
                            index++;
                        }
                    }


                }
            } catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e){
                if(e.getClass() == InvalidChoiseExeption.class)
                    System.out.println(((InvalidChoiseExeption) e).getMessage());
                else
                    System.out.println("Please enter only numbers");
                userChoice = -1;
            }
            scanner.nextLine();
        }while(userChoice != 0);
    }

}
