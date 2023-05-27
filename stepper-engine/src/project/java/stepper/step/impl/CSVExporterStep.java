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

public class CSVExporterStep extends AbstractStepDefinition {
    public CSVExporterStep() {
        super("CSV Exporter", true);

        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "CSV export result", DataDefinitionRegistry.STRING));
    }
    @Override
    public synchronized StepResult invoke(StepExecutionContext context) throws NoStepInput {
        RelationData table = context.getDataValue("SOURCE", RelationData.class);
        String output = "";
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        StepResult res = StepResult.SUCCESS;
        if(table.getRowsSize() == 0) {
            res = StepResult.WARNING;
            logs.addLogLine("No data in the table");
            context.addStepSummaryLine("No data in the table");
        }
        else {
            logs.addLogLine("About to process " + table.getRowsSize() + " lines of data");

            for(String col : table.getColumns())
                output += col + ",";

            output = output.substring(0,output.length()-1);//Remove the last comma
            output += "\n";

            for (int i = 0; i < table.getRowsSize(); i++) {
                for (String data : table.getRowDataByColumnsOrder(i))
                    output += data + ",";

                output = output.substring(0,output.length()-1);//Remove the last comma
                output += "\n";
            }
            output = output.substring(0,output.length()-1);//Remove last \n
            logs.addLogLine("finish with exporting the data");
        }
        context.storeDataValue("RESULT", output);
        context.addStepLog(logs);
        return res;
    }
}
