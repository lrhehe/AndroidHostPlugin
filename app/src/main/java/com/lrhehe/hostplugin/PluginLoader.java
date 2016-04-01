package com.lrhehe.hostplugin;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Pair;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ray
 * @Date 3/31/16.
 */
public class PluginLoader {

    protected static Instrumentation sHostInstrumentation;

    private static final String FD_STORAGE = "storage";
    private static final String FD_LIBRARY = "lib";
    private static Context mContext;
    private static final String FILE_DEX = "bundle.dex";

    /**
     * Class for redirect activity from Stub(AndroidManifest.xml) to Real(Plugin)
     */
    private static class InstrumentationWrapper extends Instrumentation {

        public InstrumentationWrapper() {}

        @Override
        public void callActivityOnCreate(Activity activity, android.os.Bundle icicle) {
            String activityName = activity.getClass().getName();
            if (mActivityResourcesMap.containsKey(activityName)) {
                ActivityInfo activityInfo = mActivityResourcesMap.get(activityName).first;
                applyActivityInfo(activity, activityInfo);
            }
            super.callActivityOnCreate(activity, icicle);
        }

        @Override
        public Activity newActivity(ClassLoader cl, String className, Intent intent)
                throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            Activity activity = super.newActivity(cl, className, intent);
            String activityName = activity.getClass().getName();
            if (mActivityResourcesMap.containsKey(activityName)) {
                Resources resources = mActivityResourcesMap.get(activityName).second;
                ensureAddAssetPath(activity, resources);
            }
            return activity;
        }
    }

    public PluginLoader(Context context) {
        mContext = context;
        // Inject instrumentation
        if (sHostInstrumentation == null) {
            try {
                final Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
                final Method method = activityThreadClass.getMethod("currentActivityThread");
                Object thread = method.invoke(null, (Object[]) null);
                Field field = activityThreadClass.getDeclaredField("mInstrumentation");
                field.setAccessible(true);
                sHostInstrumentation = (Instrumentation) field.get(thread);
                Instrumentation wrapper = new InstrumentationWrapper();
                field.set(thread, wrapper);

                // if (mContext instanceof Activity) {
                // field = Activity.class.getDeclaredField("mInstrumentation");
                // field.setAccessible(true);
                // field.set(mContext, wrapper);
                // }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Map<String, Pair<ActivityInfo, Resources>> mActivityResourcesMap = new HashMap<>();

    public void load(File plugin) {
        String pluginPath = plugin.getPath();
        PackageManager pm = MyApplication.getInstance().getPackageManager();
        PackageInfo pluginInfo = pm.getPackageArchiveInfo(pluginPath,
                PackageManager.GET_ACTIVITIES);
        Resources resources = getPluginResources(pluginPath);
        for (ActivityInfo info : pluginInfo.activities) {
            mActivityResourcesMap.put(info.name, Pair.create(info, resources));
        }

        // load dex
        File packagePath = mContext.getFileStreamPath(FD_STORAGE);
        packagePath = new File(packagePath, pluginInfo.packageName);
        if (!packagePath.exists()) {
            packagePath.mkdirs();
        }
        File libDir = new File(packagePath, FD_LIBRARY);
        File optDexFile = new File(packagePath, FILE_DEX);

        ReflectAccelerator.expandDexPathList(
                mContext.getClassLoader(), pluginPath, libDir.getPath(), optDexFile.getPath());

        // Call bundle application onCreate
        String bundleApplicationName = pluginInfo.applicationInfo.className;
        if (bundleApplicationName != null) {
            try {
                Class applicationClass = Class.forName(bundleApplicationName);
                Application bundleApplication = Instrumentation.newApplication(
                        applicationClass, MyApplication.getInstance());
                sHostInstrumentation.callApplicationOnCreate(bundleApplication);
                ReflectAccelerator.setResources(bundleApplication, resources);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Resources getPluginResources(String pluginPath) {
        AssetManager assetManager = ReflectAccelerator.newAssetManager();
        ReflectAccelerator.addAssetPath(assetManager, pluginPath);
        Resources superRes = mContext.getResources();
        return new Resources(assetManager, superRes.getDisplayMetrics(),
                superRes.getConfiguration());
    }

    /**
     * Apply plugin activity info with plugin's AndroidManifest.xml
     * 
     * @param activity
     * @param ai
     */
    private static void applyActivityInfo(Activity activity, ActivityInfo ai) {
        // Apply plugin theme
        ReflectAccelerator.setTheme(activity, null);
        activity.setTheme(ai.getThemeResource());
        // Apply plugin softInputMode
        activity.getWindow().setSoftInputMode(ai.softInputMode);
    }

    private static void ensureAddAssetPath(Activity activity, Resources resources) {
        // Replace resources for application and activity
//        ReflectAccelerator.setResources(activity.getApplication(), resources);
        ReflectAccelerator.setResources(activity, resources);
    }
}
