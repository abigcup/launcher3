package com.android.launcher3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;


public class FirstScreenAcessActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_first_screen_acess);
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        this.startActivity(intent);
    }


}