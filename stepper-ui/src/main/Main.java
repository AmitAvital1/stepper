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
            userChoice = scanner.nextInt();
            try {
                switch (userChoice) {
                    case 1://Load Data from xml
                        scanner.nextLine();
                        System.out.println("Please enter a xml native to import flows");
                        String input = scanner.nextLine();
                        flows = LoadDataFromXml.LoadData(input);
                        break;
                    case 2://Show flow details
                        menu.FlowDefenitionsMenu(flows);
                        break;
                    case 3:
                        menu.FlowsExicuteMenu(flows);
                        break;
                    case 4://Flows History
                        menu.showExecutionsHistoty();
                        break;
                    case 5://Stats

                        break;
                    case 6://Exit
                        break;
                    default:
                        throw new InvalidChoiseExeption("Invalid choose.");
                }
            } catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e) {
                if (e.getClass() == InvalidChoiseExeption.class)
                    System.out.println(((InvalidChoiseExeption) e).getMessage());
                else
                    System.out.println("Please enter a number");
                userChoice = -1;
            }
        } while (userChoice != MenuOptions.Exit.ordinal() + 1);

    }
}
