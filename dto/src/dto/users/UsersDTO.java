package dto.users;

import dto.roles.RoleDTO;
import dto.roles.RolesDTO;
import utils.user.Role;
import utils.user.User;

import java.util.ArrayList;
import java.util.List;

public class UsersDTO {
    private List<UserDTO> users;
    private List<RoleDTO> allRoles;

    public UsersDTO(List<User> users, List<Role> roles){
        this.users = new ArrayList<>();
        for(User user : users)
            this.users.add(new UserDTO(user));

        this.allRoles = new ArrayList<>();
        for(Role role : roles)
            this.allRoles.add(new RoleDTO(role));
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public List<RoleDTO> getAllRoles() {
        return allRoles;
    }
}
