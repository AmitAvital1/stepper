package utils.user;

import project.java.stepper.flow.definition.api.FlowDefinition;

import java.util.List;

public class Role {

    private final String roleName;
    private final String userString;
    private List<FlowDefinition> flowsPermissions;

    Role(String roleName,String userString){
        this.roleName = roleName;
        this.userString = userString;
    }
    Role(String roleName, String userString, List<FlowDefinition> flowsPermissions){
        this.roleName = roleName;
        this.userString = userString;
        this.flowsPermissions = flowsPermissions;
    }

    @Override
    public String toString() {
        return userString;
    }
}
