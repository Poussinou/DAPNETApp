package de.hampager.dapnetmobile.maps;

import android.app.Fragment;

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

import de.hampager.dapnetmobile.R;

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
    protected void createLayers() {
        this.downloadLayer = new TileDownloadLayer(this.tileCaches.get(0),
                this.mapView.getModel().mapViewPosition, OpenStreetMapMapnik.INSTANCE,
                AndroidGraphicFactory.INSTANCE);
        Layers layers = mapView.getLayerManager().getLayers();
        layers.add(this.downloadLayer);
        createMarkers(layers);
        mapView.setZoomLevelMin(OpenStreetMapMapnik.INSTANCE.getZoomLevelMin());
        mapView.setZoomLevelMax(OpenStreetMapMapnik.INSTANCE.getZoomLevelMax());
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
