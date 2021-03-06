package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Francis O. Makokha
 * Contient toutes les constantes utilisées dans le projet java
 */
public class Config {
    public static final String TITLE_ALLERANCE = "Allerance";
    public static final String TITLE_IF = "IntelliFridge";

    public static final String DOMAIN_URL = "https://www.intellifridge.ovh";
    public static final String LOGIN_URL = "https://api.intellifridge.ovh/v1/user/login";
    public static final String REGISTER_URL = "https://api.intellifridge.ovh/v1/user/register";
    public static final String FRIDGE_GET_LIST_URL = "https://api.intellifridge.ovh/v1/fridges/list";
    public static final String FRIDGE_ADD_URL = "https://api.intellifridge.ovh/v1/fridges/add";
    public static final String CONTACT_URL = "https://www.intellifridge.ovh/contact.php";
    public static final String SHOP_URL = "https://www.intellifridge.ovh/shop.php";
    public static final String PRODUCT_DB_LOCATION_CHECK_URL = "https://api.intellifridge.ovh/v1/products/isInDB";
    public static final String GET_PRODUCT_INFO_LOCAL_URL = "https://api.intellifridge.ovh/v1/products/getProductSInfo";
    public static final String ADD_PRODUCT_URL= "https://api.intellifridge.ovh/v1/products/add";
    public static final String FRIDGE_CONTENT_URL = "https://api.intellifridge.ovh/v1/fridges/getFridgeContent";
    public static final String FRIDGE_REMOVE_URL = "https://api.intellifridge.ovh/v1/fridges/remove";
    public static final String GROCERY_LIST_GET_URL = "https://api.intellifridge.ovh/v1/list/get";
    public static final String REMOVE_ONE_PRODUCT_URL = "https://api.intellifridge.ovh/v1/fridges/minusOneProduct";
    public static final String PLUS_ONE_PRODUCT_URL = "https://api.intellifridge.ovh/v1/fridges/minusOneProduct";
    public static final String GET_PRODUCT_NS_URL= "https://api.intellifridge.ovh/v1/products/getProductNS";
    public static final String GET_RECENT_CONTENT_URL = "https://api.intellifridge.ovh/v1/products/getRecentProduct";
    public static final String UPDATE_APP_URL = "https://intellifridge.franmako.com/update.php";
    public static final String ADD_GROCERY_FROM_FRIDGE_URL = "https://api.intellifridge.ovh/v1/list/addProduct";

    public static final String JWT_KEY = "wAMxBauED07a4GurMpuD";

    //LoginActivity array index
    public static final String DATA = "data";
    public static final String SERVER_STATUS = "status";

    //Server response messages
    public static final String SERVER_SUCCESS = "200";
    public static final String SERVER_FRIDGE_EMPTY = "201";
    public static final String SERVER_PROD_NOTINDB = "201";
    public static final String SERVER_FRIDGE_EXISTS= "202";
    public static final String SERVER_DB_ERROR = "500";
    public static final String UPDATE_AVAILABLE = "Update available";
    public static final String UP_TO_DATE = "App up-to-date";

    //Keys for email and password as defined in our $_POST['key']
    public static final String KEY_EMAIL = "UserAdresseMail";
    public static final String KEY_PASSWORD = "UserPassword";
    public static final String JWT_POST = "jwt";
    public static final String KEY_FNAME = "UserPrenom";
    public static final String KEY_LNAME = "UserNom";
    public static final String KEY_LANGUE = "LangueCode";
    public static final String KEY_USERID = "UserId";
    public static final String KEY_API_KEY = "ApiKey";
    public static final String KEY_FRIDGE_ID = "FrigoId";
    public static final String KEY_FRIDGE_NAME = "FrigoNom";
    public static final String KEY_PRODUCT_S_ID = "ProduitSId";
    public static final String KEY_PRODUCT_NS_ID = "ProduitNSId";
    public static final String KEY_PRODUCT_NAME = "ProduitSNom";
    public static final String KEY_PRODUCT_BRAND = "ProduitSMarque";
    public static final String KEY_PRODUCT_QUANTITY = "Contenance";
    public static final String KEY_PRODUCT_IMAGEURL = "ProduitImageUrl";
    public static final String KEY_PRODUCT_SCANNABLE = "IsScannable";
    public static final String KEY_PRODUCT_PRESENT = "IsPresent";
    public static final String KEY_OFFSET = "offset";
    public static final String KEY_VERSION = "version";

