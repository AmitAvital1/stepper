package app.resources.body.roles;

import app.resources.body.AdminBodyController;
import app.resources.body.AdminBodyControllerDefinition;
import app.resources.util.http.AdminHttpClientUtil;
import com.google.gson.Gson;
import dto.FlowDefinitionDTO;
import dto.roles.RoleDTO;
import dto.roles.RolesDTO;
import dto.roles.UpdateRoleDTO;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import okhttp3.*;
import org.controlsfx.control.ListSelectionView;
import org.controlsfx.control.PopOver;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static app.resources.util.AdminConstants.*;

public class RolesManageController implements AdminBodyControllerDefinition {

    private AdminBodyController bodyForStatisticsController;

    @FXML
    private VBox rolesList;

    @FXML
    private VBox roleDetails;

    @FXML
    private Label roleName;

    @FXML
    private Label roleDescription;

    @FXML
    private ListView<String> roleUsersList;

    @FXML
    private StackPane flowRoleDefinitionList;

    @FXML
    private Button openButton;

    private List<FlowDefinitionDTO> flowsDefinitions;

    private List<Button> allRolesButtons = new ArrayList<>();


    private PopOver errorPopOver;

    @Override
    public void show() {

        roleDetails.setVisible(false);
        updateRoles();
    }

