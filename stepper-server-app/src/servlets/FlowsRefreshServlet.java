package servlets;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.FlowDefinitionDTO;
import dto.HeaderDetails;
import dto.StepperDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.manager.DataManager;
import utils.SessionUtils;
import utils.context.ServerContextManager;
import utils.user.User;
import utils.user.UserManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static constants.Constants.*;

@WebServlet(name = "flowDefinitionFetch", urlPatterns = {FLOW_DEFINITION})
public class FlowsRefreshServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());
        DataManager dataManager = ServerContextManager.getStepperManager(getServletContext());

        String username = SessionUtils.getUsername(request);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else {
            List<FlowDefinition> flows = dataManager.getFlows();
            //Todo all roles


            List<FlowDefinitionDTO> dto = new ArrayList<>();
            for(FlowDefinition flow : flows)
                dto.add(new FlowDefinitionDTO(flow));
            StepperDTO stepper = new StepperDTO(dto);

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(stepper);

            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }
    }
}
