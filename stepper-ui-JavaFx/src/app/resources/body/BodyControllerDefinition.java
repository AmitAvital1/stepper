package app.resources.body;

import project.java.stepper.flow.definition.api.FlowDefinition;

import java.util.List;

public interface BodyControllerDefinition {
    void setFlowsDetails(List<FlowDefinition> flow);
    void show();
    void setBodyController(BodyController bodyCTRL);
}
