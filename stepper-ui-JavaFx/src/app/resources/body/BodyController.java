package app.resources.body;

import app.resources.body.execution.FlowsExecutionController;
import app.resources.main.AppMainConroller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class BodyController {
    private AppMainConroller mainController;
    @FXML
    private StackPane bodyPane;

    public void setMainController(AppMainConroller mainController) {
        this.mainController = mainController;
    }

    public void showFlowDefinition() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/flowdefinition/flowDefinition.fxml");
        fxmlLoader.setLocation(url);
        loadScreen(fxmlLoader, url);
    }
    public void showFlowExectuion() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/execution/flowExecution.fxml");
        fxmlLoader.setLocation(url);
        loadScreen(fxmlLoader, url);
    }
    private void loadScreen(FXMLLoader fxmlLoader,URL url){
        try {
            Parent screen = fxmlLoader.load(url.openStream());
            BodyControllerDefinition bController = fxmlLoader.getController();
            bController.setFlowsDetails(mainController.getFlows());
            bController.show();
            bController.setBodyController(this);
            bodyPane.getChildren().setAll(screen);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeExistFlowScreen(FlowDefinition flow) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/execution/flowExecution.fxml");
        fxmlLoader.setLocation(url);
        try {
            Parent screen = fxmlLoader.load(url.openStream());
            FlowsExecutionController bController = fxmlLoader.getController();
            bController.setFlowsDetails(mainController.getFlows());
            bController.show();
            bController.handleFlowButtonAction(flow);
            bodyPane.getChildren().setAll(screen);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addFlowExecutor(FlowExecution flowExecution){mainController.addExecutorFlow(flowExecution);}
    public List<FlowExecution> getFlowExecutions(){return mainController.getFlowExecutions();}
}
