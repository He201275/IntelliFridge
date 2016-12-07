package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.DATA;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_CONTENT_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.GROCERY_LIST_GET_URL;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.LIST_NOTE_DB;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_ID_NS_DB;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_NAME_DB;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_QUANTITY_DB;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_S_ID_DB;
import static ovh.intellifridge.intellifridge.Config.SERVER_FRIDGE_EMPTY;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

public class GroceryListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeLayout;
    private String server_status;
    private JSONObject server_response;
    private JSONArray jsonArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.grocery_list_title);
        setContentView(R.layout.activity_grocery_list);

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(getDrawable(R.drawable.ic_add_black));
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setBackgroundDrawable(R.drawable.fab_background)
                .setContentView(icon)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 04-12-16 : Add to current fridge 
            }
        });

        ImageView scan = new ImageView(this);
        scan.setImageDrawable(getDrawable(R.drawable.ic_camera_black));
        SubActionButton scanButton = itemBuilder.setContentView(scan).build();
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 06-12-16  
            }
        });
        ImageView nscan = new ImageView(this);
        nscan.setImageDrawable(getDrawable(R.drawable.ic_local_pizza_black_24px));
        SubActionButton nscanButton = itemBuilder.setContentView(nscan).build();
        nscanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 04-12-16
            }
        });

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(scanButton)
                .addSubActionView(nscanButton)
                .attachTo(actionButton)
                .build();

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_grocery_list);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_dark));

        getGroceryListData();
    }

    @Override
    public void onRefresh() {
        getGroceryListData();
        swipeLayout.setRefreshing(false);
    }

    private void getGroceryListData() {
        String apiKey = getApiKey();
        int user_id = getUserId();
        String jwt = signParamsGroceryList(apiKey,user_id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GROCERY_LIST_GET_URL+"?jwt="+jwt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final String secret = JWT_KEY;
                        try {
                            final JWTVerifier verifier = new JWTVerifier(secret);
                            final Map<String, Object> claims= verifier.verify(response);
                            server_response = new JSONObject(claims);
                            server_status = server_response.getString(SERVER_STATUS);
                        } catch (JWTVerifyException e) {
                            Log.e("JWT ERROR",e.toString());
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            try {
                                jsonArray = server_response.getJSONArray(DATA);
                                getGroceryList(jsonArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (server_status.equals(SERVER_FRIDGE_EMPTY)){
                            Toast.makeText(getApplicationContext(),R.string.fridge_empty,Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY ERROR",error.toString());
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, FRIDGE_CONTENT_REQUEST_TAG);
    }

    private void getGroceryList(JSONArray jsonArray) {
        GroceryListItem[] groceryList = getGroceryListArray(jsonArray);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.grocery_list_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        GroceryListRVAdapter adapter = new GroceryListRVAdapter(groceryList);
        recyclerView.setAdapter(adapter);
    }

    private GroceryListItem[] getGroceryListArray(JSONArray jsonArray) {
        int length = jsonArray.length();
        GroceryListItem[] groceryList = new GroceryListItem[length];

        for(int i=0;i<length;i++){
            try {
                groceryList[i]= new GroceryListItem("",0,0,0,"");
                groceryList[i].productName = jsonArray.optJSONObject(i).optString(PRODUCT_NAME_DB);
                groceryList[i].productSId = jsonArray.optJSONObject(i).optInt(PRODUCT_S_ID_DB);
                groceryList[i].productNSId = jsonArray.optJSONObject(i).optInt(PRODUCT_ID_NS_DB);
                groceryList[i].productQuantity = jsonArray.getJSONObject(i).optInt(PRODUCT_QUANTITY_DB);
                groceryList[i].listNote = jsonArray.getJSONObject(i).optString(LIST_NOTE_DB);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return groceryList;
    }

    private String signParamsGroceryList(String apiKey, int user_id) {
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_USERID,user_id);
        claims.put(KEY_API_KEY,apiKey);
        return jwt = signer.sign(claims);
    }

    private String getApiKey() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }

    private int getUserId() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }
}
