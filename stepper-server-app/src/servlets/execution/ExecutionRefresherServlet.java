package servlets.execution;

import com.google.gson.Gson;
import dto.execution.FlowExecutionDTO;
import dto.execution.FlowExecutionUUIDDTO;
import dto.execution.FreeInputDTO;
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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static constants.Constants.EXECUTION_REFRESHER;

@WebServlet(name = "flowExecutionRefresherServlet", urlPatterns = {EXECUTION_REFRESHER})
public class ExecutionRefresherServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        StringBuilder requestBody = new StringBuilder();
        DataManager dataManager = ServerContextManager.getStepperManager(getServletContext());
        UserManager userManager = ServerContextManager.getUserManager(getServletContext());
        String username = SessionUtils.getUsername(req);

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
        FlowExecutionDTO flowExecutionDTO;

        if(userManager.getUser(username).isManager())
            flowExecutionDTO = new FlowExecutionDTO(flowExecution);
        else
            flowExecutionDTO = new FlowExecutionDTO(flowExecution,userManager.getUser(username).getFlowsPermissionNames());

        String jsonResponse = gson.toJson(flowExecutionDTO);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }

    }
}
