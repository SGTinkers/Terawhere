package tech.msociety.terawhere.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.globals.AppPrefs;
import tech.msociety.terawhere.globals.TerawhereApplication;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.utils.DateUtils;

public class OfferInfoViewAdapter implements GoogleMap.InfoWindowAdapter {
    private final LayoutInflater mInflater;

    View popup;
    
    public OfferInfoViewAdapter(LayoutInflater inflater) {
        this.mInflater = inflater;
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
        final TextView tapToBookTextView = (TextView) popup.findViewById(R.id.textViewTapToBook);

        Offer offer = (Offer) marker.getTag();

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

        if (AppPrefs.with(TerawhereApplication.ApplicationContext).getUserId().equals(offer.getOffererId())) {
            tapToBookTextView.setVisibility(View.GONE);
        }

        seatsAvailableTextView.setText(Html.fromHtml("<font color='" + color + "'>" + offer.getSeatsRemaining() + "</font> seat" + (offer.getSeatsRemaining() > 1 ? "s" : "") + " left"));

        try {
            JSONObject props = new JSONObject();
            props.put("offer_id", offer.getOfferId());
            props.put("destination", offer.getEndTerawhereLocation().getName());
            ((TerawhereApplication) popup.getContext().getApplicationContext()).getMixpanel().track("Pin Clicked", props);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return popup;
    }
}
