package utils.user;

import project.java.stepper.flow.execution.FlowExecution;

import java.util.ArrayList;
import java.util.List;


public class User {
    private List<Role> userRoles = new ArrayList<>();
    private final String name;
    private boolean isManager = false;

    private final List<FlowExecution> flowExecutions = new ArrayList<>();

    User(String name){this.name = name;}

    public String getName() {
        return name;
    }

    public void setManager(boolean manager) {
        isManager = manager;
    }

    public boolean isManager() {
        return isManager;
    }

    public List<Role> getUserRoles() {
        return userRoles;
    }

    public void addFlowExecution(FlowExecution flowExecution){
        flowExecutions.add(flowExecution);
    }
    public List<FlowExecution> getFlowExecutions() {
        return flowExecutions;
    }
}
