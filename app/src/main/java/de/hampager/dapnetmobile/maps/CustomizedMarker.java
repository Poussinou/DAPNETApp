package de.hampager.dapnetmobile.maps;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.EventLogTags;
import android.widget.Toast;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Marker;


public class CustomizedMarker extends Marker {
    private String description;
    private Bitmap bitmap;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description;
    }

    /**
     * @param latLong          the initial geographical coordinates of this marker (may be null).
     * @param bitmap           the initial {@code Bitmap} of this marker (may be null).
     * @param horizontalOffset the horizontal marker offset.
     * @param verticalOffset   the vertical marker offset.
     */
    public CustomizedMarker(LatLong latLong, Bitmap bitmap, int horizontalOffset, int verticalOffset, String description, Context context) {
        super(latLong, bitmap, horizontalOffset, verticalOffset);
        this.description = description;
        this.bitmap = bitmap;
    }
    @Override
    public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
        //Drawable drawable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? c.getDrawable(resourceIdentifier) : c.getResources().getDrawable(resourceIdentifier);
        //Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        //bitmap.incrementRefCount();
        mapView.getLayerManager().getLayers().add();
        return true;
    }

}
