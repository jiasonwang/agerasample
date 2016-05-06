package com.stevenswang.agerasample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wolun on 16/5/6.
 */
public class SampleFuncFragment extends Fragment {
    private View mRootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.list_ly,container,false);

        return mRootView;
    }

    protected <T extends View> T getView(int id, View parent) {
        return (T) parent.findViewById(id);
    }


}
