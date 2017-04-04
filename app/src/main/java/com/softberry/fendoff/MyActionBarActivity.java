package com.softberry.fendoff;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Ramzan on 13/03/2015.
 */
public class MyActionBarActivity  extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.support.v7.app.ActionBar actionBar =  getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.kirp)));
        actionBar.setTitle("");
        actionBar.setElevation(0);

    }

}
