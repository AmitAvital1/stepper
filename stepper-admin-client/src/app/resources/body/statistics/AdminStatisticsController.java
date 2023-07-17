package app.resources.body.statistics;

import app.resources.body.AdminBodyController;
import app.resources.body.AdminBodyControllerDefinition;
import app.resources.util.http.AdminHttpClientUtil;
import com.google.gson.Gson;
import dto.FlowDefinitionDTO;
import dto.StepUsageDeclarationImplDTO;
import dto.execution.FlowStatisticsDTO;
import dto.execution.FlowsStatisticsDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.statistics.FlowStats;


import java.io.IOException;
import java.util.List;

import static app.resources.util.AdminConstants.STATISTICS;

public class AdminStatisticsController implements AdminBodyControllerDefinition {
    private AdminBodyController bodyForStatisticsController;
    @FXML
    private BarChart<String, Number> flowsBarChart;
    @FXML
    private NumberAxis numberAxis;
    @FXML
    private CategoryAxis categoryAxis;
    @FXML
    private HBox flowStepsDetails;

    @FXML
    private GridPane stepGridStats;

    @FXML
    private PieChart stepsPie;

    @Override
    public void show() {
        getStatisticsReq();
        flowStepsDetails.setVisible(false);

    }

    private void showStepDetails(FlowStatisticsDTO flowStats, List<StepUsageDeclarationImplDTO> flowSteps) {
        flowStepsDetails.setVisible(true);
        Node child1 = stepGridStats.getChildren().get(0);
        child1.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        Node child2 = stepGridStats.getChildren().get(1);
        child2.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        Node child3 = stepGridStats.getChildren().get(2);
        child3.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        stepGridStats.getChildren().clear();
        stepGridStats.getChildren().addAll(child1,child2,child3);
        stepsPie.getData().clear();
        int i = 1;
        for(StepUsageDeclarationImplDTO step : flowSteps){
            Integer stepTimesExecuted = flowStats.getStepTimesExecuted(step);
            String avg = (stepTimesExecuted > 0 ? (flowStats.getStepAverageTimeExecuted(step) + ".ms") : "NA");
            stepGridStats.addRow(i,new Label(step.getFinalStepName()),new Label(stepTimesExecuted.toString()),new Label(avg));
//            String stepDetails = step.getFinalStepName() + "\nNumbers of executed times: " + stepTimesExecuted +
//                    "\nAverage execution time: " + (stepTimesExecuted > 0 ? (flowStats.getStepAverageTimeExecuted(step) + ".ms") : "NA");
            // Label stepDet = new Label(stepDetails);
            // stepVboxStats.getChildren().add(stepDet);
            PieChart.Data pie = new PieChart.Data(step.getFinalStepName(), stepTimesExecuted);
            stepsPie.getData().add(pie);
            i++;
        }
        for (PieChart.Data data : stepsPie.getData()) {
            Tooltip tooltip = new Tooltip();
            data.getNode().setOnMouseEntered(event -> {
                data.getNode().setStyle("-fx-border-color: #7777ff; -fx-border-width: 1px;");
                tooltip.setText(data.getPieValue() + "");
                tooltip.show(data.getNode(), event.getScreenX() + 5, event.getScreenY() + 5);
            });
            data.getNode().setOnMouseExited(event -> {
                tooltip.hide();
                data.getNode().setStyle("-fx-border-color: transparent;");

            });
        }
    }

    private void showAvg(Tooltip tooltip, long avgexecutesRunTimes, XYChart.Data<String, Number> data, MouseEvent event) {
        tooltip.setText("Average execution time: " + avgexecutesRunTimes+ ".ms");
        tooltip.show(data.getNode(), event.getScreenX() + 10, event.getScreenY() + 10);
        data.getNode().setStyle("-fx-border-color: #7777ff; -fx-border-width: 2px;");
    }

    @Override
    public void setBodyController(AdminBodyController bodyCTRL) {
        bodyForStatisticsController = bodyCTRL;
    }

    @Override
    public void setFlowsDetails(List<FlowDefinition> flow, List<FlowDefinitionDTO> flowDTO) {

    }

    private void getStatisticsReq() {
        String finalUrl = HttpUrl
                .parse(STATISTICS)
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
                    FlowsStatisticsDTO dto = gson.fromJson(rawBody, FlowsStatisticsDTO.class);
                    List<FlowStatisticsDTO> flowsStats = dto.getFlowStatisticsDTOS();

                    // Create the data series
                    XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();
                    for(FlowStatisticsDTO cFlow : flowsStats){
                        XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(cFlow.getFlowName(), cFlow.getExecutesRunTimes());
                        dataSeries.getData().add(dataPoint);
                    }
                    // Add the data series to the chart
                    ObservableList<XYChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
                    chartData.add(dataSeries);

                    Platform.runLater(() -> {
                        categoryAxis.setAnimated(false);
                        flowsBarChart.setData(chartData);
                        int i = 0;
                        for (XYChart.Data<String, Number> data : dataSeries.getData()) {
                            FlowStatisticsDTO flowStats = flowsStats.get(i);
                            Tooltip tooltip = new Tooltip();
                            tooltip.setStyle("-fx-font-size: 12px;");
                            data.getNode().setOnMouseEntered(event -> showAvg(tooltip,flowStats.getAvgExecutesRunTimes(),data,event));
                            data.getNode().setOnMouseExited(event -> {
                                tooltip.hide();
                                data.getNode().setStyle("-fx-border-color: transparent;");
                            });
                            data.getNode().setOnMouseClicked(event -> showStepDetails(flowStats,flowStats.getFlowSteps()));
                            i++;
                        }
                    });
                }
            }
        });
    }

}

