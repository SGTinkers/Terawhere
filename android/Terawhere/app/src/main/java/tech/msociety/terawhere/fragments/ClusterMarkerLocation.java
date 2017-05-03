package tech.msociety.terawhere.fragments;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;


/**
 * Created by musa on 2/5/17.
 */

public class ClusterMarkerLocation implements ClusterItem {

    private LatLng mPosition;

    public ClusterMarkerLocation( LatLng latLng ) {
        mPosition = latLng;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public void setmPosition(LatLng mPosition) {
        this.mPosition = mPosition;
    }
}