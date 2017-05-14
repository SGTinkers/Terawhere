package tech.msociety.terawhere.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Offer;

/**
 * Created by musa on 4/5/17.
 */

public class CustomInfoViewAdapter implements GoogleMap.InfoWindowAdapter {
    private final LayoutInflater mInflater;
    private HashMap<LatLng, Offer> mapLocationOffer;
    View popup;

    public CustomInfoViewAdapter(LayoutInflater inflater, HashMap<LatLng, Offer> map) {
        this.mInflater = inflater;
        mapLocationOffer = map;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        popup = mInflater.inflate(R.layout.custom_info_window, null);
        //((TextView) popup.findViewById(R.id.nameTextView)).setText("HAFIZ");
        final TextView nameTextView = (TextView) popup.findViewById(R.id.nameTextView);
        final TextView destinationTextView = (TextView) popup.findViewById(R.id.destinationTextView);
        final TextView seatsAvailableTextView = (TextView) popup.findViewById(R.id.seatsAvailableTextView);
        final TextView pickUpTimeTextView = (TextView) popup.findViewById(R.id.pickUpTimeTextView);
        Offer objOffer = mapLocationOffer.get(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

        /*
        destinationTextView.setText(objOffer.getDestination());
        seatsAvailableTextView.setText(Integer.toString(objOffer.getNumberOfSeats()) + " LEFT");
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm a");

        if (objOffer.getMeetUpTime() != null) {
            pickUpTimeTextView.setText(ft.format(objOffer.getMeetUpTime()));
        }
        */
        return popup;
    }
}
