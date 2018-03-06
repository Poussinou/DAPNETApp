package de.hampager.dapnetmobile.maps;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik;
import org.mapsforge.map.layer.labels.LabelLayer;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.rendertheme.XmlRenderTheme;

import java.util.ArrayList;

import de.hampager.dapnetmobile.R;
import de.hampager.dapnetmobile.api.HamPagerService;
import de.hampager.dapnetmobile.api.ServiceGenerator;
import de.hampager.dapnetmobile.api.TransmitterResource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static de.hampager.dapnetmobile.DAPNETApp.TAG;

public class DownloadLayerViewer extends MapsBaseFragment {
    protected TileDownloadLayer downloadLayer;
    public static DownloadLayerViewer newInstance() {
        DownloadLayerViewer f = new DownloadLayerViewer();

        // Supply index input as an argument.
        /*
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
*/
        return f;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        configMap();
        return super.onCreateView(inflater,container,savedInstanceState);
    }
    private void configMap(){
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
        String server = sharedPref.getString("server", "http://www.hampager.de:8080");
        String user = sharedPref.getString("user", "invalid");
        String password = sharedPref.getString("pass", "invalid");
        fetchJSON(server,user,password);
    }
    private void fetchJSON(String server, String user, String password) {
        try {
            ServiceGenerator.changeApiBaseUrl(server);
        } catch (java.lang.NullPointerException e) {
            ServiceGenerator.changeApiBaseUrl("http://www.hampager.de:8080");
        }
        HamPagerService service = ServiceGenerator.createService(HamPagerService.class, user, password);
        Call<ArrayList<TransmitterResource>> call;
        call=service.getAllTransmitter();
        call.enqueue(new Callback<ArrayList<TransmitterResource>>() {
            @Override
            public void onResponse(Call<ArrayList<TransmitterResource>> call, Response<ArrayList<TransmitterResource>> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Connection was successful");
                    // tasks available
                    ArrayList<TransmitterResource> data = response.body();
                    Context c=getContext();
                    for(TransmitterResource t: data){
                        //OverlayItem temp = new OverlayItem(t.getName(), getDesc(t), new GeoPoint(t.getLatitude(), t.getLongitude()));
                        if (t.getUsage().equals("WIDERANGE")){
                            if(t.getStatus().equals("ONLINE")){
                                Marker mark= Utils.createTappableMarker(c,R.mipmap.ic_marker_transmitter_online,new LatLong(t.getLatitude(),t.getLongitude()));
                                mapView.getLayerManager().getLayers().add(mark);
                                //onlineWide.add(onlineWide.size(),temp);
                            }else{
                                //offlineWide.add(offlineWide.size(),temp);
                            }
                        }else{
                            if(t.getStatus().equals("ONLINE")){
                                //onlinePers.add(onlinePers.size(),temp);
                            }else{
                               // offlinePers.add(offlinePers.size(),temp);
                            }
                        }
                    }
                    // config();
                } else {
                    Log.e(TAG, "Error " + response.code());
                    Log.e(TAG, response.message());
                    if (response.code() == 401) {
                        SharedPreferences sharedPref = getActivity().getSharedPreferences("sharedPref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TransmitterResource>> call, Throwable t) {
                // something went completely wrong (e.g. no internet connection)
                Log.e(TAG, t.getMessage());
            }
        });
    }
    private String getDesc(TransmitterResource TrRe) {
        StringBuilder s = new StringBuilder();
        String dot = ": ";
        Context res = getContext();
        s.append(res.getString(R.string.type));
        s.append(dot);
        s.append(TrRe.getUsage());
        s.append("\n");
        s.append(res.getString(R.string.transmission_power));
        s.append(dot);
        s.append(Double.toString(TrRe.getPower()));
        s.append("\n");
        if (TrRe.getTimeSlot().length() > 1) s.append(res.getString(R.string.timeslots));
        else s.append(res.getString(R.string.timeslot));
        s.append(dot);
        s.append(TrRe.getTimeSlot());
        s.append("\n");
        if (TrRe.getOwnerNames().size() > 1) s.append(res.getString(R.string.owners));
        else s.append(res.getString(R.string.owner));
        s.append(dot);
        for (String temp : TrRe.getOwnerNames()) {
            s.append(temp).append(" ");
        }
        return s.toString();
    }
    @Override
    protected void createLayers() {
        this.downloadLayer = new TileDownloadLayer(this.tileCaches.get(0),
                this.mapView.getModel().mapViewPosition, OpenStreetMapMapnik.INSTANCE,
                AndroidGraphicFactory.INSTANCE);
        Layers layers = mapView.getLayerManager().getLayers();
        layers.add(this.downloadLayer);
        createMarkers(layers);
        mapView.setZoomLevelMin(OpenStreetMapMapnik.INSTANCE.getZoomLevelMin());
        mapView.setZoomLevelMax(OpenStreetMapMapnik.INSTANCE.getZoomLevelMax());
        System.out.println("MIN: "+OpenStreetMapMapnik.INSTANCE.getZoomLevelMin()+" MAX: "+OpenStreetMapMapnik.INSTANCE.getZoomLevelMax());

        mapView.getModel().mapViewPosition.setMapPosition(
                new MapPosition(new LatLong(50.77623, 6.06937), (byte) 4));

    }

    private void createMarkers(Layers layers){
        LatLong latLong1 = new LatLong(52.5, 13.4);
        Marker marker1 = Utils.createTappableMarker(getContext(),
                R.mipmap.ic_radiotower_red, latLong1);
        mapView.getLayerManager().getLayers().add(marker1);

    }
    @Override
    protected MapView createMapViews() {
        super.createMapViews();
        // we need to set a fixed size tile as the raster tiles come at a fixed size and not being blurry
        //this.mapView.getModel().displayModel.setFixedTileSize(256);
        return mapView;
    }

    @Override
    protected MapPosition getInitialPosition() {
        return new MapPosition(new LatLong(0, 0), (byte) 2);
    }

    @Override
    protected XmlRenderTheme getRenderTheme() {
        // no render theme needed here
        return null;
    }

    @Override
    protected byte getZoomLevelMax() {
        return mapView.getModel().mapViewPosition.getZoomLevelMax();
    }

    @Override
    protected byte getZoomLevelMin() {
        return mapView.getModel().mapViewPosition.getZoomLevelMin();
    }

    @Override
    public void onPause() {
        this.downloadLayer.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.downloadLayer.onResume();
    }
}
