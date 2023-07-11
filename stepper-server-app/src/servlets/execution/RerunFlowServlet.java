package servlets.execution;

import com.google.gson.Gson;
import dto.execution.FlowExecutionDTO;
import dto.execution.FlowExecutionUUIDDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.manager.DataManager;
import utils.SessionUtils;
import utils.context.ServerContextManager;
import utils.user.User;
import utils.user.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static constants.Constants.*;

@WebServlet(name = "flowRerunServlet", urlPatterns = {EXECUTION_RERUN})
public class RerunFlowServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {

        UserManager userManager = ServerContextManager.getUserManager(getServletContext());
        DataManager dataManager = ServerContextManager.getStepperManager(getServletContext());
        String username = SessionUtils.getUsername(req);
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            StringBuilder requestBody = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }
            String requestBodyString = requestBody.toString();
            Gson gson = new Gson();
            FlowExecutionUUIDDTO flowExecutionUUIDDTO = gson.fromJson(requestBodyString, FlowExecutionUUIDDTO.class);
            FlowExecution flowExecution = dataManager.getFlowExecutionByUUID(flowExecutionUUIDDTO.getUuid());
            FlowExecution newFlow = flowExecution.reRunFlow();

            userManager.addFlowExecutionToRerun(username,newFlow);
            dataManager.addFlowExecution(newFlow);

            FlowExecutionDTO flowExecutionDTO = new FlowExecutionDTO(newFlow);
            String jsonResponse = gson.toJson(flowExecutionDTO);
            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }
    }
}
