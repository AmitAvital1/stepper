package app.resources.body.statistics;

import app.resources.body.AdminBodyController;
import app.resources.body.AdminBodyControllerDefinition;
import dto.FlowDefinitionDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.statistics.FlowStats;


import java.util.List;

public class AdminStatisticsController implements AdminBodyControllerDefinition {
    private AdminBodyController bodyForStatisticsController;
    private List<FlowDefinition> flows;
    private List<FlowExecution> flowExecutions;

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
        flowExecutions = bodyForStatisticsController.getFlowExecutions();
        flowStepsDetails.setVisible(false);
        // Create the data series
        XYChart.Series<String, Number> dataSeries = new XYChart.Series<>();

        for(FlowDefinition cFlow : flows){
            FlowStats flowStats = cFlow.getFlowStatistics();
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(cFlow.getName(), flowStats.getExecutesRunTimes());
            dataSeries.getData().add(dataPoint);
            Tooltip tooltip = new Tooltip();
            tooltip.setStyle("-fx-font-size: 12px;");

            flowStats.executesRunTimesPropProperty().addListener((observable, oldValue, newValue) -> {
                dataPoint.setYValue(newValue);
            });
        }

        // Add the data series to the chart
        ObservableList<XYChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        chartData.add(dataSeries);
        flowsBarChart.setData(chartData);
        int i = 0;
        for (XYChart.Data<String, Number> data : dataSeries.getData()) {
            FlowStats flowStats = flows.get(i).getFlowStatistics();
            FlowDefinition flow = flows.get(i);
            Tooltip tooltip = new Tooltip();
            tooltip.setStyle("-fx-font-size: 12px;");
            data.getNode().setOnMouseEntered(event -> showAvg(tooltip,flowStats.getAvgExecutesRunTimes(),data,event));
            data.getNode().setOnMouseExited(event -> {
                tooltip.hide();
                data.getNode().setStyle("-fx-border-color: transparent;");
            });
            data.getNode().setOnMouseClicked(event -> showStepDetails(flowStats,flow.getFlowSteps()));
            i++;
        }

    }

    private void showStepDetails(FlowStats flowStats, List<StepUsageDeclaration> flowSteps) {
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
        for(StepUsageDeclaration step : flowSteps){
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
    public void setFlowsDetails(List<FlowDefinition> flow,List<FlowDefinitionDTO> flowDTO) {
        flows = flow;
    }

}

