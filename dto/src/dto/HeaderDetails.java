package dto;

import dto.roles.RoleDTO;
import utils.user.Role;

import java.util.ArrayList;
import java.util.List;

public class HeaderDetails {
    private List<RoleDTO> userRoles;
    private Boolean isManager;

    public List<RoleDTO> getuserRoles() {
        return userRoles;
    }
    public String getUserRoleToPrint(){
        List<String> rolesToString = new ArrayList<>();
        for (RoleDTO role : userRoles) {
            rolesToString.add(role.getRoleName());
        }
        return String.join(", ", rolesToString);
    }

    public void setuserRoles(List<Role> userRoles) {
        this.userRoles = new ArrayList<>();
        userRoles.forEach(role -> this.userRoles.add(new RoleDTO(role)));
    }

    public HeaderDetails(List<Role> userRoles, boolean isManager){
        this.isManager = isManager;
        this.userRoles = new ArrayList<>();
        userRoles.forEach(role -> this.userRoles.add(new RoleDTO(role)));
    }

    public Boolean getisManager() {
        return isManager;
    }

    public void setisManager(boolean manager) {
        isManager = manager;
    }

}
