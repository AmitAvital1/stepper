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
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.OPTIONAL, "Filter only this id's", DataDefinitionRegistry.STRING,UIDDPresent.NA));

        addOutput(new DataDefinitionDeclarationImpl("DATA", DataNecessity.NA, "Data table", DataDefinitionRegistry.RELATION, UIDDPresent.NA));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", DataNecessity.NA, "Total rows", DataDefinitionRegistry.INTEGER,UIDDPresent.NA));
    }


    @Override
    public StepResult invoke(StepExecutionContext context) throws NoStepInput {
        String tableName = context.getDataValue("TABLE_NAME", String.class);
        Optional<String> maybeFilter = Optional.ofNullable(context.getDataValue("FILTER", String.class));
        StepLogs logs = new StepLogs(context.getCurrentWorkingStep().getFinalStepName());
        String filter = maybeFilter.orElse(null); // "" says not filter
        RelationData dataRelation;
        List<String> columnNames = new ArrayList<>();

        String sql = "SELECT * FROM " + tableName + (filter != null ? (" WHERE id = '" + filter + "'") : "");
        try (PreparedStatement preparedStatement = SQLDataApi.CONNECTION.prepareStatement(sql)) {
            logs.addLogLine("Executing Query: " + sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            logs.addLogLine("Fetching data from table..");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            for(int i = 1; i <= columnCount; i++)
                columnNames.add(metaData.getColumnName(i));

            dataRelation = new RelationData(columnNames.toArray(new String[0]));

            List<String> datas = new ArrayList<>();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    datas.add( resultSet.getString(i));
                }
                dataRelation.addRow(datas.toArray(datas.toArray(new String[0])));
                datas.clear();
            }
            context.storeDataValue("DATA", dataRelation);
            context.storeDataValue("TOTAL_FOUND", dataRelation.getRowsSize());
            logs.addLogLine("Found " + dataRelation.getRowsSize() + " Rows");
            context.addStepLog(logs);
            if(columnCount == 0)
            {
                context.addStepSummaryLine("Step finish with no rows retrieved");
                return StepResult.WARNING;
            }else{
                context.addStepSummaryLine("Finish to retrieved all " + dataRelation.getRowsSize() + " rows");
                return StepResult.SUCCESS;
            }
        } catch (SQLException e) {
            logs.addLogLine("STEP FAILURE: Failed to executing query: " + e.getMessage());
            logs.addLogLine("STEP FAILURE: Failed to executing query: " + sql);
            context.addStepSummaryLine("STEP FAILURE: Failed to executing query: " + sql);
            context.addStepLog(logs);
            return StepResult.FAILURE;
        }
    }
}
