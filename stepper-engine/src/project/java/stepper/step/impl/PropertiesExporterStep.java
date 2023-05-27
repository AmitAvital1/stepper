package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.exceptions.NoStepInput;
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
    public synchronized StepResult invoke(StepExecutionContext context) throws NoStepInput {
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

            List<String> colsData = table.getColumns();
            for (int i = 0; i < table.getRowsSize(); i++) {
                List<String> eachRow = table.getRowDataByColumnsOrder(i);
                output += "row-" + (i+1) + ".";
                for(int j = 0; j < eachRow.size(); j++){
                    output += colsData.get(j) + "=" + eachRow.get(j) + ",";
                    propertiesCounter++;
                }
                output = output.substring(0,output.length()-1);
                output += "\n";
            }
            output = output.substring(0,output.length()-1);
            logs.addLogLine("Extracted total of " + (propertiesCounter - 1));
            context.addStepSummaryLine("Extracted total of " + (propertiesCounter - 1));

        }
        context.storeDataValue("RESULT", output);
        context.addStepLog(logs);
        return res;
    }
}
