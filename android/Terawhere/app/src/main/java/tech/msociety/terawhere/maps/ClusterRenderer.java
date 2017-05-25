package tech.msociety.terawhere.maps;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import tech.msociety.terawhere.R;

public class ClusterRenderer extends DefaultClusterRenderer<ClusterMarkerLocation> {

    public ClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarkerLocation> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarkerLocation item, MarkerOptions markerOptions) {

        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.pin_location);
        markerOptions.icon(markerDescriptor);
    }

    @Override
    protected void onClusterItemRendered(ClusterMarkerLocation clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        marker.setTag(clusterItem.getOffer());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ClusterMarkerLocation> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<ClusterMarkerLocation> cluster) {
        return cluster.getSize() > 1;
    }}
