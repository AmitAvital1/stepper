package utils.user;

import dto.users.UserDTO;
import exception.ServerException;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.manager.DataManager;

import java.util.*;

import static constants.Constants.ALL_FLOWS;
import static constants.Constants.READ_ONLY;

public class UserManager {
    private final List<User> users = new ArrayList<>();
    private final Map<String,User> nameToUser = new HashMap<>();
    private boolean isAdminLogin = false;

    private final List<Role> roles = new ArrayList<>();
    private final Map<Role,List<User>> rolesToUsers = new HashMap<>();

    private final Map<String,List<FlowExecution>> userToFlowsExecutions = new HashMap<>();

    public UserManager(){
        Role allFlows = new Role(ALL_FLOWS, ALL_FLOWS);
        Role readOnly = new Role(READ_ONLY, "Read-Only flows");
        roles.add(allFlows);
        roles.add(readOnly);
        rolesToUsers.put(allFlows,new ArrayList<>());
        rolesToUsers.put(readOnly,new ArrayList<>());
    }

    public boolean addUser(String name){

        if(nameToUser.containsKey(name)) {
            nameToUser.get(name).setLogin(true);
            return false;
        }

        User newUser = new User(name);
        users.add(newUser);
        nameToUser.put(name,newUser);
        return true;
    }
    public void removeUser(String name){
        User removedUser = nameToUser.remove(name);
        users.remove(removedUser);
    }

    public boolean isUserExists(String username) {
        if(nameToUser.containsKey(username))
            return nameToUser.get(username).isLogin();
        else
            return false;
    }

    public User getUser(String username){
        return nameToUser.get(username);
    }

    public FlowExecution addFlowExecution(String username,FlowDefinition flow){
        UUID uuid = UUID.randomUUID();
        FlowExecution flowExecution = new FlowExecution(uuid.toString(), flow);
        userToFlowsExecutions.computeIfAbsent(username, k -> new ArrayList<>()).add(flowExecution);
        nameToUser.get(username).addFlowExecution(flowExecution);
        return flowExecution;
    }
    public void addFlowExecutionToRerun(String username,FlowExecution flow){
        userToFlowsExecutions.computeIfAbsent(username, k -> new ArrayList<>()).add(flow);
        nameToUser.get(username).addFlowExecution(flow);
    }

    public List<Role> getRoles() {
        return roles;
    }
    public synchronized void updateRoleFlows(String roleName,List<String> flowList, DataManager dataManager){
        Role role = roles.stream().filter(r -> r.getRoleName().equals(roleName)).findFirst().get();
        List<FlowDefinition> flowPerm = new ArrayList<>();
        for(String flowName : flowList )
            flowPerm.add(dataManager.getFlowDefinitionByName(flowName));

        role.setFlowsPermissions(flowPerm);

    }
    public synchronized void addRole(String roleName,String roleDesc) throws ServerException {
        if(roleName.isEmpty())
            throw new ServerException("Please enter role name");
        Optional<Role> optionalRole = roles.stream().filter(r -> r.getRoleName().equals(roleName)).findFirst();
        if(optionalRole.isPresent())
            throw new ServerException("This role already exist");

        Role newRole = new Role(roleName,roleDesc);
        roles.add(newRole);
        rolesToUsers.put(newRole,new ArrayList<>());
    }
    public synchronized List<User> getUsers() {
        return users;
    }

    public synchronized void updateUserRoles(UserDTO userUpdateRoles) {
        User user = nameToUser.get(userUpdateRoles.getUsername());
        List<Role> newRoles = new ArrayList<>();
        List<String> userNewRolesName = new ArrayList<>();
        userUpdateRoles.getRoles().forEach(r -> userNewRolesName.add(r.getRoleName()));

        this.roles.forEach(role -> {
            if(userNewRolesName.contains(role.getRoleName())) {
                newRoles.add(role);
                if (!rolesToUsers.get(role).contains(user)) {
                    rolesToUsers.get(role).add(user);
                }
            }else{
                rolesToUsers.get(role).remove(user);
            }
        });
        user.setUserRoles(newRoles);

    }
    public synchronized void updateUserManager(String username, boolean isManager){
        User user = this.nameToUser.get(username);
        user.setManager(isManager);

    }

    public void logout(String usernameFromSession) {
        nameToUser.get(usernameFromSession).setLogin(false);
    }

    public boolean isAdminLogin() {
        return isAdminLogin;
    }

    public void setAdminLogin(boolean adminLogin) {
        isAdminLogin = adminLogin;
    }

    public Map<Role, List<User>> getRolesToUsers() {
        return rolesToUsers;
    }
}