    private void updateRoles() {
        String finalUrl = HttpUrl
                .parse(ROLES)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        AdminHttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String rawBody = response.body().string();
                    RolesDTO dto = gson.fromJson(rawBody, RolesDTO.class);
                    List<RoleDTO> roles = dto.getRolesList();
                    allRolesButtons.clear();
                    Platform.runLater(()-> rolesList.getChildren().clear());
                    int i = 0;
                    for (RoleDTO role : roles) {
                        HBox roleAndDelete = new HBox(10);
                        Button button = new Button(role.getRoleName());
                        roleAndDelete.getChildren().add(button);
                        if(!(role.getRoleName().equals(ALL_FLOWS) || role.getRoleName().equals(READ_ONLY))) {
                            Image trashIcon = new Image("/app/resources/img/trash.png");
                            ImageView trashImageView = new ImageView(trashIcon);
                            trashImageView.setTranslateY(8);
                            trashImageView.setFitHeight(20); // Set the desired height of the icon
                            trashImageView.setFitWidth(20);  // Set the desired width of the icon
                            roleAndDelete.getChildren().add(trashImageView);
                            addRemoveRole(trashImageView,role.getRoleName());
                        }
                        roleAndDelete.setAlignment(Pos.TOP_CENTER);
                        allRolesButtons.add(button);
                        int finalI = i;
                        button.setOnAction(e -> handleButtonAction(role,button,dto.getRoleToUser().get(finalI)));
                        Platform.runLater(()-> rolesList.getChildren().add(roleAndDelete));
                        i++;
                    }
                }
            }
        });
    }

    private void handleButtonAction(RoleDTO role, Button button, List<String> usernames) {
        allRolesButtons.stream().forEach(b -> b.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);"));
        button.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");
        roleDetails.setVisible(true);
        roleName.setText("Role name: " + role.getRoleName());
        roleDescription.setText("Role description: " +role.getUserString());
        List<String> rolesFlows = new ArrayList<>();
        for(FlowDefinitionDTO flowPre : role.getFlowsPermissions())
            rolesFlows.add(flowPre.getName());

        ListSelectionView<String> roleFlowsList = new ListSelectionView<>();
        if(role.getRoleName().equals(ALL_FLOWS) || role.getRoleName().equals(READ_ONLY))
            roleFlowsList.setDisable(true);

        Label f1 = new Label("Flows available to add");
        f1.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label f2 = new Label("Role current flows");
        f2.setFont(Font.font("System", FontWeight.BOLD, 14));
        roleFlowsList.setSourceHeader(f1);
        roleFlowsList.setTargetHeader(f2);
        Platform.runLater(() ->{
            flowRoleDefinitionList.getChildren().clear();
            flowRoleDefinitionList.getChildren().addAll(roleFlowsList);
        });
        roleFlowsList.getTargetItems().setAll(rolesFlows);

        List<String> targetList = new ArrayList<>();
        for(FlowDefinitionDTO flow : flowsDefinitions) {
            if(!rolesFlows.contains(flow.getName()))
                targetList.add(flow.getName());
        }
        roleFlowsList.getSourceItems().setAll(targetList);
        roleFlowsList.getTargetItems().addListener((ListChangeListener<String>) change -> {
            updateRolesFlows(role.getRoleName(),roleFlowsList);
        });
        roleUsersList.getItems().clear();
        Label placeholderLabel = new Label("No users in this role");
        roleUsersList.setPlaceholder(placeholderLabel);
        roleUsersList.setItems(FXCollections.observableArrayList(usernames));

    }

    private void updateRolesFlows(String roleName,ListSelectionView<String> roleFlowsList) {
        String finalUrl = HttpUrl
                .parse(ROLES)
                .newBuilder()
                .build()
                .toString();



        Gson gson = new Gson();
        String json = gson.toJson(new UpdateRoleDTO(roleName,roleFlowsList.getTargetItems()));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        AdminHttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //TODO FAILURE
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    updateRoles();
                }
            }
        });
    }


    @Override
    public void setBodyController(AdminBodyController bodyCTRL) {
        bodyForStatisticsController = bodyCTRL;
    }
    @Override
    public void setFlowsDetails(List<FlowDefinitionDTO> flowDTO) {
        flowsDefinitions = flowDTO;
    }
    @FXML
    void openPopup(ActionEvent event)  {
        // Get the reference to the current node
        Window sourceNode = openButton.getScene().getWindow();

        Stage primaryStage = (Stage) sourceNode.getScene().getWindow();

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(primaryStage);
        popupStage.setTitle("Add new role");


        Label addRole = new Label("Role Details:");

        Label roleName = new Label("*Role name:");
        roleName.setStyle("-fx-font-size: 20px;");
        TextField nametextField = new TextField();
        HBox roleNameHBOX = new HBox(10,roleName,nametextField);

        Label roleDetails = new Label("Role description:");
        roleDetails.setStyle("-fx-font-size: 20px;");
        TextField destextField = new TextField();
        HBox roleDesHBOX = new HBox(10,roleDetails,destextField);

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
           addNewRole(nametextField,destextField,saveButton,popupStage);
        });

        VBox popupRoot = new VBox(10,addRole, roleNameHBOX,roleDesHBOX, saveButton);
        popupRoot.setPadding(new Insets(20));

        Scene popupScene = new Scene(popupRoot, 500, 300);
        popupStage.setScene(popupScene);
        popupScene.getStylesheets().addAll(getClass().getResource("/app/resources/main/style.css").toExternalForm());
        popupStage.showAndWait();
    }

    private void addNewRole(TextField nametextField, TextField destextField, Button saveButton, Stage popupStage) {
        String finalUrl = HttpUrl
                .parse(ROLES)
                .newBuilder()
                .build()
                .toString();



        Gson gson = new Gson();
        String json = gson.toJson(new RoleDTO(nametextField.getText().trim(),destextField.getText().trim()));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .put(requestBody)
                .build();

        AdminHttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //TODO FAILURE
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.isSuccessful()) {
                    Platform.runLater(()-> popupStage.close());
                    updateRoles();
                }
                else {
                    Platform.runLater(() -> {
                        showErrorMessage(saveButton, responseBody);
                    });
                }
            }
        });
    }
    private void showErrorMessage(Button button,String message) {
        if (errorPopOver != null && errorPopOver.isShowing()) {
            errorPopOver.hide();
        }

        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorPopOver = new PopOver();
        errorPopOver.setContentNode(new HBox(errorLabel));
        errorPopOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        errorPopOver.setDetached(true);
        errorPopOver.show(button);

        // Hide the popover after 2 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> errorPopOver.hide());
        pause.play();
    }
    private void addRemoveRole(ImageView trash,String roleName) {
            Tooltip tooltip = new Tooltip();
            tooltip.setStyle("-fx-font-size: 12px;");
            trash.setOnMouseEntered(event -> showDeleteRole(tooltip,trash,event));
            trash.setOnMouseExited(event -> {
                tooltip.hide();
            });
        trash.setOnMouseClicked(event -> removeRole(roleName,trash));
    }
    private void showDeleteRole(Tooltip tooltip, ImageView data, MouseEvent event) {
        tooltip.setText("Delete Role");
        tooltip.show(data, event.getScreenX() + 5, event.getScreenY() + 5);
    }
    private void removeRole(String roleNamer, ImageView trash) {
        String finalUrl = HttpUrl
                .parse(ROLES)
                .newBuilder()
                .build()
                .toString();



        Gson gson = new Gson();
        String json = gson.toJson(new UpdateRoleDTO(roleNamer,null));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .delete(requestBody)
                .build();

        AdminHttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if(roleName.getText().equals("Role name: " + roleNamer))
                        Platform.runLater(() -> roleDetails.setVisible(false));
                    updateRoles();
                }else{
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        showErrorMessage(trash, responseBody);
                    });
                }
            }
        });
    }
    private void showErrorMessage(Node button, String message) {
        if (errorPopOver != null && errorPopOver.isShowing()) {
            errorPopOver.hide();
        }

        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorPopOver = new PopOver();
        errorPopOver.setTitle("Error");
        errorPopOver.setContentNode(new HBox(errorLabel));
        errorPopOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        errorPopOver.setDetached(true);
        errorPopOver.show(button);

        // Hide the popover after 2 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> errorPopOver.hide());
        pause.play();
    }

}
