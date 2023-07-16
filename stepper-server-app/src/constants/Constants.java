package constants;

public class Constants {

    public static final String USERNAME = "username";
    public static final String FLOW_NAME = "flowname";
    public static final String UUID = "uuid";
    public static final String RERUN = "rerun";
    public static final String ADMIN_LOGIN = "login";

    //Servlet Context
    public static final String STEPPER_DATA_MANAGER = "stepperManager";
    public static final String STEPPER_USER_MANAGER = "userManager";

    //Server paths
    public static final String CONTEXT_PATH = "/stepper";
    public static final String LOAD_XML_PATH = "/load-xml";
    public static final String USER_LOGIN = "/login";
    public static final String USER_HEAD_DETAILS = "/header-details";
    public static final String ROLES = "/roles";
    public static final String FLOW_DEFINITION = "/get-flows";
    public static final String EXECUTION = "/execute-flow";
    public static final String EXECUTION_REFRESHER = "/execute-flow-refresh";
    public static final String EXECUTION_RERUN = "/execute-rerun";
    public static final String EXECUTION_CONTINUATION = "/execute-continuation";
    public static final String EXECUTION_HISTORY = "/history";
    public static final String STATISTICS = "/statistics";
    public static final String USERS = "/users";

    //statics roles
    public static final String ALL_FLOWS = "All flows";
    public static final String READ_ONLY = "Read-Only";
}