package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtils;
import utils.user.UserManager;
import utils.context.ServerContextManager;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.*;

@WebServlet(name = "loginServlet", urlPatterns = {USER_LOGIN})
public class UserLoginServlet extends HttpServlet {

    private static final Integer NO_USERNAME = 510;
    private static final Integer USERNAME_EXIST = 511;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());
        if (usernameFromSession == null) {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null || usernameFromParameter.isEmpty()) {
                response.setStatus(NO_USERNAME);
                out.print("Please supply username to inorder to login");
            } else {
                usernameFromParameter = usernameFromParameter.trim();
                synchronized (this) {
                    if (userManager.isUserExists(usernameFromParameter)) {
                        String errorMessage = "Username " + usernameFromParameter + " already logged in. Please enter a different username.";
                        response.setStatus(USERNAME_EXIST);
                        out.print(errorMessage);
                    } else {
                        //add the new user to the users list
                        userManager.addUser(usernameFromParameter);
                        request.getSession(true).setAttribute(USERNAME, usernameFromParameter);
                        out.print("Login successfully");
                    }
                }
            }
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String usernameFromSession = SessionUtils.getUsername(request);
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());
        userManager.logout(usernameFromSession);
    }
}
