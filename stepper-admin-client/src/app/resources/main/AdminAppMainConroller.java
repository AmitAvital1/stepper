package app.resources.main;

import app.resources.body.AdminBodyController;
import app.resources.header.AdminHeaderController;
import app.resources.util.http.AdminHttpClientUtil;
import com.google.gson.Gson;
import dto.FlowDefinitionDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.runner.FlowsExecutionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static app.resources.util.AdminConstants.ADMIN_LOGIN;


public class AdminAppMainConroller {

    @FXML private Parent headerComponent;
    @FXML private AdminHeaderController headerComponentController;
    @FXML private Parent bodyComponent;
    @FXML private AdminBodyController bodyComponentController;

    private Stage primaryStage;

    private List<FlowDefinitionDTO> flows = new ArrayList<>();
    private final List<FlowExecution> flowExecutions = new ArrayList<>();


    private FlowsExecutionManager flowsExecutionManager = new FlowsExecutionManager();

    @FXML
    public void initialize() {
        if (headerComponentController != null && bodyComponentController != null) {
            headerComponentController.setMainController(this);
            bodyComponentController.setMainController(this);
        }
        adminLogin();
    }
    public void showFlowDefinition() {
        bodyComponentController.showFlowDefinition();
    }
    public void showFlowsHistory(){
        bodyComponentController.showFlowHistory();
    }
    public void showFlowsStats() {bodyComponentController.showFlowStats();}

    public void showFlowExectuion(){ bodyComponentController.showFlowExecution(); }
    public void addFlows(List<FlowDefinitionDTO> newFlows){flows = newFlows;}
    public List<FlowDefinitionDTO> getFlows(){return flows;}
    public void addExecutorFlow(FlowExecution flowExecution){
        this.flowExecutions.add(flowExecution);
        headerComponentController.setFlowHistory();//Set the button available
    }
    public List<FlowExecution> getFlowExecutions(){return flowExecutions;}

    public FlowsExecutionManager getFlowsExecutionManager() {
        return flowsExecutionManager;
    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public AdminHeaderController getHeaderComponentController() {
        return headerComponentController;
    }

    public void clearBodyScreen() {
        bodyComponentController.clearScreen();
    }

    public void shutDown(){
        bodyComponentController.close();
        adminLogout();
    }
    private void adminLogin() {
        String finalUrl = HttpUrl
                .parse(ADMIN_LOGIN)
                .newBuilder()
                .addQueryParameter("login", "login")
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new String("Dummy"));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .put(requestBody)
                .build();

        AdminHttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showErrorAndExit("An error occurred. Application will be closed.")
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            showErrorAndExit(responseBody)
                    );
                }
            }
        });
    }
    private void adminLogout() {
        String finalUrl = HttpUrl
                .parse(ADMIN_LOGIN)
                .newBuilder()
                .addQueryParameter("login", "logout")
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new String("Dummy"));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .put(requestBody)
                .build();

        AdminHttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

            }
        });
    }
    private void showErrorAndExit(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                // Close the application
                primaryStage.close();
            }
        });
    }

}
