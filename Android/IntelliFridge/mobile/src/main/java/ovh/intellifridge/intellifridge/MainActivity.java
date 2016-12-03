package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.ADD_FRIDGE_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_ADD_URL;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.MOD_ALLERGY_KEY;
import static ovh.intellifridge.intellifridge.Config.MOD_FRIDGE_KEY;
import static ovh.intellifridge.intellifridge.Config.SCAN_ALLERGY;
import static ovh.intellifridge.intellifridge.Config.SCAN_FRIDGE;
import static ovh.intellifridge.intellifridge.Config.SCAN_INFO;
import static ovh.intellifridge.intellifridge.Config.SCAN_TYPE_EXTRA;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.TAB_ALLERGY_ALLERGY;
import static ovh.intellifridge.intellifridge.Config.TAB_ALLERGY_DEFAULT;
import static ovh.intellifridge.intellifridge.Config.TAB_FRIDGE_DEFAULT;
import static ovh.intellifridge.intellifridge.Config.TAB_FRIDGE_FRIDGE;
import static ovh.intellifridge.intellifridge.Config.TAB_MAPS_ALLERGY;
import static ovh.intellifridge.intellifridge.Config.TAB_MAPS_DEFAULT;
import static ovh.intellifridge.intellifridge.Config.TAB_RECENT_DEFAULT;
import static ovh.intellifridge.intellifridge.Config.TAB_RECENT_FRIDGE;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_EMAIL_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

