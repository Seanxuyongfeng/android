package sean.com.sean.com.server;

/**
 * Created by Sean on 2016/7/20.
 */
public class Constants {

    public static final String BASE_URL = "http://192.168.1.106";
    private static final String SUFFIX_PHP = ".php";

    public static final String USER_REGISTER = BASE_URL + "/lapi/userregister" + SUFFIX_PHP;
    public static final String USER_GETINFO = BASE_URL + "/lapi/userinfo" + SUFFIX_PHP;

    public static final String JSON_USERNAME = "username";
    public static final String JSON_PWD = "password";
    public static final String JSON_AGE = "age";
    public static final String JSON_NICKNAME = "nickname";
    public static final String JSON_BIRTHDAY = "birthday";

}
