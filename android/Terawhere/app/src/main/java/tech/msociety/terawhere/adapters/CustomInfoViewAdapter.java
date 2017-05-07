package tech.msociety.terawhere.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseUser;

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
    /*final View popup = mInflater.inflate(R.layout.info_window_layout, null);

    ((TextView) popup.findViewById(R.id.title)).setText(marker.getSnippet());

    return popup;*/
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

        if (ParseUser.getCurrentUser().getString("username").equals(objOffer.getDriverId())) {
            nameTextView.setText("Me");
        } else {
            nameTextView.setText(objOffer.getDriverId());
        }

        destinationTextView.setText(objOffer.getDestination());
        seatsAvailableTextView.setText(Integer.toString(objOffer.getNumberOfSeats()) + " LEFT");
        SimpleDateFormat ft = new SimpleDateFormat("hh:mm a");

        if (objOffer.getTimestamp() != null) {
            pickUpTimeTextView.setText(ft.format(objOffer.getTimestamp()));
        }

        /*
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Offers");
        ParseGeoPoint pgp = new ParseGeoPoint(marker.getPosition().latitude, marker.getPosition().longitude);
        query.whereEqualTo("CurrentLocation", pgp);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null) {

                    if (objects.size() > 0) {
                        Log.i("Size", Integer.toString(objects.size()));
                        Log.i("Size", objects.get(0).getString("Name"));
                        Log.i("Size", objects.get(0).getString("Destination"));


                        //name = objects.get(0).getString("Name");


                                ((TextView) popup.findViewById(R.id.nameTextView)).setText(objects.get(0).getString("Name"));


                        destinationTextView.setText(objects.get(0).getString("Destination"));
                            seatsAvailableTextView.setText(Integer.toString(objects.get(0).getInt("SeatsAvailable")));
                            SimpleDateFormat ft = new SimpleDateFormat ("hh:mm a");

                            if(objects.get(0).getDate("PickUpTime") != null) {
                                pickUpTimeTextView.setText(ft.format(objects.get(0).getDate("PickUpTime")));
                            }




                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        */
        return popup;
    }
}
