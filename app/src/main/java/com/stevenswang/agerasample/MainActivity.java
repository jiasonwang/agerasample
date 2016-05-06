package com.stevenswang.agerasample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Fragment fragment = null;
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                if (uri.getHost().equals("rxui")) {
                    fragment = new SampleFragment();
                }else if (uri.getHost().equals("rxfunc")){
                    fragment = new SampleFuncFragment();
                }
            }
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
        }
    }

}
