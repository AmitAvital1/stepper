package servlets;

import com.google.gson.Gson;
import dto.HeaderDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessionUtils;
import utils.user.User;
import utils.user.UserManager;
import utils.context.ServerContextManager;

import java.io.IOException;
import java.io.PrintWriter;

import static constants.Constants.USER_HEAD_DETAILS;

@WebServlet(name = "headerDetailsServlet", urlPatterns = {USER_HEAD_DETAILS})
public class UserDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else {
            User userDetails = userManager.getUser(username);
            HeaderDetails details = new HeaderDetails(userDetails.getUserRoles(),userDetails.isManager());
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(details);

            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

}