/**
 * L'activité principale de l'application
 * Contient les onglets
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    NavigationView navigationView;
    String fridge_name = "";
    private JSONObject server_response;
    private String server_status="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getAllergyModStatus() && !getFridgeModStatus()){
            setContentView(R.layout.activity_main_allerance);
        }else {
            setContentView(R.layout.activity_main);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getAllergyModStatus() && !getFridgeModStatus()){
            toolbar.setBackgroundColor(getResources().getColor((R.color.colorPrimaryAllerance)));
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //setEmailNav();

        mViewPager.setCurrentItem(1);

        addFloatingActionMenu();
    }

    private void addFloatingActionMenu() {
        // repeat many times:
        if (getFridgeModStatus() && !getAllergyModStatus()){
            ImageView icon = new ImageView(this);
            icon.setImageDrawable(getDrawable(R.drawable.ic_camera_black));
            FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                    .setBackgroundDrawable(R.drawable.fab_background)
                    .setContentView(icon)
                    .build();

            SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

            ImageView fridgeIcon = new ImageView(this);
            fridgeIcon.setImageDrawable(getDrawable(R.drawable.ic_fridge_black));
            SubActionButton fridge = itemBuilder.setContentView(fridgeIcon).build();
            fridge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBarcodeReader(SCAN_FRIDGE);
                }
            });

            ImageView infoIcon = new ImageView(this);
            infoIcon.setImageDrawable(getDrawable(R.drawable.ic_info_black_24px));
            SubActionButton info = itemBuilder.setContentView(infoIcon).build();
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBarcodeReader(SCAN_INFO);
                }
            });

            FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(fridge)
                    .addSubActionView(info)
                    .attachTo(actionButton)
                    .build();
        }else if (!getFridgeModStatus() && getAllergyModStatus()){
            ImageView icon = new ImageView(this);
            icon.setImageDrawable(getDrawable(R.drawable.ic_photo_camera_white_24px));
            FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                    .setBackgroundDrawable(R.drawable.fab_background_allerance)
                    .setContentView(icon)
                    .build();

            SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

            ImageView allergyIcon = new ImageView(this);
            allergyIcon.setImageDrawable(getDrawable(R.drawable.ic_allergy));
            SubActionButton allergy = itemBuilder.setContentView(allergyIcon).build();
            allergy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBarcodeReader(SCAN_ALLERGY);
                }
            });

            ImageView infoIcon = new ImageView(this);
            infoIcon.setImageDrawable(getDrawable(R.drawable.ic_info_black_24px));
            SubActionButton info = itemBuilder.setContentView(infoIcon).build();
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBarcodeReader(SCAN_INFO);
                }
            });

            FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(allergy)
                    .addSubActionView(info)
                    .attachTo(actionButton)
                    .build();
        }else{
            ImageView icon = new ImageView(this);
            icon.setImageDrawable(getDrawable(R.drawable.ic_camera_black));
            FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                    .setBackgroundDrawable(R.drawable.fab_background)
                    .setContentView(icon)
                    .build();

            SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

            ImageView fridgeIcon = new ImageView(this);
            fridgeIcon.setImageDrawable(getDrawable(R.drawable.ic_fridge_black));
            SubActionButton fridge = itemBuilder.setContentView(fridgeIcon).build();
            fridge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBarcodeReader(SCAN_FRIDGE);
                }
            });

            ImageView allergyIcon = new ImageView(this);
            allergyIcon.setImageDrawable(getDrawable(R.drawable.ic_allergy));
            SubActionButton allergy = itemBuilder.setContentView(allergyIcon).build();
            allergy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBarcodeReader(SCAN_ALLERGY);
                }
            });

            ImageView infoIcon = new ImageView(this);
            infoIcon.setImageDrawable(getDrawable(R.drawable.ic_info_black_24px));
            SubActionButton info = itemBuilder.setContentView(infoIcon).build();
            info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBarcodeReader(SCAN_INFO);
                }
            });

            FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(fridge)
                    .addSubActionView(allergy)
                    .addSubActionView(info)
                    .attachTo(actionButton)
                    .build();
        }
    }

    private void startBarcodeReader(String extra) {
        Intent intent = new Intent(MainActivity.this,BarcodeReaderActivity.class);
        intent.putExtra(SCAN_TYPE_EXTRA,extra);
        startActivity(intent);
    }

    private void setEmailNav() {
        View header = navigationView.getHeaderView(0);
        TextView email_nav = (TextView)header.findViewById(R.id.email_nav);
        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String user_email = preferences.getString(USER_EMAIL_PREFS,"");
        email_nav.setText(user_email);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startSettingsActivity();
        }else if (id == R.id.action_logout){
            logout();
        }else if (id == R.id.action_add_fridge){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.add_fridge_title);
            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            // Set up the buttons
            builder.setPositiveButton(R.string.add_fridge_addBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    fridge_name = input.getText().toString();
                    addFridge();
                }
            });
            builder.setNegativeButton(R.string.add_fridge_cancelBtn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private int getUserId() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    private void addFridge() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FRIDGE_ADD_URL,
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
                            // TODO: 30-11-16
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            Toast.makeText(getApplicationContext(),R.string.add_fridge_success,Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getApplicationContext(),R.string.add_fridge_error,Toast.LENGTH_LONG).show();
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
                String apiKey = getApiKey();
                int user_id = getUserId();
                Map<String,String> params = new HashMap<>();
                String jwt = signParamsAdd(fridge_name,user_id,apiKey);
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest,ADD_FRIDGE_REQUEST_TAG);
    }

    private String signParamsAdd(String fridge_name, int user_id, String apiKey) {
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();

        claims.put(KEY_USERID, String.valueOf(user_id));
        claims.put(KEY_API_KEY, apiKey);
        claims.put(KEY_FRIDGE_NAME,fridge_name);
        return jwt = signer.sign(claims);
    }

    private String getApiKey() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_fridges) {
            // TODO: 29-10-16
        } else if (id == R.id.nav_input) {
            // TODO: 29-10-16
        }else if (id == R.id.nav_allergy){
            // TODO: 28-11-16
        }else if (id == R.id.nav_manage) {
            startSettingsActivity();
        } else if (id == R.id.nav_shop) {
            startShopActivity();
        }else if (id == R.id.nav_profile){
            startProfileActivity();
        }else if (id == R.id.nav_contact){
            startContactActivity();
        }else if (id == R.id.nav_about){
            startAboutActivity();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startAboutActivity() {
        Intent intent = new Intent(MainActivity.this,AboutActivity.class);
        startActivity(intent);
    }

    private void startContactActivity() {
        Intent intent = new Intent(MainActivity.this,ContactActivity.class);
        startActivity(intent);
    }

    private void startShopActivity() {
        Intent intent = new Intent(MainActivity.this,ShopActivity.class);
        startActivity(intent);
    }

    private void startProfileActivity() {
        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(intent);
    }


    private void logout() {
        //Creating an alert dialog to confirm logout
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.logout_confirm);
        alertDialogBuilder.setPositiveButton(R.string.logout_positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        finishAllActivities();
                        startLoginActivty();
                    }
                });

        alertDialogBuilder.setNegativeButton(R.string.logout_negative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void finishAllActivities() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("ovh.intellifridge.ACTION_LOGOUT");
        sendBroadcast(broadcastIntent);
    }

    private void startLoginActivty() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public Boolean getFridgeModStatus() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean(MOD_FRIDGE_KEY,true);
    }
    public Boolean getAllergyModStatus(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean(MOD_ALLERGY_KEY,true);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (getFridgeModStatus() && !getAllergyModStatus()){
                switch (position){
                    case TAB_RECENT_FRIDGE:
                        return new RecentFragment();
                    case TAB_FRIDGE_FRIDGE:
                        return new FridgeFragment();
                    default:
                        return null;
                }
            }else if (!getFridgeModStatus() && getAllergyModStatus()){
                switch (position){
                    case TAB_ALLERGY_ALLERGY:
                        return new AllergyFragment();
                    case TAB_MAPS_ALLERGY:
                        return new MapsFragment();
                    default:
                        return null;
                }
            }else {
                switch (position){
                    case TAB_RECENT_DEFAULT:
                        return new RecentFragment();
                    case TAB_FRIDGE_DEFAULT:
                        return new FridgeFragment();
                    case TAB_ALLERGY_DEFAULT:
                        return new AllergyFragment();
                    case TAB_MAPS_DEFAULT:
                        return new MapsFragment();
                    default:
                        return null;
                }
            }
        }

        @Override
        public int getCount() {
            int nbTabs;
            if (getFridgeModStatus() && getAllergyModStatus()){
                nbTabs = 4;
            }else{
                nbTabs = 2;
            }
            return nbTabs;
        }

        @Override
        public String getPageTitle(int position) {
            if (getFridgeModStatus() && !getAllergyModStatus()){
                switch (position){
                    case 0:
                        return getString(R.string.section1);
                    case 1:
                        return getString(R.string.section2);
                }
            }else if (getAllergyModStatus() && !getFridgeModStatus()){
                switch (position){
                    case 0:
                        return getString(R.string.section3);
                    case 1:
                        return getString(R.string.section4);
                }
            }else{
                switch (position) {
                    case 0:
                        return getString(R.string.section1);
                    case 1:
                        return getString(R.string.section2);
                    case 2:
                        return getString(R.string.section3);
                    case 3:
                        return getString(R.string.section4);
                }
            }
            return null;
        }
    }
}
