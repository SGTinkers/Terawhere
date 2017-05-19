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

import java.util.HashMap;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.utils.DateUtils;

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
        final TextView viewMoreTextView = (TextView) popup.findViewById(R.id.textViewViewMore);
    
        Offer offer = mapLocationOffer.get(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
        
        String meetUpTime = DateUtils.toFriendlyDateTimeString(offer.getMeetupTime());
        
        startingLocationTextView.setText(Html.fromHtml("<b>Meeting point:</b>" + "<br/>" + offer.getStartTerawhereLocation().getAddress()));
        endingLocationTextView.setText((Html.fromHtml("<b>Destination:</b>" + "<br/>" + offer.getEndTerawhereLocation().getAddress())));
        meetUpTimeTextView.setText((Html.fromHtml("<b>Pick Up Time:</b>" + "<br/>" + meetUpTime)));
        seatsAvailableTextView.setText((Html.fromHtml("<b>Seats Left: </b>" + "<br/>" + Integer.toString(offer.getVacancy()))));
        SpannableString content = new SpannableString("VIEW MORE");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        viewMoreTextView.setText(content);
        
        return popup;
    }
}
