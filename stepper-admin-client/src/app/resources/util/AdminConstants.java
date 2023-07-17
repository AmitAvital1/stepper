package app.resources.util;

public class AdminConstants {
    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/stepper";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOAD_XML = FULL_SERVER_PATH + "/load-xml";
    public static final String ROLES = FULL_SERVER_PATH + "/roles";
    public static final String USERS = FULL_SERVER_PATH + "/users";
    public static final String USER_HEAD_DETAILS = FULL_SERVER_PATH + "/header-details";
    public static final String ADMIN_LOGIN = FULL_SERVER_PATH + "/login";
    public static final String EXECUTION_HISTORY = FULL_SERVER_PATH + "/history";
    public static final String STATISTICS = FULL_SERVER_PATH + "/statistics";

    //statics roles
    public static final String ALL_FLOWS = "All flows";
    public static final String READ_ONLY = "Read-Only";
}
