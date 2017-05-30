package com.example.user.lovemessages;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by User on 08/03/2017.
 */

public class RMS {
    private final String VERSION_APP = "version";
    private final String LAUNCH_APP = "launch_app";

    private Context mContext;
    public SharedPreferences mSharePreference;
    private static RMS sInstance;
    private int mVersionApp = 1;
    private int mLaunch_app = 0;

    public static RMS getInstance() {
        if (sInstance == null) {
            sInstance = new RMS();
        }
        return sInstance;
    }

    public void init(Context context) {
        mContext = context;
        mSharePreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void load() {
        mVersionApp = mSharePreference.getInt(VERSION_APP, mVersionApp);
        mLaunch_app = mSharePreference.getInt(LAUNCH_APP, mLaunch_app);
    }

    public int getVersionApp() {
        return mVersionApp;
    }

    public void setVersionApp(int mVersionApp) {
        this.mVersionApp = mVersionApp;
    }

    public int getLaunch_app() {
        return mLaunch_app;
    }

    public void setLaunch_app(int mLaunch_app) {
        this.mLaunch_app = mLaunch_app;
    }

    public void saveVersion(int version) {
        mVersionApp = version;
        SharedPreferences.Editor editor = mSharePreference.edit();
        editor.putInt(VERSION_APP, mVersionApp);
        editor.commit();
    }

    public void saveLauchApp(int lauch_app) {
        mLaunch_app = lauch_app;
        SharedPreferences.Editor editor = mSharePreference.edit();
        editor.putInt(LAUNCH_APP, mLaunch_app);
        editor.commit();
    }

    public boolean isFirstLaunchApp() {
        return mLaunch_app == 0;
    }

    public void increaseNumberOfLaunchApp() {
        mLaunch_app++;
        SharedPreferences.Editor editor = mSharePreference.edit();
        editor.putInt(LAUNCH_APP, mLaunch_app);
        editor.commit();
    }
}
