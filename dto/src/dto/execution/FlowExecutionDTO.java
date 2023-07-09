package dto.execution;

import dto.FlowDefinitionDTO;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.FlowExecutionResult;
import project.java.stepper.flow.execution.context.StepExecutionContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlowExecutionDTO {
    private final String uniqueId;
    private final FlowDefinitionDTO flowDefinition;
    private StepExecutionContext flowContexts;
    private Duration totalTime;
    private String startedTime;
    private ObjectProperty<FlowExecutionResult> flowExecutionResult = new SimpleObjectProperty<>();
    private Map<String, Object> startersFreeInputForContext;
    private Map<String, Object> allDataValues;
    private List<FlowExecution.flowOutputsData> outputsStepData = new ArrayList<>();
    private IntegerProperty stepFinished = new SimpleIntegerProperty(0);


}
