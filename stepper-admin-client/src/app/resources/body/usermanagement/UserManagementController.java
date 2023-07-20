package app.resources.body.usermanagement;

import app.resources.body.AdminBodyController;
import app.resources.body.AdminBodyControllerDefinition;
import app.resources.util.http.AdminHttpClientUtil;
import com.google.gson.Gson;
import dto.FlowDefinitionDTO;
import dto.roles.RoleDTO;
import dto.users.UpdateManagerDTO;
import dto.users.UserDTO;
import dto.users.UsersDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import okhttp3.*;
import org.controlsfx.control.ListSelectionView;
import org.jetbrains.annotations.NotNull;
import project.java.stepper.flow.definition.api.FlowDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static app.resources.util.AdminConstants.*;

public class UserManagementController implements AdminBodyControllerDefinition {
    @FXML private Parent bodyForFlowDefinition;
    @FXML private AdminBodyController bodyForFlowDefinitionController;

    @FXML
    private ListView<UserDTO> usersList;

    @FXML
    private Label userNameLabel;

    @FXML
    private StackPane userRolesStack;

    @FXML
    private ListView<String> userAvailableFlowsList;

    @FXML
    private VBox userDetailsVBOX;

    @FXML
    private CheckBox isManagerCheckBox;

    private int lastSelectedIndex = -1;

    private Timer timer;

    private UserManagementController userManagementController = this;



    @Override
    public void show() {
        userDetailsVBOX.setVisible(false);

        Label placeholderLabel = new Label("There are no flows permissions to this user");
        userAvailableFlowsList.setPlaceholder(placeholderLabel);

        Label placeholderLabel2 = new Label("There are no users sing to stepper yet");
        usersList.setPlaceholder(placeholderLabel2);

        geAllUsers();
    }

    private void geAllUsers() {
        String finalUrl = HttpUrl
                .parse(USERS)
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
                    UsersDTO dto = gson.fromJson(rawBody, UsersDTO.class);
                    List<UserDTO> users = dto.getUsers();
                    usersList.setItems(FXCollections.observableArrayList(users));
                    Platform.runLater(()-> {
                        addUsersToList(dto.getAllRoles());
                    });
                    usersList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        lastSelectedIndex = usersList.getSelectionModel().getSelectedIndex();
                    });
                    UsersRefresher usersRefresher = new UsersRefresher(users,usersList,userManagementController);
                    timer = new Timer();
                    timer.schedule(usersRefresher, 0, 1000);
                    bodyForFlowDefinitionController.setTimer(timer);
                }
            }
        });
    }
    public void addUsersToList(List<RoleDTO> allRoles) {
        usersList.setCellFactory(param -> new ListCell<UserDTO>() {
            @Override
            protected void updateItem(UserDTO user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setText(null);
                } else {
                    setText(user.getUsername() + " (" + (user.isOnline() ? "Online)" : "Offline)"));
                }
            }
        });
        usersList.setOnMouseClicked(event -> {
            UserDTO selectedItem = usersList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                handleItemSelectedList(selectedItem,allRoles);
            }
        });
    }

    private void handleItemSelectedList(UserDTO user, List<RoleDTO> allRoles) {
        userDetailsVBOX.setVisible(true);
        userNameLabel.setText(user.getUsername());
        addFlowsUserList(user,allRoles);
        addRolesSelectionList(user, allRoles);
        if(user.isManager())
            isManagerCheckBox.setSelected(true);
        else
            isManagerCheckBox.setSelected(false);

        isManagerCheckBox.setOnAction(event -> {
            updateUserManager(user.getUsername(),isManagerCheckBox.isSelected());
            user.setManager(isManagerCheckBox.isSelected());
            addFlowsUserList(user,allRoles);
        });


    }

    private void updateUserManager(String username, boolean selected) {
        String finalUrl = HttpUrl
                .parse(USERS)
                .newBuilder()
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new UpdateManagerDTO(username,selected));
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
                if (response.isSuccessful()) {

                }
            }
        });
    }

    private void addRolesSelectionList(UserDTO user, List<RoleDTO> allRoles) {
        List<String> userRoles = new ArrayList<>();
        List<String> userAvailableRoles = new ArrayList<>();
        user.getRoles().forEach(role -> userRoles.add(role.getRoleName())); //Make user current roles
        allRoles.forEach(role -> {
            if(!userRoles.contains(role.getRoleName()))
                userAvailableRoles.add(role.getRoleName());
        });


        ListSelectionView<String> userRolesSelection = new ListSelectionView<>();
        Label f1 = new Label("Roles available to add");
        f1.setFont(Font.font("System", FontWeight.BOLD, 14));
        Label f2 = new Label("User current roles");
        f2.setFont(Font.font("System", FontWeight.BOLD, 14));
        userRolesSelection.setSourceHeader(f1);
        userRolesSelection.setTargetHeader(f2);

        userRolesSelection.getTargetItems().setAll(userRoles);
        userRolesSelection.getSourceItems().setAll(userAvailableRoles);

        userRolesSelection.getTargetItems().addListener((ListChangeListener<String>) change -> {
            updateUserRoles(user, allRoles,userRolesSelection);
        });
        userRolesStack.getChildren().clear();
        userRolesStack.getChildren().add(userRolesSelection);
    }

    private void updateUserRoles(UserDTO user, List<RoleDTO> allRoles, ListSelectionView<String> userRolesSelection) {
        List<RoleDTO> newUserRoles = new ArrayList<>();
        List<String> userCurrentRoles = userRolesSelection.getTargetItems();
        allRoles.forEach(r -> {
            if(userCurrentRoles.contains(r.getRoleName()))
                newUserRoles.add(r);
        });
        user.setRoles(newUserRoles);
        addFlowsUserList(user,allRoles);

        String finalUrl = HttpUrl
                .parse(USERS)
                .newBuilder()
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(user);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        AdminHttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {

                }
            }
        });

    }

    private void addFlowsUserList(UserDTO user,List<RoleDTO> allRoles) {
        userAvailableFlowsList.getItems().clear();
        List<String> userFlows = new ArrayList<>();
        if(user.isManager())
            allRoles.stream().filter(r -> r.getRoleName().equals(ALL_FLOWS)).findFirst().get().getFlowsPermissions().stream().forEach(f-> userFlows.add(f.getName()));
        else{
        for(RoleDTO role : user.getRoles()) {
            role.getFlowsPermissions().stream().forEach(f -> {
                if (!userFlows.contains(f.getName()))
                    userFlows.add(f.getName());
            });
        }
        }
        userAvailableFlowsList.getItems().addAll(userFlows);
        userAvailableFlowsList.refresh();
    }

    @Override
    public void setFlowsDetails(List<FlowDefinitionDTO> flowDTO) {

    }

    @Override
    public void setBodyController(AdminBodyController bodyCTRL) {
        bodyForFlowDefinitionController = bodyCTRL;
    }
}
