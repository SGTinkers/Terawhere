package tech.msociety.terawhere.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import tech.msociety.terawhere.models.Offer;

public class ClusterMarkerLocation implements ClusterItem {

    private Offer offer;

    private LatLng position;

    public ClusterMarkerLocation(Offer offer, LatLng position) {
        this.position = position;
        this.offer = offer;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public Offer getOffer() {
        return offer;
    }

}