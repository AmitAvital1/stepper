package utils.user;

import project.java.stepper.flow.definition.api.FlowDefinition;

import java.util.ArrayList;
import java.util.List;

public class Role {

    private final String roleName;
    private String userString;
    private List<FlowDefinition> flowsPermissions;

    Role(String roleName,String userString){
        this.roleName = roleName;
        this.userString = userString;
        flowsPermissions = new ArrayList<>();
    }
    Role(String roleName, String userString, List<FlowDefinition> flowsPermissions){
        this.roleName = roleName;
        this.userString = userString;
        this.flowsPermissions = flowsPermissions;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getUserString() {
        return userString;
    }

    public List<FlowDefinition> getFlowsPermissions() {
        return flowsPermissions;
    }

    public void setFlowsPermissions(List<FlowDefinition> flowsPermissions) {
        this.flowsPermissions = flowsPermissions;
    }
}
