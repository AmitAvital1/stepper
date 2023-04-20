package project.java.stepper.dd.impl.relation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelationData {

    private List<String> columns;
    private List<SingleRow> rows;

    public RelationData() {
        this.columns = new ArrayList<>();
        rows = new ArrayList<>();
    }

    public RelationData(List<String> columns) {
        this.columns = columns;
        rows = new ArrayList<>();
    }

public void addData(String column,String data){
        if(!columns.contains(column))
            columns.add(column);
        boolean addedFlag = false;
        for(SingleRow row : rows) {
            if(!row.checkSpace(column)) {
                row.addData(column, data);
                addedFlag = true;
                break;
            }

        }
        if(!addedFlag){
            rows.add(new SingleRow());
            rows.get(rows.size() -1).addData(column, data);
        }
    }


    public List<String> getRowDataByColumnsOrder(int rowId) {
        List<String> row = new ArrayList<>();
        for(String colum : columns)
        {
            row.add(rows.get(rowId).getRowDataByColumn(colum));
        }
        return row;
    }

    public List<String> getColumns(){return columns;}
    public int getRowsSize(){return rows.size();}

    private static class SingleRow {
        private Map<String, String> data;

        public SingleRow() {
            data = new HashMap<>();
        }

        public String getRowDataByColumn(String column){return data.get(column);}

        public boolean checkSpace(String column){return data.containsKey(column);}

        public void addData(String columnName, String value) {
            data.put(columnName, value);
        }
    }
}
