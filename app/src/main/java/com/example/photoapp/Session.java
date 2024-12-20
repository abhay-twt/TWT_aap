package com.example.photoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setLoc(String loc) {
        prefs.edit().putString("Location",loc ).commit();

    }

    public String getLoc() {
        String loc = prefs.getString("Location","");
        return loc;
    }
}
