package ovh.intellifridge.intellifridge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by franc on 13-11-16.
 */

public class PlaceholderFragment extends android.support.v4.app.Fragment {
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
        return sharedPreferences.getBoolean(SettingsActivity.MOD_FRIDGE_KEY,true);
    }
    public Boolean getAllergyModStatus(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        return sharedPreferences.getBoolean(SettingsActivity.MOD_ALLERGY_KEY,true);
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
                int userId = getUserId();
                new FridgeBackgroundWorker(getActivity()).execute(String.valueOf(userId));
                try {
                    getFridgeList(rootView);
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
                int userId = getUserId();
                //new AllergyBackgroundWorker(getActivity()).execute(String.valueOf(userId));
                try {
                    getAllergyList(rootView);
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
                int userId = getUserId();
                new FridgeBackgroundWorker(getActivity()).execute(String.valueOf(userId));
                try {
                    getFridgeList(rootView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return rootView;
            }else if (getArguments().getInt(ARG_SECTION_NUMBER) == 3){
                View rootView = inflater.inflate(R.layout.fragment_allergy, container, false);
                int userId = getUserId();
                //new AllergyBackgroundWorker(getActivity()).execute(String.valueOf(userId));
                try {
                    getAllergyList(rootView);
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

    private int getUserId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        return sharedPreferences.getInt("user_id",0);
    }

    private void getAllergyList(View rootView)throws JSONException {
        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        JSONArray allergy_list_json = null;
        String allergy_list_string = prefs.getString("allergy_list","");
        String[] list= null;
        allergy_list_json = new JSONArray(allergy_list_string);*/
        String[] list= {"Gluten","Oeufs","Poisson","Arachides","Lactose","Sésame"};
        //list = getStringArrayAllergy(allergy_list_json);

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
                stringArray[i] = jsonObj.getString("AllergieNom");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArray;
    }

    public String[] getStringArrayFridge(JSONArray fridge_list_json) {
        String string;
        String[] stringArray = null;
        int length = fridge_list_json.length();
        stringArray = new String[length];
        for(int i=0;i<length;i++){
            string = fridge_list_json.optString(i);
            try {
                JSONObject jsonObj = new JSONObject(string);
                stringArray[i] = jsonObj.getString("FrigoNom");
                Log.wtf("test",jsonObj.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArray;
    }

    private void getFridgeList(View rootView) throws JSONException {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        JSONArray fridge_list_json = null;
        String fridge_list_string = prefs.getString("fridge_list","");
        String[] list= null;
        fridge_list_json = new JSONArray(fridge_list_string);
        list = getStringArrayFridge(fridge_list_json);

        ListView listView = (ListView) rootView.findViewById(R.id.fridge_list);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                list
        );
        listView.setAdapter(listViewAdapter);
    }
}
