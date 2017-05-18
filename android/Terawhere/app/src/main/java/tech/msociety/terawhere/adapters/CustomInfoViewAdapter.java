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

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        final TextView viewMoreTextView = (TextView) popup.findViewById(R.id.textViewViewMore);

        Offer objOffer = mapLocationOffer.get(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));


        String meetUpTime = "";
        try {
            meetUpTime = new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").parse(objOffer.getMeetUpTime().toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        startingLocationTextView.setText(Html.fromHtml("<b>Meeting point:</b>" + "<br/>" + objOffer.getStartingLocationAddress()));
        endingLocationTextView.setText((Html.fromHtml("<b>Destination:</b>" + "<br/>" + objOffer.getEndingLocationAddress())));
        meetUpTimeTextView.setText((Html.fromHtml("<b>Pick Up Time:</b>" + "<br/>" + meetUpTime)));
        seatsAvailableTextView.setText((Html.fromHtml("<b>Seats Left: </b>" + "<br/>" + Integer.toString(objOffer.getSeatsAvailable()))));
        SpannableString content = new SpannableString("VIEW MORE");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        viewMoreTextView.setText(content);



        return popup;
    }
}
