package tech.msociety.terawhere.adapters;

import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Date;
import java.util.HashMap;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.utils.DateUtils;

public class OfferInfoViewAdapter implements GoogleMap.InfoWindowAdapter {
    private final LayoutInflater mInflater;

    private HashMap<LatLng, Offer> mapLocationOffer;

    View popup;
    
    public OfferInfoViewAdapter(LayoutInflater inflater, HashMap<LatLng, Offer> map) {
        this.mInflater = inflater;
        mapLocationOffer = map;
    }
    
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
    
    @Override
    public View getInfoContents(Marker marker) {
        popup = mInflater.inflate(R.layout.map_info_view, null);
        final TextView endingLocationTextView = (TextView) popup.findViewById(R.id.textViewEndingLocation);
        final TextView meetUpTimeTextView = (TextView) popup.findViewById(R.id.textViewMeetUpTime);
        final TextView seatsAvailableTextView = (TextView) popup.findViewById(R.id.textViewSeatsAvailable);
    
        Offer offer = mapLocationOffer.get(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));

        String destination = offer.getEndTerawhereLocation().getName();
        if (destination == null || destination.isEmpty()) {
            destination = offer.getEndTerawhereLocation().getAddress();
        } else {
            destination = "To: " + destination;
        }
        endingLocationTextView.setText(destination);

        meetUpTimeTextView.setText("Later at " + DateUtils.toFriendlyTimeString(offer.getMeetupTime()));
        String color = "#4CAF50";
        if (offer.getVacancy() == 1) {
            color = "#F44336";
        }
        seatsAvailableTextView.setText(Html.fromHtml("<font color='" + color + "'>" + offer.getVacancy() + "</font> seat" + (offer.getVacancy() > 1 ? "s" : "") + " left"));
        
        return popup;
    }
}
