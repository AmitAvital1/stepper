package app.resources.body.flowdefinition;

import app.resources.util.http.AdminHttpClientUtil;
import com.google.gson.Gson;
import dto.HeaderDetails;
import dto.users.UpdateManagerDTO;
import dto.users.UserDTO;
import dto.users.UsersDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;

import static app.resources.util.AdminConstants.USERS;
import static app.resources.util.AdminConstants.USER_HEAD_DETAILS;


public class UsersRefresher extends TimerTask {

    private final List<UserDTO> users;
    private final ListView<UserDTO> usersList;
    private final UserManagementController controller;

    public UsersRefresher(List<UserDTO> users,ListView<UserDTO> usersList,UserManagementController controller){
        this.users = users;
        this.usersList = usersList;
        this.controller = controller;
    }

    @Override
    public void run() {

        System.out.println("Hi");

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
                    List<UserDTO> tempUsers = dto.getUsers();
                    for (int i = 0; i < tempUsers.size(); i++) {
                        if (i < users.size()) {
                            users.get(i).setOnline(tempUsers.get(i).isOnline());
                        } else
                            users.add(tempUsers.get(i));
                    }
                    Platform.runLater(() -> {
                        usersList.setItems(FXCollections.observableArrayList(users));
                        controller.addUsersToList(dto.getAllRoles());
                    });

                }
            }
        });
    }
}
