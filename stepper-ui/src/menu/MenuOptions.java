package menu;

public enum MenuOptions {
    LOAD_XML("Load data from XML file")
    ,FLOWS_DEFINITION("Show flow's details")
    ,FLOW_EXECUTION("Execute flow")
    ,FLOW_HISTORY("Show flows executions history")
    ,STATISTICS("Show flow statistics")
    ,Exit("Exit");

    private final String userString;
    private int optionNum;

    MenuOptions(String userString){this.userString = userString;}

    public void print(){
        System.out.println((this.ordinal()+1) + "." + this.userString);}
}
