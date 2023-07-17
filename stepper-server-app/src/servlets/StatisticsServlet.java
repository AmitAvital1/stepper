package servlets;

import com.google.gson.Gson;
import dto.HistoryFlowsDTO;
import dto.execution.FlowStatisticsDTO;
import dto.execution.FlowsStatisticsDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.manager.DataManager;
import utils.SessionUtils;
import utils.context.ServerContextManager;
import utils.user.UserManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static constants.Constants.STATISTICS;

@WebServlet(name = "statisticsServlet", urlPatterns = {STATISTICS})
public class StatisticsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        DataManager dataManager = ServerContextManager.getStepperManager(getServletContext());

            List<FlowDefinition> flows = dataManager.getFlows();
            List<FlowStatisticsDTO> dto = new ArrayList<>();
            for(FlowDefinition flow : flows)
                dto.add(new FlowStatisticsDTO(flow.getName(),flow.getFlowSteps(),flow.getFlowStatistics()));

            FlowsStatisticsDTO flowsStats = new FlowsStatisticsDTO(dto);

            Gson gson = new Gson();
            String jsonResponse = gson.toJson(flowsStats);

            try (PrintWriter out = response.getWriter()) {
                out.print(jsonResponse);
                out.flush();
            }
        }
}
