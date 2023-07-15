package dto.roles;

import dto.FlowDefinitionDTO;
import project.java.stepper.flow.definition.api.FlowDefinition;
import utils.user.Role;

import java.util.ArrayList;
import java.util.List;

public class RoleDTO {
    private final String roleName;
    private final String userString;
    private final List<FlowDefinitionDTO> flowsPermissions;

    public RoleDTO(String roleName, String userString) {
        this.roleName = roleName;
        this.userString = userString;
        this.flowsPermissions = new ArrayList<>();
    }

    public RoleDTO(Role role){
        this.roleName = role.getRoleName();
        this.userString = role.getUserString();
        this.flowsPermissions = convertFlowPermissions(role.getFlowsPermissions());
    }

    private List<FlowDefinitionDTO> convertFlowPermissions(List<FlowDefinition> flowsPermissions) {
        List<FlowDefinitionDTO> res = new ArrayList<>();

        for(FlowDefinition flow : flowsPermissions)
            res.add(new FlowDefinitionDTO(flow));

        return res;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getUserString() {
        return userString;
    }

    public List<FlowDefinitionDTO> getFlowsPermissions() {
        return flowsPermissions;
    }
}
