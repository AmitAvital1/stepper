package dto.roles;

import utils.user.Role;
import utils.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolesDTO {
    private final List<RoleDTO> rolesList;
    private final List<List<String>> roleToUser = new ArrayList<>();

    public RolesDTO(List<Role> rolesList, Map<Role, List<User>> rolesToUsers) {
        this.rolesList = new ArrayList<>();
        for(Role role : rolesList) {
            RoleDTO rdto = new RoleDTO(role);
            List<String> names = new ArrayList<>();
            rolesToUsers.get(role).stream().forEach(u -> names.add(u.getName()));
            this.rolesList.add(rdto);
            roleToUser.add(names);
        }
    }

    public List<RoleDTO> getRolesList() {
        return rolesList;
    }

    public List<List<String>> getRoleToUser() {
        return roleToUser;
    }
}
