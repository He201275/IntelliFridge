package com.intellifridge.intellifridge;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Sofiane on 04-10-16.
 */

public class pagerAdapter extends FragmentPagerAdapter {
    String[] menuTitle;

   

    public pagerAdapter(android.support.v4.app.FragmentManager fragmentManager, Context applicationContext) {
        super(fragmentManager);
        menuTitle = applicationContext.getResources().getStringArray(R.array.MenuList);
    }




    public android.support.v4.app.Fragment getItem(int i){
        Bundle args = new Bundle();
        args.putString(MainFragment.ARG_PARAM1, "home");
        MainFragment mFragment = new MainFragment();
        mFragment.setArguments(args);
        return mFragment;

        /*switch (i){
            /*Recettes
            case 0: getReciepes();
                break;
            /*home
            case 1: getHomePage();
                break;
            /*fridges
            case 2: getFridgesList();
                break;

        }*/
    }

    private void getReciepes() {
    }
    private void getHomePage() {
    }
    private void getFridgesList() {
    }

    @Override
    public int getCount() {

        return menuTitle.length;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        Log.d("test", menuTitle[position]);
        return menuTitle[position];
    }
}
