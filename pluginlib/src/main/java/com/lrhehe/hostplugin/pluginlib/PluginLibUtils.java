package com.lrhehe.hostplugin.pluginlib;

import android.content.Context;
import android.widget.Toast;

/**
 * @Author ray
 * @Date 3/31/16.
 */
public class PluginLibUtils {

    public static void toast(Context context, String tip) {
        Toast.makeText(context, tip, Toast.LENGTH_LONG).show();
    }
}
