package project.java.stepper.dd.impl.list;

import project.java.stepper.dd.impl.DataDefinitionRegistry;

import java.util.ArrayList;
import java.util.List;

public class ListData {
    private List<DataDefinitionRegistry> list;

    public ListData(){
        list =  new ArrayList<>();
    }
    public void addData(DataDefinitionRegistry dataDefinitionRegistry){
        list.add(dataDefinitionRegistry);
    }
    public List<DataDefinitionRegistry> getList(){
        return list;
    }
    //and more stuff if neended
}
