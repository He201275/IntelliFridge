package ovh.intellifridge.intellifridge;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

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
import static ovh.intellifridge.intellifridge.Config.GET_RECENT_CONTENT_URL;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_NAME_DB;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_QUANTITY_DB;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_S_ID_DB;
import static ovh.intellifridge.intellifridge.Config.RECENT_CONTENT_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.SERVER_FRIDGE_EMPTY;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout swipeLayout;
    private JSONObject server_response;
    private String server_status;
    JSONArray jsonArray;
    View rootView;

    public RecentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recent, container, false);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_recent_content);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_dark));

        getRecentContent();
        return rootView;
    }

    private void getRecentContent() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_RECENT_CONTENT_URL,
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
                            // Invalid Token
                            Log.e("JWT ERROR",e.toString());
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            try {
                                jsonArray = server_response.getJSONArray(DATA);
                                Log.wtf("REC",jsonArray.toString());
                                getRecentContentList(jsonArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            // TODO: 11-12-16
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY ERROR",error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String apiKey = getApiKey();
                int user_id = getUserId();
                String jwt = signParamsRecentContent(apiKey,user_id);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest, RECENT_CONTENT_REQUEST_TAG);
    }

    /**
     * {@link BarcodeReaderActivity#getUserId()}
     * @return
     */
    private int getUserId() {
        SharedPreferences preferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    /**
     * {@link BarcodeReaderActivity#getApiKey()}
     * @return
     */
    private String getApiKey() {
        SharedPreferences preferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }

    private String signParamsRecentContent(String apiKey, int user_id) {
        final JWTSigner signer = new JWTSigner(JWT_KEY);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_USERID,user_id);
        claims.put(KEY_API_KEY,apiKey);
        return signer.sign(claims);
    }

    private void getRecentContentList(JSONArray jsonArray) {
        Product[] fridgeContent = getRecentContentArray(jsonArray);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.recent_content_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        FridgeContentRVAdapter adapter = new FridgeContentRVAdapter(fridgeContent);
        recyclerView.setAdapter(adapter);
    }

    private Product[] getRecentContentArray(JSONArray jsonArray) {
        int length = jsonArray.length();
        Product[] fridgeContent = new Product[length];

        for(int i=0;i<length;i++){
            try {
                fridgeContent[i]= new Product("",0);
                fridgeContent[i].setProductName(jsonArray.optJSONObject(i).optString(PRODUCT_NAME_DB));
                fridgeContent[i].setProductSId(jsonArray.optJSONObject(i).optInt(PRODUCT_S_ID_DB));
                fridgeContent[i].setProductQuantity(jsonArray.getJSONObject(i).optInt(PRODUCT_QUANTITY_DB));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return fridgeContent;
    }

    @Override
    public void onRefresh() {
        getRecentContent();
        swipeLayout.setRefreshing(false);
    }
}
