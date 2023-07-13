package servlets.admin;

import com.google.gson.Gson;
import dto.execution.FlowExecutionUUIDDTO;
import dto.roles.RoleDTO;
import dto.roles.RolesDTO;
import dto.roles.UpdateRoleDTO;
import exception.ServerException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.manager.DataManager;
import utils.context.ServerContextManager;
import utils.user.Role;
import utils.user.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import static constants.Constants.ROLES;

@WebServlet(name = "rolesManagement", urlPatterns = {ROLES})
public class RolesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());

        List<Role> roles = userManager.getRoles();
        RolesDTO roleList = new RolesDTO(roles);

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(roleList);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());
        DataManager dataManager = ServerContextManager.getStepperManager(getServletContext());

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        String requestBodyString = requestBody.toString();
        Gson gson = new Gson();
        UpdateRoleDTO updateRole = gson.fromJson(requestBodyString, UpdateRoleDTO.class);
        synchronized (this) {
            userManager.updateRoleFlows(updateRole.getRoleName(), updateRole.getNewFlows(), dataManager);
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());
        DataManager dataManager = ServerContextManager.getStepperManager(getServletContext());

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        String requestBodyString = requestBody.toString();
        Gson gson = new Gson();
        RoleDTO updateRole = gson.fromJson(requestBodyString, RoleDTO.class);
        synchronized (this) {
            try{
                userManager.addRole(updateRole.getRoleName(),updateRole.getUserString());
            }catch (ServerException e){
                PrintWriter out = response.getWriter();
                response.setStatus(504);
                out.print(e.getMessage());
            }

        }
    }
}
