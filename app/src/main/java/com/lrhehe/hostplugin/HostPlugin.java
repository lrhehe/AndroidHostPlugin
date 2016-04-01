package com.lrhehe.hostplugin;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ray
 * @Date 3/23/16.
 */
public class HostPlugin {

    private static final String TAG = "HostPlugin";
    private static Context sContext;
    private static String sPluginsDir;
    private static PluginLoader sPluginLoader;

    private static List<File> sPluginFiles = new ArrayList<>();

    public static void init(Context context) {
        init(context, null);
    }

    public static void init(Context context, PluginLoader pluginLoader) {
            sContext = context;
            File file = sContext.getFileStreamPath("plugins");
            if (!file.exists() && !file.mkdir()) {
                Log.e(TAG, "create plugins dir fail");
                return;
            }
            sPluginsDir = file.getAbsolutePath();
            if (pluginLoader == null) {
                sPluginLoader = new PluginLoader(context);
            } else {
                sPluginLoader = pluginLoader;
            }
    }


    public static void loadPlugins() {
        copyPluginsToLocal();

        for (File plugin : sPluginFiles) {
            sPluginLoader.load(plugin);
        }
    }

    private static void copyPluginsToLocal() {
        AssetManager assetManager = sContext.getAssets();
        try {
            for (String pluginName : assetManager.list("plugins")) {
                InputStream inputStream = assetManager.open("plugins/" + pluginName);
                File pluginFile = new File(sPluginsDir, pluginName);
                if (!pluginFile.exists()) {
                    pluginFile.createNewFile();
                }
                OutputStream outputStream = new FileOutputStream(pluginFile);
                byte[] buffer = new byte[1024 * 4];
                int n;
                while (-1 != (n = inputStream.read(buffer))) {
                    outputStream.write(buffer, 0, n);
                }
                inputStream.close();
                outputStream.close();
                sPluginFiles.add(pluginFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
