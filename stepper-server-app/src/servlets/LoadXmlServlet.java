package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import project.java.stepper.exceptions.StepperExeption;
import utils.context.ServerContextManager;
import constants.Constants;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.Collection;
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
        PrintWriter out = response.getWriter();
        Collection<Part> parts = req.getParts();
        StringBuilder xmlContent = new StringBuilder();
        for (Part part : parts) {

            //to write the content of the file to a string
            xmlContent.append(readFromInputStream(part.getInputStream()));
        }

        try{
            ServerContextManager.addFlows(getServletContext(),xmlContent.toString().getBytes());
            out.print("Load successful");
        }catch(FileNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            response.getOutputStream().print("Error: XML path not found");
        }catch (JAXBException e) {
            response.setStatus(ERROR_OCCURRED);
            out.print("Error while trying get your data from XML");
        }catch (StepperExeption e){
            response.setStatus(LOAD_FAILED);
            out.print(e.getMessage());
        }

    }
    private String readFromInputStream(InputStream inputStream) {
        return new Scanner(inputStream).useDelimiter("\\Z").next();
    }
}
