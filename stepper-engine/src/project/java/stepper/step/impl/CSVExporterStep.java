package project.java.stepper.step.impl;

import org.w3c.dom.ranges.Range;
import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.file.FileData;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.AbstractStepDefinition;
import project.java.stepper.step.api.DataDefinitionDeclarationImpl;
import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.StepResult;

import java.util.List;

public class CSVExporterStep extends AbstractStepDefinition {
    public CSVExporterStep() {
        super("CSV Exporter", true);

        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "CSV export result", DataDefinitionRegistry.STRING));
    }
    @Override
    public StepResult invoke(StepExecutionContext context) {
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


            for (int i = 0; i < table.getRowsSize(); i++) {
                for (String data : table.getRowDataByColumnsOrder(i))
                    output = output + "," + data;
                output = output + "\n";
            }

        }
        context.storeDataValue("RESULT", output);
        context.addStepLog(logs);
        return res;
    }
}
