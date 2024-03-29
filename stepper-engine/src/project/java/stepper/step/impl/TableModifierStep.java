package project.java.stepper.step.impl;

import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.SqlFilter.SqlFilter;
import project.java.stepper.exceptions.NoStepInput;
import project.java.stepper.flow.execution.context.StepExecutionContext;
import project.java.stepper.flow.execution.context.logs.StepLogs;
import project.java.stepper.step.api.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableModifierStep extends AbstractStepDefinition {
    public TableModifierStep() {
        super("Table Modifier", false);

        addInput(new DataDefinitionDeclarationImpl("TABLE_NAME", DataNecessity.MANDATORY, "Table name to retrieve data", DataDefinitionRegistry.STRING, UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("COLUMN_TARGET", DataNecessity.MANDATORY, "Column name to change", DataDefinitionRegistry.STRING,UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("VALUE", DataNecessity.MANDATORY, "New value data to insert", DataDefinitionRegistry.STRING,UIDDPresent.NA));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.MANDATORY, "Filter options", DataDefinitionRegistry.SQLFILTER,UIDDPresent.SQL_FILTER));

        addOutput(new DataDefinitionDeclarationImpl("OLD_VALUE", DataNecessity.NA, "Old data", DataDefinitionRegistry.STRING, UIDDPresent.NA));
        //addOutput(new DataDefinitionDeclarationImpl("DATA", DataNecessity.NA, "Data table", DataDefinitionRegistry.RELATION, UIDDPresent.NA));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL", DataNecessity.NA, "Total rows changed", DataDefinitionRegistry.INTEGER,UIDDPresent.NA));
    }
    @Override
    public StepResult invoke(StepExecutionContext context) throws NoStepInput {
        String tableName = context.getDataValue("TABLE_NAME", String.class);
        String columnTargetName = context.getDataValue("COLUMN_TARGET", String.class);
        String newValue = context.getDataValue("VALUE", String.class);
        Optional<SqlFilter> maybeFilter = Optional.ofNullable(context.getDataValue("FILTER", SqlFilter.class));
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        SqlFilter filter = maybeFilter.orElse(null); // "" says not filter
        Integer rowsAffected;
        List<String> columnNames = new ArrayList<>();
        String updateQuery = "UPDATE " + tableName + " SET " + columnTargetName + " = ?" + (filter != null ? filter.toSql() : "");
        try (PreparedStatement preparedStatement = SQLDataApi.CONNECTION.prepareStatement(updateQuery)) {
            if(!verifyTableWithOneRow(tableName, columnTargetName, filter, context)){
                logs.addLogLine("STEP FAILURE: Multiple rows has been found.");
                context.addStepSummaryLine("STEP FAILURE: Multiple rows has been found.");
                context.addStepLog(logs);
                return StepResult.FAILURE;
            }
            preparedStatement.setString(1, newValue);
            logs.addLogLine("Executing Query: " + preparedStatement.toString());
            rowsAffected = preparedStatement.executeUpdate();
            logs.addLogLine("Updated " + rowsAffected + " rows");
            context.storeDataValue("TOTAL", rowsAffected);
            context.addStepLog(logs);
            if(rowsAffected == 0)
            {
                context.addStepSummaryLine("STEP FAILURE: Step finish with no rows affected");
                return StepResult.FAILURE;
            }else{
                context.addStepSummaryLine("Finish to update all " + rowsAffected + " rows");
                return StepResult.SUCCESS;
            }
        } catch (SQLException e) {
            logs.addLogLine("STEP FAILURE: Failed to executing query: " + e.getMessage());
            context.addStepSummaryLine("STEP FAILURE: Failed to executing query: " + updateQuery);
            context.addStepLog(logs);
            return StepResult.FAILURE;
        }
    }

    private boolean verifyTableWithOneRow(String tableName,String columnTargetName, SqlFilter filter, StepExecutionContext context) throws SQLException {
        String sql = "SELECT * FROM " + tableName + (filter != null ? filter.toSql() : "");
        try (PreparedStatement preparedStatement = SQLDataApi.CONNECTION.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            int rowsNum = 0;
            String oldValue = null;
            while (resultSet.next()) {
                rowsNum++;
                if(rowsNum > 1)
                    return false;
                oldValue = resultSet.getString(columnTargetName);
            }
            if(rowsNum == 1){
                context.storeDataValue("OLD_VALUE", oldValue);
            }
            return true;
        }
    }
}
