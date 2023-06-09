package main;

import exeptions.InvalidChoiseExeption;
import menu.FlowMainMenu;
import menu.LoadDataFromXml;
import menu.MenuOptions;
import project.java.stepper.flow.definition.api.FlowDefinition;

import java.util.*;



public class Main {
    public static void main(String[] args) {

        FlowMainMenu menu = new FlowMainMenu();
        int userChoice = 0;

        String userInput;
        Scanner scanner = new Scanner(System.in);
        List<FlowDefinition> flows = new ArrayList<>();

        do {
            System.out.println("Stepper main menu");
            Arrays.stream(MenuOptions.values()).forEach(item -> item.print());
            try{
                userChoice = scanner.nextInt();
                scanner.nextLine(); // consume the newline character

                switch (userChoice) {
                    case 1://Load Data from xml
                        System.out.println("Please enter a xml native to import flows or 0 to exit");
                        String input = scanner.nextLine();
                        List<FlowDefinition> tempFlow = LoadDataFromXml.LoadData(input);
                        if(tempFlow.size() != 0)//To not override the last xml flows if you cannot reload it
                            flows = tempFlow;
                        break;
                    case 2://Show flow details
                        menu.FlowDefinitionsMenu(flows);
                        break;
                    case 3:
                        menu.FlowsExecuteMenu(flows);
                        break;
                    case 4://Flows History
                        menu.showExecutionsHistory();
                        break;
                    case 5://Stats
                        menu.showFlowStatistics(flows);
                        break;
                    case 6://Exit
                        break;
                    default:
                        throw new InvalidChoiseExeption("Invalid choose.");
                }
            } catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e) {
                if (e.getClass() == InvalidChoiseExeption.class)
                    System.out.println(((InvalidChoiseExeption) e).getMessage());
                else {
                    System.out.println("Please enter only a number");
                    scanner.nextLine(); // consume the newline character
                }
                userChoice = -1;
            }catch (Exception e){
                System.out.println("Error occurred from unknown reason, please contact with the support.");
                userChoice = -1;
            }

        } while (userChoice != MenuOptions.Exit.ordinal() + 1);
    }
}
