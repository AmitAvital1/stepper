package dto.users;

import dto.roles.RoleDTO;
import utils.user.Role;
import utils.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private final String username;
    private boolean isManager;
    private boolean isOnline;

    private List<RoleDTO> roles;


    public UserDTO(User user){
        username = user.getName();
        isManager = user.isManager();
        isOnline = user.isLogin();
        roles = convertUserRoles(user.getUserRoles());

    }

    private List<RoleDTO> convertUserRoles(List<Role> userRoles) {
        List<RoleDTO> res = new ArrayList<>();
        for(Role role : userRoles)
            res.add(new RoleDTO(role));
        return res;
    }

    public String getUsername() {
        return username;
    }

    public boolean isManager() {
        return isManager;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
    public void setManager(boolean manager) {
        isManager = manager;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
