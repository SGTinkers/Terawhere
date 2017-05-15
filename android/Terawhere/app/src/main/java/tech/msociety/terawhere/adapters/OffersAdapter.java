package tech.msociety.terawhere.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.networkcalls.jsonschema2pojo.getoffers.OffersDatum;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.CreateOfferActivity;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {
    private List<Offer> offers;
    private ViewGroup viewGroup;
    private String[] value = new String[]{"Edit", "Delete"};
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_offer, parent, false);
        viewGroup = parent;
    
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Offer offer = offers.get(position);
        viewHolder.textViewOfferPickupLocation.setText(offer.getStartingLocationAddress());
        viewHolder.textViewOfferDestination.setText(offer.getEndingLocationAddress());
        viewHolder.textViewOfferTime.setText(offer.getMeetUpTime());
        viewHolder.textViewOfferDate.setText(offer.getMeetUpTime());
        
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(viewGroup.getContext());
                alertdialogbuilder.setItems(value, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedText = Arrays.asList(value).get(which);
    
                        if (selectedText.equals("Delete")) {
                            Log.i("DELETING_INDEX", ":" + offers.get(position).getId());
        
                            Call<OffersDatum> deleteRequest = TerawhereBackendServer.getApiInstance().deleteOffer(offers.get(position).getId());
                            deleteRequest.enqueue(new Callback<OffersDatum>() {
                                @Override
                                public void onResponse(Call<OffersDatum> call, Response<OffersDatum> response) {
                                    if (response.isSuccessful()) {
                                        Log.i("DELETING: ", Integer.toString(position));
    
                                        Log.i("DELETED", ": " + response.message());
                                        offers.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, getItemCount());
    
                                    } else {
                                        Log.i("ERROR_DELETE", ": " + response.message());
    
                                        try {
                                            Log.i("ERROR_DELETE_MESSAGE", ": " + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
    
                                    }
                                    // use response.code, response.headers, etc.
                                }
    
                                @Override
                                public void onFailure(Call<OffersDatum> call, Throwable t) {
                                    // handle failure
                                }
                            });
                        }
                        if (selectedText.equals("Edit")) {
                            Intent intent = new Intent(new Intent(viewGroup.getContext(), CreateOfferActivity.class));
                            intent.putExtra("isEdit", true);
                            intent.putExtra("id", offer.getId());
                            intent.putExtra("driverId", offer.getDriverId());
                            Log.i("DRIVER", ":" + offer.getDriverId());
                            intent.putExtra("meetUpTime", offer.getMeetUpTime());
    
                            intent.putExtra("startingLocationName", offer.getStartingLocationName());
                            intent.putExtra("startingLocationAddress", offer.getStartingLocationAddress());
                            intent.putExtra("startingLocationLatitude", offer.getStartingLocationLatitude());
                            intent.putExtra("startingLocationLongitude", offer.getStartingLocationLongitude());
    
                            intent.putExtra("endingLocationName", offer.getEndingLocationName());
                            intent.putExtra("endingLocationAddress", offer.getEndingLocationAddress());
                            intent.putExtra("endingLocationLatitude", offer.getEndingLocationLatitude());
                            intent.putExtra("endingLocationLongitude", offer.getEndingLocationLongitude());
    
                            Log.i("LAT2", ":" + offer.getEndingLocationLatitude());
                            Log.i("LON2", ":" + offer.getEndingLocationLongitude());
    
                            intent.putExtra("driverRemarks", offer.getDriverRemarks());
                            intent.putExtra("seatsAvailable", offer.getSeatsAvailable());
    
                            intent.putExtra("endingLocationAddress", offer.getEndingLocationAddress());
                            intent.putExtra("endingLocationAddress", offer.getEndingLocationAddress());
    
                            intent.putExtra("vehicleModel", offer.getVehicleModel());
                            intent.putExtra("vehicleDescription", offer.getVehicleDescription());
                            intent.putExtra("vehicleNumber", offer.getVehicleNumber());
    
                            intent.putExtra("genderPreference", offer.getGenderPreference());
                            //((Activity) viewGroup.getContext()).finish();
                            viewGroup.getContext().startActivity(intent);
                        }
                    }
    
                });
    
                AlertDialog dialog = alertdialogbuilder.create();
    
                dialog.show();
                return true;
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return offers == null ? 0 : offers.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewOfferTime;
        private TextView textViewOfferDate;
        private TextView textViewOfferPickupLocation;
        private TextView textViewOfferDestination;
        
        private ViewHolder(View view) {
            super(view);
            textViewOfferTime = (TextView) view.findViewById(R.id.text_view_offer_time);
            textViewOfferDate = (TextView) view.findViewById(R.id.text_view_offer_date);
            textViewOfferPickupLocation = (TextView) view.findViewById(R.id.text_view_offer_pickup_location);
            textViewOfferDestination = (TextView) view.findViewById(R.id.text_view_offer_destination);
        }
    }
    
    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}
