package ovh.intellifridge.intellifridge;

/**
 * Created by franc on 21-11-16.
 */

public class Config {
    public static final String DOMAIN_URL = "http://www.intellifridge.ovh";
    public static final String DOMAIN_API_URL = "http://intellifridge.franmako.com";
    public static final String LOGIN_URL = DOMAIN_API_URL+"/login.php";
    public static final String REGISTER_URL = DOMAIN_API_URL+"/register.php";
    public static final String ALLERGY_GET_LIST_URL = DOMAIN_API_URL+"/getAllergyList.php";
    public static final String FRIDGE_GET_LIST_URL = DOMAIN_API_URL+"/getFridgeList.php";
    public static final String FRIDGE_ADD_URL = DOMAIN_API_URL+"/addFridge.php";
    public static final String CONTACT_URL = DOMAIN_API_URL+"/contact.php";
    public static final String SHOP_URL = DOMAIN_API_URL+"/shop.php";

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
    public static final String FRIDGE_LIST_SUCCESS = "Fridge list found";
    public static final String FRIDGE_LIST_ERROR = "No Fridges";
    public static final String ALLERGY_LIST_SUCCESS = "Allergy List found";
    public static final String ALLERGY_LIST_ERROR = "No allergy List";

    //Keys for email and password as defined in our $_POST['key']
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FNAME = "fName";
    public static final String KEY_LNAME = "lName";
    public static final String KEY_LANGUE = "langue";
    public static final String KEY_USERID = "userId";

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
    public static final String FRIDGE_LIST_PREFS = "user_fridge_list";
    public static final String ALLERGY_LIST_PREFS = "allergy_list";

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
