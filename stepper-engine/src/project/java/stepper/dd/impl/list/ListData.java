package project.java.stepper.dd.impl.list;


import java.util.ArrayList;
import java.util.List;

public class ListData <T> {
    private List<T> list;

    public ListData(){
        list =  new ArrayList<>();
    }
    public void addData(T data){
        list.add(data);
    }
    public List<T> getList(){
        return list;
    }
    public int size() {return list.size();}
    //and more stuff if needed
}
