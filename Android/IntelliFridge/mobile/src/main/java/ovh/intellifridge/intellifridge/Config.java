package ovh.intellifridge.intellifridge;

/**
 * Created by franc on 24-11-16.
 */

public class Config {
    public static final String DOMAIN_URL = "http://www.intellifridge.ovh";
    //public static final String DOMAIN_API_URL = "http://www.intellifridge.franmako.com";
    public static final String LOGIN_URL = "http://api.intellifridge.ovh/v1/user/login";
    public static final String REGISTER_URL = "http://api.intellifridge.ovh/v1/user/register";
    public static final String FRIDGE_GET_LIST_URL = "http://api.intellifridge.ovh/v1/fridges/list";
    public static final String FRIDGE_ADD_URL = "http://api.intellifridge.ovh/v1/fridges/add";
    public static final String CONTACT_URL = "http://www.intellifridge.ovh/contact.php";
    public static final String SHOP_URL = "http://www.intellifridge.ovh/shop.php";
    public static final String PRODUCT_DB_LOCATION_CHECK_URL = "";
    public static final String GET_PRODUCT_INFO_LOCAL_URL = "";
    public static final String ADD_PRODUCT_LOCAL_DB = "";
    public static final String FRIDGE_ID_URL = "http://api.intellifridge.ovh/v1/fridges/getFridgeId";
    public static final String FRIDGE_CONTENT_URL = "http://api.intellifridge.ovh/v1/fridges/getFridgeContent";
    public static final String GET_FRIDGECONTENT_INFO = "http://api.intellifridge.ovh/v1/products/searchById";

    public static final String JWT_KEY = "wAMxBauED07a4GurMpuD";

    //LoginActivity array index
    public static final String DATA = "data";
    public static final String SERVER_STATUS = "status";

    //Server response messages
    public static final String SERVER_SUCCESS = "200";

    //Keys for email and password as defined in our $_POST['key']
    public static final String KEY_EMAIL = "UserAdresseMail";
    public static final String KEY_PASSWORD = "UserPassword";
    public static final String JWT_POST = "jwt";
    public static final String KEY_FNAME = "UserPrenom";
    public static final String KEY_LNAME = "UserNom";
    public static final String KEY_LANGUE = "LangueCode";
    public static final String KEY_USERID = "UserId";
    public static final String KEY_API_KEY = "ApiKey";
    public static final String KEY_FRIDGE_NAME = "FrigoNom";
    public static final String KEY_FRIDGE_ID = "FrigoId";
    public static final String KEY_BARCODE = "ProductSId";
    public static final String KEY_PRODUCT_ID = "productId";
    public static final String KEY_PRODUCT_BRAND = "brand";
    public static final String KEY_PRODUCT_QUANTITY = "quantity";
    public static final String KEY_PRODUCT_IMAGEURL = "imageUrl";

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
    public static final String USER_API_KEY = "user_apiKey";
    public static final String FRIDGE_LIST_PREFS = "user_fridge_list";

    //Table column names
    public static final String USER_ID_DB = "UserId";
    public static final String USER_EMAIL_DB = "UserAdresseMail";
    public static final String USER_PRENOM_DB = "UserPrenom";
    public static final String USER_NOM_DB = "UserNom";
    public static final String USER_LOCALITE_DB = "CommuneLocalite";
    public static final String USER_GENRE_DB = "UserGenre";
    public static final String USER_LANG_DB = "LangueCode";
    public static final String USER_API_KEY_DB = "ApiKey";
    public static final String FRIDGE_NAME_DB = "FrigoNom";
    public static final String PRODUCT_S_ID = "ProduitSId";

    public static final String LOGIN_REGISTER_EXTRA= "new_user_email";
    public static final String SCAN_TYPE_EXTRA = "scan_type";
    public static final String SCAN_FRIDGE = "scan_fridge";
    public static final String SCAN_ALLERGY = "scan_allergy";
    public static final String SCAN_INFO = "scan_info";
    public static final String BARCODE_EXTRA = "scan_result";
    public static final String FRIDGE_NAME_EXTRA = "fridge_name";

    public static final String MOD_FRIDGE_KEY = "module_fridge";
    public static final String MOD_ALLERGY_KEY = "module_allergy";

    public static final String LOGIN_REQUEST_TAG = "ovh.intellifridge.loginrequest";
    public static final String REGISTER_REQUEST_TAG = "ovh.intellifridge.registerrequest";
    public static final String FRIDGE_LIST_REQUEST_TAG = "ovh.intellifridge.fridgelistrequest";
    public static final String ALLERGY_LIST_REQUEST_TAG = "ovh.intellifridge.allergylistrequest";
    public static final String PRODUCT_ADD_REQUEST_TAG = "ovh.intellifridge.productaddrequest";
    public static final String BARCODE_CHECK_REQUEST_TAG = "ovh.intellifridge.barcodecheckrequest";
    public static final String GET_INFO_LOCAL_REQUEST_TAG = "ovh.intellifridge.getinfolocaldb";
    public static final String GET_INFO_OFF_REQUEST_TAG = "ovh.intellifridge.getinfooffdb";
    public static final String ADD_PRODUCT_LOCAL_DB_REQUEST_TAG = "ovh.intellifridge.addproductlocaldb";
    public static final String FRIDGE_CONTENT_REQUEST_TAG = "ovh.intellifridge.fridgecontent";
    public static final String FRIDGE_ID_REQUEST_TAG = "ovh.intellifridge.fridgeid";
    public static final String ADD_FRIDGE_REQUEST_TAG = "ovh.intellifridge.addfridge";

    public static final int TAB_RECENT_DEFAULT = 0;
    public static final int TAB_FRIDGE_DEFAULT = 1;
    public static final int TAB_ALLERGY_DEFAULT = 2;
    public static final int TAB_MAPS_DEFAULT = 3;
    public static final int TAB_RECENT_FRIDGE = 0;
    public static final int TAB_FRIDGE_FRIDGE = 1;
    public static final int TAB_ALLERGY_ALLERGY = 0;
    public static final int TAB_MAPS_ALLERGY = 1;

    //JSON response OFF
    public static final String OFF_STATUS_VERBOSE = "status_verbose";
    public static final String OFF_STATUS_FOUND = "product found";
    public static final String OFF_PRODUCT = "product";
    public static final String OFF_PRODUCTNAME = "product_name";
    public static final String OFF_PRODUCTNAME_EN = "product_name_en";
    public static final String OFF_PRODUCTNAME_FR = "product_name_fr";
    public static final String OFF_QUANTITY = "quantity";
    public static final String OFF_IMAGE_URL = "image_url";
    public static final String OFF_BRANDS = "brands";

}
