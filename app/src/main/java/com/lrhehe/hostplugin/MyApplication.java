package com.lrhehe.hostplugin;

import android.app.Application;

/**
 * @Author ray
 * @Date 3/26/16.
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;

    public static MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HostPlugin.init(this);
        mInstance = this;
    }
}
