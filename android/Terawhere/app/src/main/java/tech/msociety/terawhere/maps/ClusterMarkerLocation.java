package tech.msociety.terawhere.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


/**
 * Created by musa on 2/5/17.
 */

public class ClusterMarkerLocation implements ClusterItem {

    private LatLng mPosition;
    private int mId;

    public ClusterMarkerLocation(int id, LatLng latLng) {
        mPosition = latLng;
        mId = id;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public int getId() {
        return mId;
    }
    public void setPosition(LatLng mPosition) {
        this.mPosition = mPosition;
    }
}