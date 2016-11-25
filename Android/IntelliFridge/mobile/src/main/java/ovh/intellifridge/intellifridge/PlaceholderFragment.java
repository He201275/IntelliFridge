package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.ALLERGY_GET_LIST_URL;
import static ovh.intellifridge.intellifridge.Config.ALLERGY_LIST_ERROR;
import static ovh.intellifridge.intellifridge.Config.ALLERGY_LIST_PREFS;
import static ovh.intellifridge.intellifridge.Config.ALLERGY_LIST_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.ALLERGY_LIST_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.ALLERGY_NAME_DB;
import static ovh.intellifridge.intellifridge.Config.DATA;
import static ovh.intellifridge.intellifridge.Config.DB_CONNECTION_ERROR;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_GET_LIST_URL;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_ERROR;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_PREFS;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_DB;
import static ovh.intellifridge.intellifridge.Config.LOGIN_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.MOD_ALLERGY_KEY;
import static ovh.intellifridge.intellifridge.Config.MOD_FRIDGE_KEY;
import static ovh.intellifridge.intellifridge.Config.SERVER_RESPONSE;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

/**
 * Created by franc on 13-11-16.
 */

public class PlaceholderFragment extends android.support.v4.app.Fragment {
    JSONObject jsonObject;
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public Boolean getFridgeModStatus() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        return sharedPreferences.getBoolean(MOD_FRIDGE_KEY,true);
    }
    public Boolean getAllergyModStatus(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        return sharedPreferences.getBoolean(MOD_ALLERGY_KEY,true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getFridgeModStatus() && !getAllergyModStatus()){
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                View rootView = inflater.inflate(R.layout.fragment_recent, container, false);
                return rootView;
            }else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                View rootView = inflater.inflate(R.layout.fragment_fridge, container, false);
                getFridgeListDb();
                try {
                    String fridge_list = getFridgeList();
                    JSONArray jsonArray = new JSONArray(fridge_list);
                    getFridgeList(rootView,jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return rootView;
            }else {
                View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                return rootView;
            }
        }else if (getAllergyModStatus() && !getFridgeModStatus()){
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                View rootView = inflater.inflate(R.layout.fragment_allergy, container, false);
                getAllergyListDb();
                try {
                    String allergy_list = getAllergyList();
                    JSONArray jsonArray = new JSONArray(allergy_list);
                    getAllergyList(rootView,jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return rootView;
            }else {
                View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                return rootView;
            }
        }else {
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1){
                View rootView = inflater.inflate(R.layout.fragment_recent, container, false);
                return rootView;
            }else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                View rootView = inflater.inflate(R.layout.fragment_fridge, container, false);
                getFridgeListDb();
                try {
                    String fridge_list = getFridgeList();
                    JSONArray jsonArray = new JSONArray(fridge_list);
                    getFridgeList(rootView,jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return rootView;
            }else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                View rootView = inflater.inflate(R.layout.fragment_allergy, container, false);
                getAllergyListDb();
                try {
                    String allergy_list = getAllergyList();
                    JSONArray jsonArray = new JSONArray(allergy_list);
                    getAllergyList(rootView,jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return rootView;
            }else {
                View rootView = inflater.inflate(R.layout.fragment_main, container, false);
                return rootView;
            }
        }
    }

    private void getFridgeListDb() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FRIDGE_GET_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonObject = new JSONObject(response);
                            String server_status = jsonObject.getString(SERVER_STATUS);
                            String server_response = jsonObject.getString(SERVER_RESPONSE);
                            if (server_response.equals(FRIDGE_LIST_ERROR)){
                                Toast.makeText(getActivity().getApplicationContext(), R.string.fridge_list_empty, Toast.LENGTH_LONG).show();
                            }else if (server_response.equals(FRIDGE_LIST_SUCCESS)){
                                JSONArray fridgeList = jsonObject.getJSONArray(DATA);
                                saveFridgeList(fridgeList);
                            }else if (server_status.equals(DB_CONNECTION_ERROR)){
                                Toast.makeText(getActivity().getApplicationContext(),R.string.db_connect_error,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                params.put(Config.KEY_USERID, String.valueOf(user_id));
                return params;
            }
        };

        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest, FRIDGE_LIST_REQUEST_TAG);
    }

    private void getAllergyListDb(){
        StringRequest stringRequest = new StringRequest(ALLERGY_GET_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonObject = new JSONObject(response);
                            String server_status = jsonObject.getString(SERVER_STATUS);
                            String server_response = jsonObject.getString(SERVER_RESPONSE);
                            if (server_response.equals(ALLERGY_LIST_ERROR)){
                                Toast.makeText(getActivity().getApplicationContext(), R.string.allergy_list_empty, Toast.LENGTH_LONG).show();
                            }else if (server_response.equals(ALLERGY_LIST_SUCCESS)){
                                JSONArray allergyList = jsonObject.getJSONArray(DATA);
                                // TODO: 22-11-16 Bug - doesn't get array from API
                                saveAllergyList(allergyList);
                            }else if (server_status.equals(DB_CONNECTION_ERROR)){
                                Toast.makeText(getActivity().getApplicationContext(),R.string.db_connect_error,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: 21-11-16
                    }
                });
        //Adding the string request to the queue
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest, ALLERGY_LIST_REQUEST_TAG);
    }

    private void saveFridgeList(JSONArray fridgeList) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(FRIDGE_LIST_PREFS,fridgeList.toString());
        editor.apply();
    }

    private void saveAllergyList(JSONArray allergyList){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ALLERGY_LIST_PREFS,allergyList.toString());
        editor.apply();
    }

    private String getFridgeList(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(FRIDGE_LIST_PREFS,"");
    }

    private String getAllergyList(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(ALLERGY_LIST_PREFS,"");
    }

    private int getUserId() {
        SharedPreferences preferences = this.getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    private void getFridgeList(View rootView, JSONArray jsonArray) throws JSONException {
        String[] list= null;
        list = getStringArrayFridge(jsonArray);

        ListView listView = (ListView) rootView.findViewById(R.id.fridge_list);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                list
        );
        listView.setAdapter(listViewAdapter);
    }

    private void getAllergyList(View rootView, JSONArray jsonArrray)throws JSONException {
        //String[] list= {"Gluten","Oeufs","Poisson","Arachides","Lactose","Sésame"};
        String[] list= null;
        list = getStringArrayAllergy(jsonArrray);

        ListView listView = (ListView) rootView.findViewById(R.id.allergy_list);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_checked,
                list
        );
        listView.setAdapter(listViewAdapter);
    }

    private String[] getStringArrayAllergy(JSONArray allergy_list_json) {
        String string;
        String[] stringArray = null;
        int length = allergy_list_json.length();
        stringArray = new String[length];
        for(int i=0;i<length;i++){
            string = allergy_list_json.optString(i);
            try {
                JSONObject jsonObj = new JSONObject(string);
                stringArray[i] = jsonObj.getString(ALLERGY_NAME_DB);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArray;
    }

    public String[] getStringArrayFridge(JSONArray jsonArray) {
        String string;
        String[] stringArray = null;
        int length = jsonArray.length();
        stringArray = new String[length];
        for(int i=0;i<length;i++){
            string = jsonArray.optString(i);
            try {
                JSONObject jsonObj = new JSONObject(string);
                stringArray[i] = jsonObj.getString(FRIDGE_NAME_DB);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArray;
    }
}
