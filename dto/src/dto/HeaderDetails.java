package dto;

import utils.user.Role;

import java.util.ArrayList;
import java.util.List;

public class HeaderDetails {
    private List<Role> userRoles = new ArrayList<>();
    private Boolean isManager;

    public List<Role> getuserRoles() {
        return userRoles;
    }
    public String getUserRoleToPrint(){
        List<String> rolesToString = new ArrayList<>();
        for (Role role : userRoles) {
            rolesToString.add(role.toString());
        }
        return String.join(", ", rolesToString);
    }

    public void setuserRoles(List<Role> userRoles) {
        this.userRoles = userRoles;
    }

    public HeaderDetails(List<Role> userRoles, boolean isManager){
        this.isManager = isManager;
        this.userRoles = userRoles;
    }

    public Boolean getisManager() {
        return isManager;
    }

    public void setisManager(boolean manager) {
        isManager = manager;
    }

}
