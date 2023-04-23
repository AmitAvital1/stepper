package menu.user.input;

import menu.methods.MenuMethodsRegisty;
import project.java.stepper.flow.definition.api.FlowDefinition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserInputContext {
    int currentFlowIndex;
    int currentChoseIndex;
    private List<FlowDefinition> flows;
    private List<MenuMethodsRegisty> menusCurrentOptions;

    public UserInputContext() {
        flows = new ArrayList<>();
        menusCurrentOptions = new LinkedList<>();
    }
    public List<FlowDefinition> getFlows() {return flows;}
    public void addFlow(FlowDefinition flow){ flows.add(flow); }
    public void updateCurrentFlowIndex(int currentFlowIndex) {this.currentFlowIndex = currentFlowIndex;}
    public void addToMenu(MenuMethodsRegisty opt){menusCurrentOptions.add(opt);}
    public List<MenuMethodsRegisty> getMenu() {return menusCurrentOptions;}
}
