package utils.context;

import jakarta.servlet.ServletContext;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.manager.DataManager;
import project.java.stepper.load.LoadStepperDataFromXml;
import utils.user.UserManager;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.List;

import static constants.Constants.STEPPER_DATA_MANAGER;
import static constants.Constants.STEPPER_USER_MANAGER;

public class ServerContextManager {

    private static final Object stepperManagerLock = new Object();
    private static final Object userManagerLock = new Object();

    public static DataManager getStepperManager(ServletContext servletContext) {

        synchronized (stepperManagerLock) {
            if (servletContext.getAttribute(STEPPER_DATA_MANAGER) == null) {
                servletContext.setAttribute(STEPPER_DATA_MANAGER, new DataManager());
            }
        }
        return (DataManager) servletContext.getAttribute(STEPPER_DATA_MANAGER);
    }
    public static void addFlows(ServletContext servletContext, byte[] bytes) throws JAXBException, StepperExeption, FileNotFoundException {

        DataManager data = ServerContextManager.getStepperManager(servletContext);
        List<FlowDefinition> newFlows = LoadStepperDataFromXml.load(bytes,data.getFlowsExecutionManager());
        newFlows.stream().forEach(flow -> {
           data.addFlow(flow);
        });
    }

    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(STEPPER_USER_MANAGER) == null) {
                servletContext.setAttribute(STEPPER_USER_MANAGER, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(STEPPER_USER_MANAGER);
    }
    public static boolean addUser(ServletContext servletContext, String name){

        UserManager data = ServerContextManager.getUserManager(servletContext);
        if(data.addUser(name)){
            return true;
        }
        return false;
    }

}
