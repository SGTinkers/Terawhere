package tech.msociety.terawhere.fragments;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


/**
 * Created by musa on 2/5/17.
 */

public class ClusterMarkerLocation implements ClusterItem {

    private LatLng mPosition;
    private String mId;
    public ClusterMarkerLocation( String id, LatLng latLng) {
        mPosition = latLng;
        mId = id;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public String getId() {
        return mId;
    }
    public void setmPosition(LatLng mPosition) {
        this.mPosition = mPosition;
    }
}