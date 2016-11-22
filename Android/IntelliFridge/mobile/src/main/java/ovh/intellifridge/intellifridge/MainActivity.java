package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static ovh.intellifridge.intellifridge.Config.USER_EMAIL_PREFS;

/**
 * L'activité principale de l'application
 * Contient les onglets
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{

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
    public Boolean fridge_mod_status,allergy_mod_status;
    NavigationView navigationView;
    String fridge_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setEmailNav();

        fridge_mod_status = getFridgeModStatus();
        allergy_mod_status = getAllergyModStatus();
        //mViewPager.setCurrentItem(1);
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
                    String type = "add_fridge";
                    int userId = getUserId();
                    // TODO: 22-11-16  
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int userId = prefs.getInt("user_id",0);
        return userId;
    }

    private void addFridge() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int userId = prefs.getInt("user_id",0);
        String userId_string = String.valueOf(userId);
        String type = "add_fridge";
        // TODO: 22-11-16  
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                Intent intent = new Intent(MainActivity.this, BarcodeReader.class);
                startActivity(intent);
                break;
        }
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
        } else if (id == R.id.nav_allergy) {
            // TODO: 29-10-16
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
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);
                        editor.putString(USER_EMAIL_PREFS, "");
                        editor.apply();

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

    private void startLoginActivty() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public Boolean getFridgeModStatus() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean(SettingsActivity.MOD_FRIDGE_KEY,true);
    }
    public Boolean getAllergyModStatus(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean(SettingsActivity.MOD_ALLERGY_KEY,true);
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
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            int nbTabs;
            if (getFridgeModStatus() && !getAllergyModStatus()){
                nbTabs = 2;
            }else if (getAllergyModStatus() && !getFridgeModStatus()){
                nbTabs = 1;
            }else{
                nbTabs = 3;
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
                }
            }else{
                switch (position) {
                    case 0:
                        return getString(R.string.section1);
                    case 1:
                        return getString(R.string.section2);
                    case 2:
                        return getString(R.string.section3);
                }
            }
            return null;
        }
    }
}
