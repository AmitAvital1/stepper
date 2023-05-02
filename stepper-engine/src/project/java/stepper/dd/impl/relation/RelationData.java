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

    @Override
    public String toString() {
        String res = "Cols:[";
        for(String str : columns)
            res+= str + ",";
        res = res.substring(0,res.length()-1);
        res += "], Nums of rows:" + rows.size();
        return res;
    }

    public RelationData(String[] columns) {
        this.columns = new ArrayList<>();
        for(String str : columns)
            this.columns.add(str);

        rows = new ArrayList<>();
    }

public void addRow(String... datas) {

    if (datas.length > columns.size())
        return; //Not match arguments

    SingleRow newRow = new SingleRow();
    final int sizeOfDatas = datas.length;
    for (int i = 0; i < sizeOfDatas; i++) {
        newRow.addData(columns.get(i), datas[i]);
    }
    rows.add(newRow);
}


    public List<String> getRowDataByColumnsOrder(int rowId) {
        List<String> row = new ArrayList<>();
        for(String colum : columns)
        {
            String str = rows.get(rowId).getRowDataByColumn(colum);
            if(str != null)
                row.add(str);
            else
                row.add("NA");
        }
        return row;
    }
    public List<String> getColumnsData(int columnsNum)
    {
        List<String> columnsAllData = new ArrayList<>();
        String columnsData = columns.get(columnsNum);
        for(int i = 0; i < rows.size(); i++) {
            columnsAllData.add(rows.get(i).getRowDataByColumn(columnsData));
        }
        return columnsAllData;
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
