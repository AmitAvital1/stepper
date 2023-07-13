package dto.roles;

import utils.user.Role;

import java.util.ArrayList;
import java.util.List;

public class RolesDTO {
    private final List<RoleDTO> rolesList;

    public RolesDTO(List<Role> rolesList) {
        this.rolesList = new ArrayList<>();
        for(Role role : rolesList)
            this.rolesList.add(new RoleDTO(role));
    }

    public List<RoleDTO> getRolesList() {
        return rolesList;
    }
}
