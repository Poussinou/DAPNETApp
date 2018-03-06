package de.hampager.dapnetmobile.maps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.mapsforge.core.model.Dimension;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.core.util.Parameters;
import org.mapsforge.map.android.input.MapZoomControls;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.MapWorkerPool;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.scalebar.ImperialUnitAdapter;
import org.mapsforge.map.scalebar.MetricUnitAdapter;
import org.mapsforge.map.scalebar.NauticalUnitAdapter;

import java.io.File;

import de.hampager.dapnetmobile.DAPNETApp;
import de.hampager.dapnetmobile.R;

public abstract class MapsBaseFragment extends MapViewerTemplateFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String SETTING_SCALEBAR = "scalebar";
    public static final String SETTING_SCALEBAR_METRIC = "metric";
    public static final String SETTING_SCALEBAR_IMPERIAL = "imperial";
    public static final String SETTING_SCALEBAR_NAUTICAL = "nautical";
    public static final String SETTING_SCALEBAR_BOTH = "both";
    public static final String SETTING_SCALEBAR_NONE = "none";

    protected static final int DIALOG_ENTER_COORDINATES = 2923878;
    protected SharedPreferences sharedPreferences;

    @Override
    protected int getLayoutId() {
        return R.layout.mapviewer;
    }

    @Override
    protected int getMapViewId() {
        return R.id.mapView;
    }

    @Override
    protected MapPosition getInitialPosition() {
        int tileSize = this.mapView.getModel().displayModel.getTileSize();
        byte zoomLevel = LatLongUtils.zoomForBounds(new Dimension(tileSize * 4, tileSize * 4), getMapFile().boundingBox(), tileSize);
        return new MapPosition(getMapFile().boundingBox().getCenterPoint(), zoomLevel);
    }

    @Override
    protected void createLayers() {
        TileRendererLayer tileRendererLayer = AndroidUtil.createTileRendererLayer(this.tileCaches.get(0),
                mapView.getModel().mapViewPosition, getMapFile(), getRenderTheme(), false, true, false,
                getHillsRenderConfig());
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

        // needed only for samples to hook into Settings.
        setMaxTextWidthFactor();
    }

    @Override
    protected void createControls() {
        super.createControls();
        setMapScaleBar();
    }

    @Override
    protected MapView createMapViews() {
        super.createMapViews();

        mapView.getMapZoomControls().setZoomControlsOrientation(MapZoomControls.Orientation.VERTICAL_IN_OUT);
        mapView.getMapZoomControls().setZoomInResource(R.drawable.zoom_control_in);
        mapView.getMapZoomControls().setZoomOutResource(R.drawable.zoom_control_out);
        mapView.getMapZoomControls().setMarginHorizontal(getResources().getDimensionPixelOffset(R.dimen.controls_margin));
        mapView.getMapZoomControls().setMarginVertical(getResources().getDimensionPixelOffset(R.dimen.controls_margin));
        return mapView;
    }

    @Override
    protected void createTileCaches() {
        boolean persistent = sharedPreferences.getBoolean(DAPNETApp.SETTING_TILECACHE_PERSISTENCE, true);
        this.tileCaches.add(AndroidUtil.createTileCache(getContext(), getPersistableId(),
                this.mapView.getModel().displayModel.getTileSize(), this.getScreenRatio(),
                this.mapView.getModel().frameBufferModel.getOverdrawFactor(), persistent));
    }

    @Override
    protected String getMapFileName() {
        //TODO Proper launchUrl
        //String mapfile = (Samples.launchUrl == null) ? null : Samples.launchUrl.getQueryParameter("mapfile");
        String mapfile="bleb";
        if (mapfile != null) {
            return mapfile;
        }
        return "germany.map";
    }

    @Override
    protected File getMapFileDirectory() {
        //TODO: Proper launchUrl
//        String mapdir = (Samples.launchUrl == null) ? null : Samples.launchUrl.getQueryParameter("mapdir");
        String mapdir="/Download/bleb";
        if (mapdir != null) {
            File file = new File(mapdir);
            if (file.exists() && file.isDirectory()) {
                return file;
            }
            //TODO: Proper LaunchUrl
            throw new RuntimeException(file + " does not exist or is not a directory (configured in launch URI " + " )");
        }
        return super.getMapFileDirectory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setTitle(getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        this.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    /*
     * Settings related methods.
     */

    @Override
    protected void createSharedPreferences() {
        super.createSharedPreferences();

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        // problem that the first call to getAll() returns nothing, apparently the
        // following two calls have to be made to read all the values correctly
        // http://stackoverflow.com/questions/9310479/how-to-iterate-through-all-keys-of-shared-preferences
        this.sharedPreferences.edit().clear();
        PreferenceManager.setDefaultValues(getContext(), R.xml.preferences, true);

        this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mapfilter, menu);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.online_filter:
            case R.id.offline_filter:
            case R.id.widerange_filter:
            case R.id.personal_filter:
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
        if (DAPNETApp.SETTING_SCALE.equals(key)) {
            this.mapView.getModel().displayModel.setUserScaleFactor(DisplayModel.getDefaultUserScaleFactor());
            Log.d(DAPNETApp.TAG, "Tilesize now " + this.mapView.getModel().displayModel.getTileSize());
            AndroidUtil.restartActivity(this.getActivity());
        }
        if (DAPNETApp.SETTING_PREFERRED_LANGUAGE.equals(key)) {
            String language = preferences.getString(DAPNETApp.SETTING_PREFERRED_LANGUAGE, null);
            Log.d(DAPNETApp.TAG, "Preferred language now " + language);
            AndroidUtil.restartActivity(this.getActivity());
        }
        if (DAPNETApp.SETTING_TILECACHE_PERSISTENCE.equals(key)) {
            if (!preferences.getBoolean(DAPNETApp.SETTING_TILECACHE_PERSISTENCE, false)) {
                Log.d(DAPNETApp.TAG, "Purging tile caches");
                for (TileCache tileCache : this.tileCaches) {
                    tileCache.purge();
                }
            }
            AndroidUtil.restartActivity(this.getActivity());
        }
        if (DAPNETApp.SETTING_TEXTWIDTH.equals(key)) {
            AndroidUtil.restartActivity(this.getActivity());
        }
        if (SETTING_SCALEBAR.equals(key)) {
            setMapScaleBar();
        }
        if (DAPNETApp.SETTING_DEBUG_TIMING.equals(key)) {
            MapWorkerPool.DEBUG_TIMING = preferences.getBoolean(DAPNETApp.SETTING_DEBUG_TIMING, false);
        }
        if (DAPNETApp.SETTING_RENDERING_THREADS.equals(key)) {
            Parameters.NUMBER_OF_THREADS = preferences.getInt(DAPNETApp.SETTING_RENDERING_THREADS, 1);
            AndroidUtil.restartActivity(this.getActivity());
        }
        if (DAPNETApp.SETTING_WAYFILTERING_DISTANCE.equals(key) ||
                DAPNETApp.SETTING_WAYFILTERING.equals(key)) {
            MapFile.wayFilterEnabled = preferences.getBoolean(DAPNETApp.SETTING_WAYFILTERING, true);
            if (MapFile.wayFilterEnabled) {
                MapFile.wayFilterDistance = preferences.getInt(DAPNETApp.SETTING_WAYFILTERING_DISTANCE, 20);
            }
        }
    }

    /**
     * Sets the scale bar from preferences.
     */
    protected void setMapScaleBar() {
        String value = this.sharedPreferences.getString(SETTING_SCALEBAR, SETTING_SCALEBAR_BOTH);

        if (SETTING_SCALEBAR_NONE.equals(value)) {
            AndroidUtil.setMapScaleBar(this.mapView, null, null);
        } else {
            if (SETTING_SCALEBAR_BOTH.equals(value)) {
                AndroidUtil.setMapScaleBar(this.mapView, MetricUnitAdapter.INSTANCE, ImperialUnitAdapter.INSTANCE);
            } else if (SETTING_SCALEBAR_METRIC.equals(value)) {
                AndroidUtil.setMapScaleBar(this.mapView, MetricUnitAdapter.INSTANCE, null);
            } else if (SETTING_SCALEBAR_IMPERIAL.equals(value)) {
                AndroidUtil.setMapScaleBar(this.mapView, ImperialUnitAdapter.INSTANCE, null);
            } else if (SETTING_SCALEBAR_NAUTICAL.equals(value)) {
                AndroidUtil.setMapScaleBar(this.mapView, NauticalUnitAdapter.INSTANCE, null);
            }
        }
    }

    /**
     * sets the value for breaking line text in labels.
     */
    protected void setMaxTextWidthFactor() {
        mapView.getModel().displayModel.setMaxTextWidthFactor(Float.valueOf(sharedPreferences.getString(DAPNETApp.SETTING_TEXTWIDTH, "0.7")));
    }

}
