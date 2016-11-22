package ovh.intellifridge.intellifridge;

/**
 * Created by franc on 21-11-16.
 */

public class Config {
    public static final String LOGIN_URL = "http://intellifridge.franmako.com/login.php";
    public static final String REGISTER_URL = "http://intellifridge.franmako.com/register.php";
    public static final String ALLERGY_GET_LIST_URL = "";
    public static final String FRIDGE_GET_LIST_URL = "";
    public static final String FRIDGE_ADD_URL = "";
    public static final String CONTACT_URL = "http://www.intellifridge.ovh/contact.php";
    public static final String SHOP_URL = "http://www.intellifridge.ovh/shop.php";
    public static final String DOMAIN_URL = "http://www.intellifridge.ovh";

    //LoginActivity array index
    public static final String SERVER_STATUS = "server-status";
    public static final String SERVER_RESPONSE = "reponse-status";
    public static final String DATA = "reponse-data";

    //Server response messages
    public static final String REGISTER_SUCCESS = "Registration Successful!";
    public static final String REGISTER_ERROR = "Registration Error!";
    public static final String DB_CONNECTION_ERROR = "Database not accessible!";
    public static final String REQUIRED_FIELD_ERROR = "Required field(s) empty!";
    public static final String LOGIN_SUCCESS = "Login Successful!";
    public static final String LOGIN_ERROR = "Login Unsuccessful!";

    //Keys for email and password as defined in our $_POST['key']
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FNAME = "fName";
    public static final String KEY_LNAME = "lName";
    public static final String KEY_LANGUE = "langue";

    //Keys for Sharedpreferences
    public static final String SHARED_PREF_NAME = "intellifridge";
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
    public static final String USER_ID_PREFS = "user_id";
    public static final String USER_EMAIL_PREFS = "user_email";
    public static final String USER_PRENOM_PREFS = "user_fName";
    public static final String USER_NOM_PREFS = "user_lName";
    public static final String USER_LOCALITE_PREFS = "user_localite";
    public static final String USER_GENRE_PREFS = "user_genre";
    public static final String USER_LANG_PREFS = "user_language";

    //LoginActivity table column names
    public static final String USER_ID_DB = "UserId";
    public static final String USER_EMAIL_DB = "UserAdresseMail";
    public static final String USER_PRENOM_DB = "UserPrenom";
    public static final String USER_NOM_DB = "UserNom";
    public static final String USER_LOCALITE_DB = "UserLocalite";
    public static final String USER_GENRE_DB = "UserGenre";
    public static final String USER_LANG_DB = "UserLangue";

    public static final String LOGIN_REGISTER_EXTRA= "new_user_email";
}
