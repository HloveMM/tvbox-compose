package com.github.tvbox.osc.base;

import android.app.Activity;
import android.app.Application;

public class App extends Application {
    private static App instance;
    private static Activity currentActivity;

    public static App getInstance() {
        return instance;
    }

    public static Activity getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
