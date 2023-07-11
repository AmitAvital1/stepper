package servlets.execution;

import com.google.gson.Gson;
import dto.HistoryFlowsDTO;
import dto.execution.FlowExecutionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.manager.DataManager;
import utils.SessionUtils;
import utils.context.ServerContextManager;
import utils.user.UserManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static constants.Constants.EXECUTION_HISTORY;

@WebServlet(name = "historyServlet", urlPatterns = {EXECUTION_HISTORY})
public class GetExecutionHistory extends HttpServlet {
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
            List<FlowExecution> flows = userManager.getUser(username).getFlowExecutions();
            List<FlowExecutionDTO> dto = new ArrayList<>();
            for(FlowExecution flow : flows)
                dto.add(new FlowExecutionDTO(flow));

            HistoryFlowsDTO history = new HistoryFlowsDTO(dto);

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(history);

            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }
    }
}
