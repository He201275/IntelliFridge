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
import static ovh.intellifridge.intellifridge.Config.SCAN_TYPE_EXTRA;
import static ovh.intellifridge.intellifridge.Config.SERVER_FRIDGE_EXISTS;
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
import static ovh.intellifridge.intellifridge.Config.TITLE_ALLERANCE;
import static ovh.intellifridge.intellifridge.Config.TITLE_IF;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_EMAIL_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

/**
 * @author Francis O. Makokha
 * L'activité principale de l'application
 *
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
    public FridgeFragment ff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getAllergyModStatus() && !getFridgeModStatus()){
            setContentView(R.layout.activity_main_allerance);
            setTitle(TITLE_ALLERANCE);
        }else {
            setContentView(R.layout.activity_main);
            setTitle(TITLE_IF);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

        if (getFridgeModStatus() && getAllergyModStatus()){
            mViewPager.setCurrentItem(TAB_FRIDGE_FRIDGE);
            setEmailNav();
        }else if (getFridgeModStatus() && !getAllergyModStatus()){
            mViewPager.setCurrentItem(TAB_FRIDGE_FRIDGE);
            setEmailNav();
        }

        addFloatingActionMenu();
    }

    /**
     * Gestion du floating action button avec menu en fonction des modules activés
     */
    private void addFloatingActionMenu() {
        // repeat many times:
        if (getFridgeModStatus() && !getAllergyModStatus()){
            ImageView icon = new ImageView(this);
            icon.setImageDrawable(getDrawable(R.drawable.ic_fridge_black));
            FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                    .setBackgroundDrawable(R.drawable.fab_background)
                    .setContentView(icon)
                    .build();

            SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

            ImageView sIcon = new ImageView(this);
            sIcon.setImageDrawable(getDrawable(R.drawable.ic_camera_black));
            SubActionButton sAdd = itemBuilder.setContentView(sIcon).build();
            sAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startBarcodeReader(SCAN_FRIDGE);
                }
            });

            ImageView nsIcon = new ImageView(this);
            nsIcon.setImageDrawable(getDrawable(R.drawable.ic_local_pizza_black_24px));
            SubActionButton nsAdd = itemBuilder.setContentView(nsIcon).build();
            nsAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: 08-12-16
                }
            });

            FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(nsAdd)
                    .addSubActionView(sAdd)
                    .attachTo(actionButton)
                    .build();
        }else if (!getFridgeModStatus() && getAllergyModStatus()){
            ImageView icon = new ImageView(this);
            icon.setImageDrawable(getDrawable(R.drawable.ic_allergy));
            FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                    .setBackgroundDrawable(R.drawable.fab_background_allerance)
                    .setContentView(icon)
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
            FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(fridge)
                    .addSubActionView(allergy)
                    .attachTo(actionButton)
                    .build();
        }
    }

    /**
     * Lance le lecteur de code-barres
     * @param extra
     */
    private void startBarcodeReader(String extra) {
        Intent intent = new Intent(MainActivity.this,BarcodeReaderActivity.class);
        intent.putExtra(SCAN_TYPE_EXTRA,extra);
        startActivity(intent);
    }

    /**
     * Affichage de l'email dans le navigation drawer
     */
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

    /**
     * Gestion du menu
     * @param menu
     * @return
     */
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

    /**
     * Gestion du click au niveau du menu
     * @param item
     * @return
     */
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
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setMessage(R.string.add_fridge_message);
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

    /**
     * {@link BarcodeReaderActivity#getUserId()}
     * @return
     */
    private int getUserId() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    /**
     * Gère l'ajout un frigo
     */
    private void addFridge() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FRIDGE_ADD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String secret = JWT_KEY;
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
                            Toast.makeText(getApplicationContext(),R.string.add_fridge_success,Toast.LENGTH_SHORT).show();
                            ff.onRefresh();
                        }else if(server_status.equals(SERVER_FRIDGE_EXISTS)){
                            Toast.makeText(getApplicationContext(),R.string.add_fridge_error_exists,Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(),R.string.add_fridge_error,Toast.LENGTH_LONG).show();
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
                Map<String,String> params = new HashMap<>();
                String jwt = signParamsAdd(fridge_name,user_id,apiKey);
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest,ADD_FRIDGE_REQUEST_TAG);
    }

    /**
     * {@link BarcodeReaderActivity#signParamsIsInDb(String, String, int)}
     * @param fridge_name
     * @param user_id
     * @param apiKey
     * @return
     */
    private String signParamsAdd(String fridge_name, int user_id, String apiKey) {
        final String secret = JWT_KEY;

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();

        claims.put(KEY_USERID, String.valueOf(user_id));
        claims.put(KEY_API_KEY, apiKey);
        claims.put(KEY_FRIDGE_NAME,fridge_name);
        return signer.sign(claims);
    }

    /**
     * {@link BarcodeReaderActivity#getApiKey()}
     * @return
     */
    private String getApiKey() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }

    /**
     * Permet de lancer l'activité des paramètres
     */
    private void startSettingsActivity() {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Gestion du click du menu de type navigation drawer
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_input_ns) {
            startProductNSActivity();
        }else if (id == R.id.nav_grocery_list){
            startGroceryListActivity();
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

    /**
     * Permet de lancer l'activité d'ajout de produits non-scannables
     */
    private void startProductNSActivity() {
        Intent intent = new Intent(MainActivity.this,ProductNSActivity.class);
        startActivity(intent);
    }

    /**
     * Permet de lancer l'activité d'ajout de liste de courses
     */
    private void startGroceryListActivity() {
        Intent intent = new Intent(MainActivity.this,GroceryListActivity.class);
        startActivity(intent);
    }

    /**
     * Permet de lancer l'activité "a propos"
     */
    private void startAboutActivity() {
        Intent intent = new Intent(MainActivity.this,AboutActivity.class);
        startActivity(intent);
    }

    /**
     * Permet de lancer l'activité de contact
     */
    private void startContactActivity() {
        Intent intent = new Intent(MainActivity.this,ContactActivity.class);
        startActivity(intent);
    }

    /**
     * Permet de lancer l'activité du shop
     */
    private void startShopActivity() {
        Intent intent = new Intent(MainActivity.this,ShopActivity.class);
        startActivity(intent);
    }

    /**
     * Permet de lancer l'activité "mon profil"
     */
    private void startProfileActivity() {
        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
        startActivity(intent);
    }

    /**
     * Gestion du logout de l'utilisateur
     */
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

    /**
     * Permet de lancer l'activité de login
     */
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
                        ff = new FridgeFragment();
                        return ff;
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
                        ff = new FridgeFragment();
                        return ff;
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
