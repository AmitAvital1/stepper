package app.resources.util;

public class Constants {

    public final static Integer REFRESH_RATE = 2000;

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/stepper";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN = FULL_SERVER_PATH + "/login";
    public static final String USER_HEAD_DETAILS = FULL_SERVER_PATH + "/header-details";
    public static final String FLOW_DEFINITION = FULL_SERVER_PATH + "/get-flows";
    public static final String FLOW_EXECUTION = FULL_SERVER_PATH + "/execute-flow";
    public static final String FLOW_EXECUTION_REFRESH = FULL_SERVER_PATH + "/execute-flow-refresh";
    public static final String EXECUTION_RERUN = FULL_SERVER_PATH + "/execute-rerun";
}
