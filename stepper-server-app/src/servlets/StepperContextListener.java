package servlets;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import project.java.stepper.flow.manager.DataManager;
import utils.context.ServerContextManager;

@WebListener
public class StepperContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        DataManager dataManager = ServerContextManager.getStepperManager(servletContextEvent.getServletContext());
        dataManager.getFlowsExecutionManager().shutDown();
    }
}
