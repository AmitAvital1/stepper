package servlets.execution;

import com.google.gson.Gson;
import dto.StepperDTO;
import dto.execution.FlowExecutionDTO;
import dto.execution.FlowExecutionUUIDDTO;
import dto.execution.FreeInputDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.manager.DataManager;
import utils.SessionUtils;
import utils.context.ServerContextManager;
import utils.user.User;
import utils.user.UserManager;

import java.io.*;

import static constants.Constants.*;

@WebServlet(name = "flowExecutionServlet", urlPatterns = {EXECUTION})
public class ExecutionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        UserManager userManager = ServerContextManager.getUserManager(getServletContext());
        DataManager dataManager = ServerContextManager.getStepperManager(getServletContext());
        String username = SessionUtils.getUsername(request);

        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else {
            String flowName = request.getParameter(FLOW_NAME);
            User userDetails = userManager.getUser(username);

            FlowDefinition flowDefinition = dataManager.getFlowDefinitionByName(flowName);

            FlowExecution flowExecution = userManager.addFlowExecution(username,flowDefinition);
            dataManager.addFlowExecution(flowExecution);

            FlowExecutionUUIDDTO flowExecutionUUIDDTO = new FlowExecutionUUIDDTO(flowExecution.getUniqueId());
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(flowExecutionUUIDDTO);

            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder requestBody = new StringBuilder();
        DataManager dataManager = ServerContextManager.getStepperManager(getServletContext());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        String requestBodyString = requestBody.toString();

        Gson gson = new Gson();
        FreeInputDTO freeInputDTO = gson.fromJson(requestBodyString, FreeInputDTO.class);
        FlowExecution flowExecution = dataManager.getFlowExecutionByUUID(freeInputDTO.getUUID());

        try{
            if(freeInputDTO.getInputFinalName() != null)
                flowExecution.addFreeInputForStart(freeInputDTO.getInputFinalName(), flowExecution.getFlowDefinition().getFreeInputFinalNameToDD().get(freeInputDTO.getInputFinalName()), freeInputDTO.getInputData());
            if(flowExecution != null)
                flowExecution.validateToExecute();
        }catch (MissMandatoryInput e) {
            response.setStatus(402);
        }catch (StepperExeption e){
            response.setStatus(403);
            out.print(e.getMessage());
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        PrintWriter out = response.getWriter();

        StringBuilder requestBody = new StringBuilder();
        DataManager dataManager = ServerContextManager.getStepperManager(getServletContext());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        String requestBodyString = requestBody.toString();

        Gson gson = new Gson();
        FlowExecutionUUIDDTO uuiddto = gson.fromJson(requestBodyString, FlowExecutionUUIDDTO.class);
        FlowExecution flowExecution = dataManager.getFlowExecutionByUUID(uuiddto.getUuid());
        try {
            flowExecution.validateToExecute();
            dataManager.getFlowsExecutionManager().exeFlow(flowExecution);
        }catch (MissMandatoryInput e) {
            response.setStatus(402);
            out.print(e.getMessage());
        }
    }

}
