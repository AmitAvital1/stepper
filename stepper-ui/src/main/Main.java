package main;

import exeptions.InvalidChoiseExeption;
import menu.FlowMainMenu;
import menu.MenuOptions;
import menu.methods.impl.loadxml.LoadDataFromXml;
import project.java.stepper.flow.definition.api.FlowDefinition;

import java.util.*;

import static menu.MenuOptions.*;


public class Main {
    public static void main(String[] args) {

        FlowMainMenu menu = new FlowMainMenu();
        int userChoice = 0;

        String userInput;
        Scanner scanner = new Scanner(System.in);
        List<FlowDefinition> flows = new ArrayList<>();

        System.out.println("Please enter a xml native to import flows");
        String input = scanner.nextLine();
        do{
            System.out.println("Stepper main menu");
            Arrays.stream(MenuOptions.values()).forEach(item -> item.print());
            userChoice = scanner.nextInt();
            try {
                switch (userChoice)
                {
                    case 1:
                        flows = LoadDataFromXml.LoadData(input);
                }
            } catch (InputMismatchException | IllegalStateException | InvalidChoiseExeption e){
                if(e.getClass() == InvalidChoiseExeption.class)
                    System.out.println(((InvalidChoiseExeption) e).getMessage());
                else
                    System.out.println("Please enter a number");
                userChoice = -1;
            }
        }while(userChoice != MenuOptions.Exit.ordinal()+1);

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
    }
}
