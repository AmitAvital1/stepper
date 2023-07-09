package app.resources.main;

import app.resources.util.http.HttpClientUtil;
import com.google.gson.Gson;
import dto.FlowDefinitionDTO;
import dto.HeaderDetails;
import dto.StepperDTO;
import javafx.application.Platform;
import javafx.scene.control.Label;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import project.java.stepper.flow.definition.api.FlowDefinition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import static app.resources.util.Constants.FLOW_DEFINITION;
import static app.resources.util.Constants.USER_HEAD_DETAILS;

public class FlowDefinitionRefresher extends TimerTask {
    private AppMainConroller mainController;

    public FlowDefinitionRefresher(AppMainConroller mainController){
        this.mainController = mainController;
    }

    @Override
    public void run() {

        String finalUrl = HttpUrl
                .parse(FLOW_DEFINITION)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    mainController.addFlows(new ArrayList<>(),new ArrayList<>());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String rawBody = response.body().string();
                    StepperDTO stepper = gson.fromJson(rawBody, StepperDTO.class);
                  //  System.out.println(stepper.getFlows().get(0).getDescription());
                    mainController.addFlows(new ArrayList<>(),stepper.getFlows());
                }
            }
        });

    }
}
