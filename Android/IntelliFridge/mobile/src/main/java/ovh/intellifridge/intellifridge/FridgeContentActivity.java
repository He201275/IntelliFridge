package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.FRIDGE_CONTENT_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_CONTENT_URL;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_ID_URL;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_EXTRA;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

public class FridgeContentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeLayout;
    String fridge;
    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge_content);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fridge = extras.getString(FRIDGE_NAME_EXTRA);
            setTitle(fridge);
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_fridge_content);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_dark));

        getFridgeId();
        //getFridgeContentData();
        //displayFridgeContent();
    }

    private void getFridgeId() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FRIDGE_ID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*try {
                            jsonObject = new JSONObject(response);
                            String server_status = jsonObject.getString(SERVER_STATUS);
                            String server_response = jsonObject.getString(SERVER_RESPONSE);
                            if (server_response.equals(FRIDGE_ID_ERROR)){
                                Toast.makeText(getApplicationContext(), R.string.fridge_id_error, Toast.LENGTH_LONG).show();
                            }else if (server_response.equals(FRIDGE_ID_SUCCESS)){
                                Log.wtf("TEST",jsonObject.toString());
                            }else if (server_status.equals(DB_CONNECTION_ERROR)){
                                Toast.makeText(getApplicationContext(),R.string.db_connect_error,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: 21-11-16
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int user_id = getUserId();
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(KEY_USERID, String.valueOf(user_id));
                params.put(KEY_FRIDGE_NAME, fridge);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, FRIDGE_CONTENT_REQUEST_TAG);
    }

    private void getFridgeContentData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FRIDGE_CONTENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*try {
                            jsonObject = new JSONObject(response);
                            String server_status = jsonObject.getString(SERVER_STATUS);
                            String server_response = jsonObject.getString(SERVER_RESPONSE);
                            if (server_response.equals(FRIDGE_LIST_ERROR)){
                                Toast.makeText(getApplicationContext(), R.string.fridge_list_empty, Toast.LENGTH_LONG).show();
                            }else if (server_response.equals(FRIDGE_LIST_SUCCESS)){
                                JSONArray fridgeList = jsonObject.getJSONArray(DATA);
                            }else if (server_status.equals(DB_CONNECTION_ERROR)){
                                Toast.makeText(getApplicationContext(),R.string.db_connect_error,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: 21-11-16
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int user_id = getUserId();
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(KEY_USERID, String.valueOf(user_id));
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, FRIDGE_CONTENT_REQUEST_TAG);
    }

    private int getUserId() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    private void displayFridgeContent(){

    }

    @Override
    public void onRefresh() {
        getFridgeId();
        //getFridgeContentData();
        //displayFridgeContent();
        swipeLayout.setRefreshing(false);
    }
}
