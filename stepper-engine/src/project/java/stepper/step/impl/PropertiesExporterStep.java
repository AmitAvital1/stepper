package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;

import java.util.List;

public class PropertiesExporterStep extends AbstractStepDefinition {
    public PropertiesExporterStep() {
        super("Properties Exporter", true);

        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Properties export result", DataDefinitionRegistry.STRING));
    }
    @Override
    public StepResult invoke(StepExecutionContext context) {
        RelationData table = context.getDataValue("SOURCE", RelationData.class);
        String output = "";
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res = StepResult.SUCCESS;
        int propertiesCounter = 1;
        if(table.getRowsSize() == 0) {
            res = StepResult.WARNING;
            logs.addLogLine("No data in the table");
            context.addStepSummaryLine("No data in the table");
        }
        else {
            logs.addLogLine("About to process " + table.getRowsSize() + " lines of data");


            for (int i = 0; i < table.getColumns().size(); i++) {
                List<String> eachColumn = table.getColumnsData(i);
                for(int j =0; j < eachColumn.size(); j++){
                    output += propertiesCounter + "." + table.getColumns().get(i) + "=" + eachColumn.get(j);
                    if(i != table.getColumns().size()-1 || j != eachColumn.size() - 1)
                        output += "\n";
                    propertiesCounter++;
                }
            }
            logs.addLogLine("Extracted total of " + (propertiesCounter - 1));
            context.addStepSummaryLine("Extracted total of " + (propertiesCounter - 1));

        }
        context.storeDataValue("RESULT", output);
        context.addStepLog(logs);
        return res;
    }
}
