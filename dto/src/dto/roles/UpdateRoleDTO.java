package dto.roles;

import java.util.List;

public class UpdateRoleDTO {
    private final String roleName;
    private final List<String> newFlows;

    public UpdateRoleDTO(String roleName, List<String> newFlows) {
        this.roleName = roleName;
        this.newFlows = newFlows;
    }

    public String getRoleName() {
        return roleName;
    }

    public List<String> getNewFlows() {
        return newFlows;
    }
}
