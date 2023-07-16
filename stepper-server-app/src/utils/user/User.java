package utils.user;

import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;

import java.util.ArrayList;
import java.util.List;


public class User {
    private List<Role> userRoles = new ArrayList<>();
    private final String name;
    private boolean isManager = false;
    private boolean login = true;

    private final List<FlowExecution> flowExecutions = new ArrayList<>();

    private List<FlowDefinition> flowsPermission = new ArrayList<>();

    private List<String> flowsPermissionNames = new ArrayList<>();

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

    public synchronized void setUserRoles(List<Role> userRoles) {
        this.userRoles = userRoles;
    }

    public List<String> getFlowsPermissionNames() {
        List<FlowDefinition> userFlows = new ArrayList<>();
        List<String> userFlowsName = new ArrayList<>();
        for(Role role : this.userRoles){
            role.getFlowsPermissions().stream().forEach(f -> {
                if(!userFlowsName.contains(f.getName())) {
                    userFlows.add(f);
                    userFlowsName.add(f.getName());
                }
            });
        }
        flowsPermission = userFlows;
        flowsPermissionNames = userFlowsName;
        return flowsPermissionNames;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }
    public List<FlowDefinition> getFlowsPermission() {
        List<FlowDefinition> userFlows = new ArrayList<>();
        List<String> userFlowsName = new ArrayList<>();
        for(Role role : this.userRoles){
            role.getFlowsPermissions().stream().forEach(f -> {
                if(!userFlowsName.contains(f.getName())) {
                    userFlows.add(f);
                    userFlowsName.add(f.getName());
                }
            });
        }
        flowsPermission = userFlows;
        flowsPermissionNames = userFlowsName;
        return flowsPermission;
    }
}
