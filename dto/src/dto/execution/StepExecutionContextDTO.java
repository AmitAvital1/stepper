package dto.execution;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.context.StepExecutionContextImpl;
import project.java.stepper.flow.execution.context.logs.StepLogs;

import java.util.List;
import java.util.Map;

public class StepExecutionContextDTO {
    private final Map<String, Object> dataValues;
    private StepUsageDeclaration currentWorkingStep;
    private List<StepLogs> flowLogs;
    private List<String> stepSummaryLine;
    private final List<FlowExecution.flowOutputsData> outputsData;
    private List<StepExecutionContextImpl.stepData> flowStepsData;
    private ListProperty<StepExecutionContextImpl.stepData> flowStepsDataProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
}