    //Keys for Sharedpreferences
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
    public static final String UPDATE_NOTIF_PREFS = "updateNotif";
    public static final String LAST_UPDATE_CHECK = "lastTimeActionDone";
    public static final String USER_ID_PREFS = "user_id";
    public static final String USER_EMAIL_PREFS = "user_email";
    public static final String USER_PRENOM_PREFS = "user_fName";
    public static final String USER_NOM_PREFS = "user_lName";
    public static final String USER_LOCALITE_PREFS = "user_localite";
    public static final String USER_GENRE_PREFS = "user_genre";
    public static final String USER_LANG_PREFS = "user_language";
    public static final String USER_API_KEY = "user_apiKey";
    public static final String USER_NB_FRIDGES_PREFS = "user_nbFridges";
    public static final String USER_FRIDGE_PREFS = "user_fridge";

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
    public static final String FRIDGE_ID_DB = "FrigoId";
    public static final String PRODUCT_S_ID_DB = "ProduitSId";
    public static final String PRODUCT_NAME_DB = "ProduitNom";
    public static final String PRODUCT_ID_NS_DB = "ProduitNSId";
    public static final String PRODUCT_QUANTITY_DB = "Quantite";
    public static final String PRODUCT_ID = "ProduitId";
    public static final String LIST_NOTE_DB = "ListeNote";
    public static final String DATE_AJOUT_DB = "DateAjout";
    public static final String PRODUCT_NS_NAME_FR_DB = "ProduitNSNomFR";
    public static final String PRODUCT_NS_TYPE_DB = "ProduitNSType";


    public static final String LOGIN_REGISTER_EXTRA= "new_user_email";
    public static final String SCAN_TYPE_EXTRA = "scan_type";
    public static final String SCAN_FRIDGE = "scan_fridge";
    public static final String SCAN_ALLERGY = "scan_allergy";
    public static final String BARCODE_EXTRA = "scan_result";
    public static final String FRIDGE_NAME_EXTRA = "fridge_name";
    public static final String FRIDGE_ID_EXTRA = "fridge_id";

    public static final String MOD_FRIDGE_KEY = "module_fridge";
    public static final String MOD_ALLERGY_KEY = "module_allergy";

    public static final String LOGIN_REQUEST_TAG = "ovh.intellifridge.loginrequest";
    public static final String REGISTER_REQUEST_TAG = "ovh.intellifridge.registerrequest";
    public static final String FRIDGE_LIST_REQUEST_TAG = "ovh.intellifridge.fridgelistrequest";
    public static final String PRODUCT_ADD_REQUEST_TAG = "ovh.intellifridge.productaddrequest";
    public static final String BARCODE_CHECK_REQUEST_TAG = "ovh.intellifridge.barcodecheckrequest";
    public static final String GET_INFO_LOCAL_REQUEST_TAG = "ovh.intellifridge.getinfolocaldb";
    public static final String GET_INFO_OFF_REQUEST_TAG = "ovh.intellifridge.getinfooffdb";
    public static final String ADD_PRODUCT_LOCAL_DB_REQUEST_TAG = "ovh.intellifridge.addproductlocaldb";
    public static final String FRIDGE_CONTENT_REQUEST_TAG = "ovh.intellifridge.fridgecontent";
    public static final String ADD_FRIDGE_REQUEST_TAG = "ovh.intellifridge.addfridge";
    public static final String GET_GROCERY_LIST_TAG = "ovh.intellifridge.getgrocerylist";
    public static final String PRODUCT_REMOVE_REQUEST_TAG = "ovh.intellifridge.removeoneprod";
    public static final String RECENT_CONTENT_REQUEST_TAG = "ovh.intellifridge.recentcontent";

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

    public static final String VOLLEY_ERROR_TAG = "VOLLEY ERROR";

    public static final String SHARED_PREF_NAME_ALLERGY = "spAllergy"; // TODO: 04-12-16
    public static final String SHARED_PREF_NAME = "intellifridge";
    public static final String SHARED_PREF_FRIDGES_NAME = "intellifridge_fridges";

    public static final String MINUS_ONE = "minus_one";
    public static final String PLUS_ONE = "plus_one";

    public static final int updateNotification_id = 131315;
}
