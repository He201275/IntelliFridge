package com.intellifridge.intellifridge;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sofiane on 05-10-16.
 */

public class FragmentTab1 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancesState){
        return inflater.inflate(R.layout.fragment_tab_1, container, false);
    }

}
