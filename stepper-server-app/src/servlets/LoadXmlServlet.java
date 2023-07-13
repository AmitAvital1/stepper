package servlets;

import com.google.gson.Gson;
import dto.FlowDefinitionDTO;
import dto.StepperDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.manager.DataManager;
import utils.context.ServerContextManager;
import constants.Constants;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import static constants.Constants.LOAD_XML_PATH;

@WebServlet(name = "loadXmlServlet", urlPatterns = {LOAD_XML_PATH})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadXmlServlet extends HttpServlet {

    private static final Integer LOAD_FAILED = 510;
    private static final Integer ERROR_OCCURRED = 511;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain;charset=UTF-8");
        Collection<Part> parts = req.getParts();
        StringBuilder xmlContent = new StringBuilder();
        DataManager dataManager = ServerContextManager.getStepperManager(getServletContext());
        for (Part part : parts) {

            //to write the content of the file to a string
            xmlContent.append(readFromInputStream(part.getInputStream()));
        }

        try{
            ServerContextManager.addFlows(getServletContext(),xmlContent.toString().getBytes());

            List<FlowDefinition> flows = dataManager.getFlows();
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
        }catch(FileNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getOutputStream().print("Error: XML path not found");
        }catch (JAXBException e) {
            PrintWriter out = response.getWriter();
            response.setStatus(ERROR_OCCURRED);
            out.print("Error while trying get your data from XML");
        }catch (StepperExeption e){
            PrintWriter out = response.getWriter();
            response.setStatus(LOAD_FAILED);
            out.print(e.getMessage());
        }

    }
    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
