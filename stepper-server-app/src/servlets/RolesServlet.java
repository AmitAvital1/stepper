package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static constants.Constants.ROLES;

@WebServlet(name = "rolesManagement", urlPatterns = {ROLES})
public class RolesServlet extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        //Add new role

    }
}
