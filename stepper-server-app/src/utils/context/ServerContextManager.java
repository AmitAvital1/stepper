package utils.context;

import jakarta.servlet.ServletContext;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.manager.DataManager;
import project.java.stepper.load.LoadStepperDataFromXml;
import utils.user.UserManager;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static constants.Constants.*;

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
        List<FlowDefinition> newFlows = LoadStepperDataFromXml.load(bytes,data.getFlowsExecutionManager(),data.getFlows());
        newFlows.stream().forEach(flow -> {
           data.addFlow(flow);
        });
        UserManager userManager = ServerContextManager.getUserManager(servletContext);
        userManager.getRoles().stream().filter(role -> role.getRoleName().equals(ALL_FLOWS)).findFirst().get().setFlowsPermissions(data.getFlows());//Update all flows role

        List<FlowDefinition> readOnlyFlows = new ArrayList<>();
        data.getFlows().stream().forEach(flow -> {
            if(flow.isReadOnly())
                readOnlyFlows.add(flow);
        });
        userManager.getRoles().stream().filter(role -> role.getRoleName().equals(READ_ONLY)).findFirst().get().setFlowsPermissions(readOnlyFlows);//Update all flows role

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
