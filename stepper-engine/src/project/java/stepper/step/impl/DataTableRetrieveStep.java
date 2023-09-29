package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataTableRetrieveStep extends AbstractStepDefinition {
    public DataTableRetrieveStep() {
        super("Data Table Retrieve", true);

        addInput(new DataDefinitionDeclarationImpl("TABLE_NAME", DataNecessity.MANDATORY, "Table name to retrieve data", DataDefinitionRegistry.STRING, UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.MANDATORY, "Filter only this id's", DataDefinitionRegistry.STRING,UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("COLUMN_TARGET", DataNecessity.MANDATORY, "Column name to get value", DataDefinitionRegistry.STRING,UIDDPresent.NA));

        addOutput(new DataDefinitionDeclarationImpl("VALUE", DataNecessity.NA, "Expected Value", DataDefinitionRegistry.STRING, UIDDPresent.NA));
    }
    @Override
    public StepResult invoke(StepExecutionContext context) throws NoStepInput {
        String tableName = context.getDataValue("TABLE_NAME", String.class);
        String columnTargetName = context.getDataValue("COLUMN_TARGET", String.class);
        String filter = context.getDataValue("FILTER", String.class);
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        String value = "null";
        StepResult res;
        String sql = "SELECT * FROM " + tableName + " WHERE id = '" + filter + "'";
        try (PreparedStatement preparedStatement = SQLDataApi.CONNECTION.prepareStatement(sql)) {
            logs.addLogLine("Executing Query: " + sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                value = resultSet.getString(columnTargetName);
                logs.addLogLine("Data successfully retrieved");
                if(value != null){
                    context.addStepSummaryLine("Step finish with retrieve the data successfully from column '" + columnTargetName + "' inside the table '" + tableName + "'");
                    res = StepResult.SUCCESS;
                } else{
                    context.addStepSummaryLine("Step finish - no content in the column '" + columnTargetName + "' inside the table '" + tableName + "'");
                    res = StepResult.WARNING;
                }
            }
            else{
                logs.addLogLine("STEP FAILURE: Failed due that have no content in the table '" + tableName + "'");
                context.addStepSummaryLine("Step Failure - no content inside the table '" + tableName + "'");
                res = StepResult.FAILURE;
            }
            context.storeDataValue("VALUE", value);
            context.addStepLog(logs);
            return res;
        } catch (SQLException e) {
            logs.addLogLine("STEP FAILURE: Failed to executing query: " + e.getMessage());
            logs.addLogLine("STEP FAILURE: Failed to executing query: " + sql);
            context.addStepSummaryLine("STEP FAILURE: Failed to executing query: " + sql);
            context.addStepLog(logs);
            return StepResult.FAILURE;
        }
    }

}
