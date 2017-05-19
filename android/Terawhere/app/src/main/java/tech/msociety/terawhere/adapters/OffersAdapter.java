package tech.msociety.terawhere.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        
        viewHolder.endingLocationTextView.setText("Destination: " + offer.getEndingLocationAddress());
        viewHolder.startingLocationTextView.setText("Meeting Point: " + offer.getStartingLocationAddress());
        
        String meetUpTime = "";
        String day = "";
        String month = "";
        try {
            meetUpTime = new SimpleDateFormat("hh:mm a").format(new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").parse(offer.getMeetUpTime().toString()));
            day = new SimpleDateFormat("dd").format(new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").parse(offer.getMeetUpTime().toString()));
            month = new SimpleDateFormat("MMMM").format(new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").parse(offer.getMeetUpTime().toString()));
    
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (!meetUpTime.matches("")) {
            viewHolder.meetUpTimeTextView.setText("Pick Up Time: " + meetUpTime);
        }
        viewHolder.dayTextView.setText(day);
        viewHolder.monthTextView.setText(month);
        
        if (offer.getDriverRemarks().matches("")) {
            viewHolder.remarksTextView.setText("Remarks: NIL");
        } else {
            viewHolder.remarksTextView.setText("Remarks: " + offer.getDriverRemarks());
        }
        
        final boolean[] shouldExpand = {viewHolder.remarksTextView.getVisibility() == View.GONE};
        ChangeBounds transition = new ChangeBounds();
        transition.setDuration(125);
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldExpand[0]) {
                    viewHolder.remarksTextView.setVisibility(View.VISIBLE);
                    viewHolder.detailsTextView.setText("\u2014 LESS DETAILS");
    
                    shouldExpand[0] = false;
                } else {
                    viewHolder.remarksTextView.setVisibility(View.GONE);
                    viewHolder.detailsTextView.setText("+ MORE DETAILS");
    
                    shouldExpand[0] = true;
                }
    
                TransitionManager.beginDelayedTransition(viewGroup);
                viewHolder.itemView.setActivated(shouldExpand[0]);
    
            }
        });
        
        viewHolder.detailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldExpand[0]) {
                    viewHolder.remarksTextView.setVisibility(View.VISIBLE);
                    viewHolder.detailsTextView.setText("\u2014 LESS DETAILS");
    
                    shouldExpand[0] = false;
                } else {
                    viewHolder.remarksTextView.setVisibility(View.GONE);
                    viewHolder.detailsTextView.setText("+ MORE DETAILS");
    
                    shouldExpand[0] = true;
                }
    
                TransitionManager.beginDelayedTransition(viewGroup);
                viewHolder.itemView.setActivated(shouldExpand[0]);
    
            }
        });
        
        viewHolder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CLICK", "EDIT");
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
        });
        
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("CLICK", "DELETE");
                final AlertDialog.Builder adb = new AlertDialog.Builder(viewGroup.getContext());
    
                adb.setTitle("Delete Offer?");
                adb.setMessage("Are you sure you want to delete your offer?");
                adb.setNegativeButton("Cancel", null); // dismisses by default
                adb.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do the acknowledged action, beware, this is run on UI thread
                        Log.i("CLICK", "OK");
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
                });
    
                AlertDialog alert = adb.create();
                alert.show();
    
                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(Color.BLACK);
                nbutton.setText("Cancel");
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(Color.parseColor("#54d8bd"));
                pbutton.setText("Confirm");
            }
        });
        //viewHolder.seatsLeftTextView.setText(offer.getSeatsAvailable());



        /*viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
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
        });*/
    }
    
    @Override
    public int getItemCount() {
        return offers == null ? 0 : offers.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dayTextView;
        private TextView monthTextView;
        private TextView endingLocationTextView;
        private TextView startingLocationTextView;
        private TextView meetUpTimeTextView;
        private TextView remarksTextView;
        
        private LinearLayout linearLayout;
        private TextView seatsOfferedTextView;
        private TextView seatsLeftTextView;
        private ImageButton editButton;
        private ImageButton deleteButton;
        private TextView detailsTextView;
        
        private ViewHolder(View view) {
            super(view);
            endingLocationTextView = (TextView) view.findViewById(R.id.offersItemTextViewEndingLocation);
            startingLocationTextView = (TextView) view.findViewById(R.id.offersItemTextViewStartingLocation);
            meetUpTimeTextView = (TextView) view.findViewById(R.id.offersItemTextViewMeetUpTime);
            seatsOfferedTextView = (TextView) view.findViewById(R.id.offersItemTextViewSeatsOffered);
            seatsLeftTextView = (TextView) view.findViewById(R.id.offersItemTextViewSeatsLeft);
            dayTextView = (TextView) view.findViewById(R.id.offersItemTextViewDay);
            remarksTextView = (TextView) view.findViewById(R.id.offersItemTextViewRemarks);
            monthTextView = (TextView) view.findViewById(R.id.offersItemTextViewMonth);
            editButton = (ImageButton) view.findViewById(R.id.offersItemButtonEdit);
            deleteButton = (ImageButton) view.findViewById(R.id.offersItemButtonDelete);
            detailsTextView = (TextView) view.findViewById(R.id.offersItemTextViewDetails);
            linearLayout = (LinearLayout) view.findViewById(R.id.offersItemLinearLayout);
            
        }
    }
    
    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}
