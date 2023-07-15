package servlets.admin;

import com.google.gson.Gson;
import dto.roles.RolesDTO;
import dto.roles.UpdateRoleDTO;
import dto.users.UpdateManagerDTO;
import dto.users.UserDTO;
import dto.users.UsersDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.java.stepper.flow.manager.DataManager;
import utils.context.ServerContextManager;
import utils.user.Role;
import utils.user.User;
import utils.user.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import static constants.Constants.USERS;

@WebServlet(name = "usersManagement", urlPatterns = {USERS})
public class UsersManagementServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());

        List<User> users = userManager.getUsers();
        UsersDTO dto = new UsersDTO(users,userManager.getRoles());

        Gson gson = new Gson();
        String jsonResponse = gson.toJson(dto);

        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        String requestBodyString = requestBody.toString();
        Gson gson = new Gson();
        UserDTO userUpdateRoles = gson.fromJson(requestBodyString, UserDTO.class);
        synchronized (this) {
            userManager.updateUserRoles(userUpdateRoles);
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/json");
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        String requestBodyString = requestBody.toString();
        Gson gson = new Gson();
        UpdateManagerDTO updateManagerDTO = gson.fromJson(requestBodyString, UpdateManagerDTO.class);
        synchronized (this) {
            userManager.updateUserManager(updateManagerDTO.getUsername(),updateManagerDTO.isManager());
        }
    }
}
