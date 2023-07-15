package app.resources.header;

import app.resources.util.http.HttpClientUtil;
import com.google.gson.Gson;
import dto.HeaderDetails;
import javafx.application.Platform;
import javafx.scene.control.Label;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.TimerTask;

import static app.resources.util.Constants.USER_HEAD_DETAILS;

public class HeaderDetailsRefresher extends TimerTask {
    private final Label isManager;
    private final Label userRoles;

    public HeaderDetailsRefresher(Label isManager,Label userRoles){
        this.isManager = isManager;
        this.userRoles = userRoles;
    }

    @Override
    public void run() {
        String finalUrl = HttpUrl
                .parse(USER_HEAD_DETAILS)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->{
                        isManager.setText("NaN");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String rawBody = response.body().string();
                    HeaderDetails userDetails = gson.fromJson(rawBody, HeaderDetails.class);
                    Platform.runLater(() ->{
                        isManager.setText("Is manager: " + (userDetails.getisManager() ? "Yes" : "No"));
                        userRoles.setText("Roles: " + (userDetails.getuserRoles().size() > 0 ? userDetails.getUserRoleToPrint() : "No roles"));
                    });
                }
            }
        });

    }


}
