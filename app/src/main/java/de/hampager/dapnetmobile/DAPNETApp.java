package de.hampager.dapnetmobile;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;


public class DAPNETApp extends Application {
    public static final String TAG = "DAPNETApp";

    /*
     * type to use for maps to store in the external files directory
     */
    public static final String MAPS = "maps";

    public static final String SETTING_DEBUG_TIMING = "debug_timing";
    public static final String SETTING_LANGUAGE_SHOWLOCAL = "language_showlocal";
    public static final String SETTING_PREFERRED_LANGUAGE = "language_selection";
    public static final String SETTING_RENDERING_THREADS = "rendering_threads";
    public static final String SETTING_SCALE = "scale";
    public static final String SETTING_TEXTWIDTH = "textwidth";
    public static final String SETTING_TILECACHE_PERSISTENCE = "tilecache_persistence";
    public static final String SETTING_WAYFILTERING = "wayfiltering";
    public static final String SETTING_WAYFILTERING_DISTANCE = "wayfiltering_distance";
    @Override public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }
}
