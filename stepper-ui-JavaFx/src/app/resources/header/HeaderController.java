package app.resources.header;

import app.resources.main.AppMainConroller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.load.LoadStepperDataFromXml;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;

public class HeaderController {

    private AppMainConroller mainController;
    @FXML
    private Button loadXmlButton;

    @FXML
    private TextField fileXmlPathTextField;

    @FXML
    private Button flowDefinitionButtom;
    @FXML
    private Button flowExecutionButton;
    @FXML
    private Button executionHistoryButton;
    @FXML
    private Button statisticsFlowsButton;

    @FXML
    void loadXmlButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            boolean res = false;
            try{
                mainController.addFlows(LoadStepperDataFromXml.load(selectedFile.getAbsolutePath(),mainController.getFlowsExecutionManager()));
                mainController.clearBodyScreen();
                res = true;
            }catch(FileNotFoundException e) {
                showErrorPopup("An error occurred","Error: XML path not found");
            }catch (JAXBException e) {
                showErrorPopup("An error occurred","Error while trying get your data from XML");
            }catch (StepperExeption e){
                showErrorPopup("Load Failed",e.getMessage());
            }
            if(res) {
                fileXmlPathTextField.setPromptText(selectedFile.getAbsolutePath());
                flowDefinitionButtom.setDisable(false);
                flowExecutionButton.setDisable(true);
                statisticsFlowsButton.setDisable(false);
            }
        }
    }
    public void setMainController(AppMainConroller mainController) {
        this.mainController = mainController;
    }
    private static void showErrorPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void flowDefinitionButtonListen(ActionEvent event) {
        resetButtonsColor();
        flowDefinitionButtom.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");
        mainController.showFlowDefinition();
    }
    @FXML
    void flowExecutionListener(ActionEvent event) {
        resetButtonsColor();
        flowExecutionButton.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");
        mainController.showFlowExectuion();
    }
    @FXML
    void executionHistoryButtonListen(ActionEvent event) {
        resetButtonsColor();
        executionHistoryButton.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");
        mainController.showFlowsHistory();
    }
    @FXML
    void statisticsFlowsButtonListen(ActionEvent event) {
        resetButtonsColor();
        statisticsFlowsButton.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");
        mainController.showFlowsStats();
    }

    public void setFlowHistory() {
        executionHistoryButton.setDisable(false);
    }

    public void resetButtonsColor(){
        flowDefinitionButtom.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);");
        flowExecutionButton.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);");
        statisticsFlowsButton.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);");
        executionHistoryButton.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);");
    }

    public void disableExecutionButton() {
        flowExecutionButton.setDisable(true);
    }
    public void enableExecutionButton() {
        flowExecutionButton.setDisable(false);
    }
}
