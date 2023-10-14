package project.java.stepper.dd.impl.SqlFilter;

import com.jayway.jsonpath.internal.path.ArraySliceOperation;
import project.java.stepper.exceptions.SqlFilterException;

import java.util.*;

public class SqlFilter {
     private final Map<String,Data> filterMapping = new HashMap<>();

    private class Data{
       private SqlOperation operation = null;
       private String value = null;

        public Data(SqlOperation operation,String value) {
            this.operation = operation;
            this.value = value;
        }

        public SqlOperation getOperation() {
            return operation;
        }

        public String getValue() {
            return value;
        }

        public void setOperation(SqlOperation operation) {
            this.operation = operation;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public SqlFilter(String input) throws SqlFilterException{
        List<String> valuesList = new ArrayList<>(Arrays.asList(input.split("\\|")));
        for(String filterReg : valuesList){
            List<String> filter = new ArrayList<>(Arrays.asList(filterReg.split("\\,")));
            if(filter.size() == 1)
                filterMapping.put(filter.get(0),new Data(null,null));
            else if(filter.size() == 2 || filter.size() == 3) {
                try{
                    SqlOperation operation = SqlOperation.valueOf(SqlOperation.class, filter.get(1));
                    if(filter.size() == 2)
                        filterMapping.put(filter.get(0),new Data(operation,null));
                    else
                        filterMapping.put(filter.get(0),new Data(operation,filter.get(2)));
                } catch (NullPointerException | IllegalArgumentException e) {
                    throw new SqlFilterException("Invalid sql operation");
                }
            }
            else{
                throw new SqlFilterException("Invalid sql filter regex");
            }


        }

    }

    public void addFilter(String key,String oper,String value){
        Data newData = new Data(SqlOperation.valueOf(SqlOperation.class,oper),value);
        filterMapping.put(key,newData);
    }
    public void addFilter(String key){
        filterMapping.put(key,null);
    }
    public void addOperation(String key,String oper){
        filterMapping.get(key).setOperation(SqlOperation.valueOf(SqlOperation.class,oper));
    }
    public void addValue(String key,String value){
        filterMapping.get(key).setValue(value);
    }
    public Map<String, Data> getFilterMapping() {
        return filterMapping;
    }
    public Set<String> getKeys(){
        return filterMapping.keySet();
    }
    public SqlOperation getOperation(String key){
        return filterMapping.get(key).getOperation();
    }
    public String getValue(String key){
        return filterMapping.get(key).getValue();
    }
    public String toSql(){
        StringBuilder sql = new StringBuilder(" WHERE ");
        for (Map.Entry<String, Data> entry : filterMapping.entrySet()) {
            String key = entry.getKey();
            Data filterData = entry.getValue();
            if(key == null || filterData.getValue() == null || filterData.getOperation() == null)
                break;
            String tempSql = key + " " + filterData.getOperation().toString() + " '" + filterData.getValue() + "'" + " AND ";
            sql.append(tempSql);
        }
        if(sql.toString().equals(" WHERE "))//The input was optional and does not added nothing
            return "";
        sql.setLength(sql.length() - 5);
        return sql.toString();
    }
}
