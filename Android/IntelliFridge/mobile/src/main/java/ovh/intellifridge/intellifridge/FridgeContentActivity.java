package ovh.intellifridge.intellifridge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FridgeContentActivity extends AppCompatActivity {
    String fridge_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge_content);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            fridge_name = extras.getString("fridge_name");
        }
        int userId = getUserId();
        String type = "get_fridge_content";
        new FridgeBackgroundWorker(getApplicationContext()).execute(String.valueOf(userId),type,fridge_name);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        JSONArray content_list_json = null;
        String fridge_list_string = prefs.getString("fridge_list","");
        try {
            content_list_json = new JSONArray(fridge_list_string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String[] list = getStringArrayFridgeContent(content_list_json);

        ListView listView = (ListView)findViewById(R.id.fridge_list);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                list
        );
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO: 19-11-16
            }
        });
    }

    private int getUserId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getInt("user_id",0);
    }

    public String[] getStringArrayFridgeContent(JSONArray content_list_json) {
        String string;
        String[] stringArray = null;
        int length = content_list_json.length();
        stringArray = new String[length];
        for(int i=0;i<length;i++){
            string = content_list_json.optString(i);
            try {
                JSONObject jsonObj = new JSONObject(string);
                stringArray[i] = jsonObj.getString("FrigoNom");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArray;
    }
}
