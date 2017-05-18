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
        final TextView startingLocationTextView = (TextView) popup.findViewById(R.id.textViewStartingLocation);
        final TextView endingLocationTextView = (TextView) popup.findViewById(R.id.textViewEndingLocation);
        final TextView meetUpTimeTextView = (TextView) popup.findViewById(R.id.textViewMeetUpTime);
        final TextView seatsAvailableTextView = (TextView) popup.findViewById(R.id.textViewSeatsAvailable);
        Offer objOffer = mapLocationOffer.get(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

        startingLocationTextView.append("\n" + objOffer.getStartingLocationName());
        endingLocationTextView.append("\n" + objOffer.getEndingLocationName());
        meetUpTimeTextView.append("\n" + objOffer.getMeetUpTime());
        seatsAvailableTextView.append("\n" + Integer.toString(objOffer.getSeatsAvailable()));



        return popup;
    }
}
