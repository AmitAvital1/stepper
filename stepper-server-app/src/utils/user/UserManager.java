package utils.user;

import exception.ServerException;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.manager.DataManager;

import java.util.*;

import static constants.Constants.ALL_FLOWS;

public class UserManager {
    private final List<User> users = new ArrayList<>();
    private final Map<String,User> nameToUser = new HashMap<>();

    private final List<Role> roles = new ArrayList<>();
    private final Map<Role,List<User>> rolesToUsers = new HashMap<>();

    private final Map<String,List<FlowExecution>> userToFlowsExecutions = new HashMap<>();

    public UserManager(){
        Role allFlows = new Role(ALL_FLOWS, ALL_FLOWS);
        roles.add(allFlows);
        rolesToUsers.put(allFlows,new ArrayList<>());
    }

    public boolean addUser(String name){

        if(nameToUser.containsKey(name))
            return false;

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
        return nameToUser.containsKey(username);
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
    }
}
