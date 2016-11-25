package ovh.intellifridge.intellifridge;

/**
 * Created by franc on 24-11-16.
 */

public class Config {
    public static final String DOMAIN_URL = "http://www.intellifridge.ovh";
    public static final String DOMAIN_API_URL = "http://www.intellifridge.franmako.com";
    public static final String LOGIN_URL = "http://www.intellifridge.franmako.com/login.php";
    public static final String REGISTER_URL = "http://www.intellifridge.franmako.com/register.php";
    public static final String ALLERGY_GET_LIST_URL = "http://www.intellifridge.franmako.com/getAllergyList.php";
    public static final String FRIDGE_GET_LIST_URL = "http://www.intellifridge.franmako.com/getFridgeList.php";
    public static final String FRIDGE_ADD_URL = "http://www.intellifridge.franmako.com/addFridge.php";
    public static final String CONTACT_URL = "http://www.intellifridge.ovh/contact.php";
    public static final String SHOP_URL = "http://www.intellifridge.ovh/shop.php";

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
    public static final String FRIDGE_ADD_SUCCESS = "Fridge added";
    public static final String FRIDGE_ADD_ERROR = "Fridge add error";

    //Keys for email and password as defined in our $_POST['key']
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FNAME = "fName";
    public static final String KEY_LNAME = "lName";
    public static final String KEY_LANGUE = "langue";
    public static final String KEY_USERID = "userId";
    public static final String KEY_FRIDGE_NAME = "fridgeName";

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
    public static final String OFF_DATA_PREFS = "off_data";

    //LoginActivity table column names
    public static final String USER_ID_DB = "UserId";
    public static final String USER_EMAIL_DB = "UserAdresseMail";
    public static final String USER_PRENOM_DB = "UserPrenom";
    public static final String USER_NOM_DB = "UserNom";
    public static final String USER_LOCALITE_DB = "UserLocalite";
    public static final String USER_GENRE_DB = "UserGenre";
    public static final String USER_LANG_DB = "UserLangue";

    public static final String FRIDGE_NAME_DB = "FrigoNom";
    public static final String ALLERGY_NAME_DB = "AllergieNom";

    public static final String LOGIN_REGISTER_EXTRA= "new_user_email";
    public static final String SCAN_EXTRA = "scan_type";
    public static final String SCAN_FRIDGE = "scan_fridge";
    public static final String SCAN_ALLERGY = "scan_allergy";
    public static final String BARCODE_EXTRA = "scan_result";

    public static String MOD_FRIDGE_KEY = "module_fridge";
    public static String MOD_ALLERGY_KEY = "module_allergy";

    public static String LOGIN_REQUEST_TAG = "ovh.intellifridge.loginrequest";
    public static String REGISTER_REQUEST_TAG = "ovh.intellifridge.registerrequest";
    public static String FRIDGE_LIST_REQUEST_TAG = "ovh.intellifridge.fridgelistrequest";
    public static String ALLERGY_LIST_REQUEST_TAG = "ovh.intellifridge.allergylistrequest";
}
