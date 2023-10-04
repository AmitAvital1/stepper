package dto.execution;

import project.java.stepper.dd.impl.SqlFilter.SqlFilter;
import project.java.stepper.dd.impl.SqlFilter.SqlOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SqlFilterDTO {
    private Map<String,Data> filters = new HashMap<>();
    public void addKey(String key,String operation,String value){
        filters.put(key,new Data(operation,value));
    }

    private class Data{
        String operation;
        String value;

        public Data(String oper,String value){
            operation = oper;
            this.value = value;
        }
    }

    public Set<String> getKeys(){
        return filters.keySet();
    }
    public String getOperation(String key){
        return filters.get(key).operation;
    }
    public String getValue(String key){
        return filters.get(key).value;
    }
}
