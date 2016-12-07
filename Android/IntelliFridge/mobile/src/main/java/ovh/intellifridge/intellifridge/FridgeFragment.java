package ovh.intellifridge.intellifridge;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import static ovh.intellifridge.intellifridge.Config.FRIDGE_GET_LIST_URL;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_ID_DB;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_PREFS;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_SIZE_PREFS;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_DB;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

/**
 * A simple {@link Fragment} subclass.
 */
public class FridgeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private String server_status;
    private JSONObject server_response;
    private JSONArray jsonArray;
    SwipeRefreshLayout swipeLayout;
    private View rootView;
    public Fridge[] fridgeList;

    public FridgeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_fridge, container, false);
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_fridge);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_dark));

        getFridgeData();

        return rootView;
    }

    @Override
    public void onRefresh() {
        getFridgeData();
        swipeLayout.setRefreshing(false);
    }

    private void getFridgeData() {
        int user_id = getUserId();
        String apiKey = getApiKey();
        String jwt = signParams(user_id,apiKey);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, FRIDGE_GET_LIST_URL+"?jwt="+jwt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final String secret = JWT_KEY;
                        try {
                            final JWTVerifier verifier = new JWTVerifier(secret);
                            final Map<String, Object> claims= verifier.verify(response);
                            server_response = new JSONObject(claims);
                            server_status = server_response.getString(SERVER_STATUS);
                            jsonArray = server_response.getJSONArray(DATA);
                        } catch (JWTVerifyException e) {
                            // Invalid Token
                            Log.e("JWT ERROR",e.toString());
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            saveFridgeList(jsonArray);
                            getFridgeCardList(jsonArray);
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), R.string.fridge_list_empty, Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY ERROR",error.toString());
                    }
                });
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest, FRIDGE_LIST_REQUEST_TAG);
    }

    private void saveFridgeList(JSONArray jsonArray) { // TODO: 06-12-16
        Fridge[] fridgeList = getFridgeArray(jsonArray);
        SharedPreferences preferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(FRIDGE_LIST_SIZE_PREFS,fridgeList.length);
        for (int i=0;i<fridgeList.length;i++){
            editor.putString(FRIDGE_LIST_PREFS+"_"+i,fridgeList[i].getFridgeName());
        }
        editor.apply();
    }

    private void getFridgeCardList(JSONArray jsonArray) {
        fridgeList = getFridgeArray(jsonArray);

        RecyclerView recyclerView = (RecyclerView)rootView.findViewById(R.id.fridge_list_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(gridLayoutManager);
        FridgeRVAdapter adapter = new FridgeRVAdapter(fridgeList,getActivity().getApplicationContext());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private Fridge[] getFridgeArray(JSONArray jsonArray) {
        int length = jsonArray.length();
        Fridge[] fridgeList = new Fridge[length];

        for(int i=0;i<length;i++){
            fridgeList[i]= new Fridge("");
            fridgeList[i].setFridgeName(jsonArray.optJSONObject(i).optString(FRIDGE_NAME_DB));
            fridgeList[i].setFridgeId(jsonArray.optJSONObject(i).optInt(FRIDGE_ID_DB));
        }
        return fridgeList;
    }

    private String signParams(int user_id, String apiKey) {
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_USERID,user_id);
        claims.put(KEY_API_KEY,apiKey);
        return jwt = signer.sign(claims);
    }

    private int getUserId() {
        SharedPreferences preferences = this.getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    private String getApiKey(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }
}
