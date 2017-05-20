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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.CreateOfferActivity;
import tech.msociety.terawhere.utils.DateUtils;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {
    private List<Offer> offers;
    private ViewGroup viewGroup;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_offer, parent, false);
        viewGroup = parent;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Offer offer = offers.get(position);

        String meetUpTime = DateUtils.toFriendlyTimeString(offer.getMeetupTime());
        String day = DateUtils.toString(offer.getMeetupTime(), DateUtils.DAY_OF_MONTH_FORMAT);
        String month = DateUtils.toString(offer.getMeetupTime(), DateUtils.MONTH_ABBREVIATED_FORMAT);

        viewHolder.endingLocationTextView.setText("Destination: " + offer.getEndTerawhereLocation().getAddress());
        viewHolder.startingLocationTextView.setText("Meeting Point: " + offer.getStartTerawhereLocation().getAddress());
        viewHolder.meetUpTimeTextView.setText("Pick Up Time: " + meetUpTime);
        viewHolder.dayTextView.setText(day);
        viewHolder.monthTextView.setText(month);
        viewHolder.remarksTextView.setText("Remarks: " + offer.getRemarks());

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


                TerawhereLocation startTerawhereLocation = new TerawhereLocation(offer.getStartTerawhereLocation().getName(),
                        offer.getStartTerawhereLocation().getAddress(),
                        offer.getStartTerawhereLocation().getLatitude(),
                        offer.getStartTerawhereLocation().getLongitude(),
                        offer.getStartTerawhereLocation().getGeohash());

                TerawhereLocation endTerawhereLocation = new TerawhereLocation(offer.getEndTerawhereLocation().getName(),
                        offer.getEndTerawhereLocation().getAddress(),
                        offer.getEndTerawhereLocation().getLatitude(),
                        offer.getEndTerawhereLocation().getLongitude(),
                        offer.getEndTerawhereLocation().getGeohash());

                Vehicle vehicle = new Vehicle(offer.getVehicle().getPlateNumber(),
                        offer.getVehicle().getDescription(),
                        offer.getVehicle().getModel());


                Intent intent = new Intent(new Intent(viewGroup.getContext(), CreateOfferActivity.class));
                intent.putExtra("startTerawhereLocation", startTerawhereLocation);
                intent.putExtra("endTerawhereLocation", endTerawhereLocation);
                intent.putExtra("vehicle", vehicle);


                intent.putExtra("isEdit", true);
                intent.putExtra("id", offer.getOfferId());
                intent.putExtra("driverId", offer.getOffererId());
                intent.putExtra("meetUpTime", offer.getMeetupTime());


                intent.putExtra("driverRemarks", offer.getRemarks());
                intent.putExtra("seatsAvailable", offer.getVacancy());


                //((Activity) viewGroup.getContext()).finish();
                viewGroup.getContext().startActivity(intent);
            }
        });

        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(viewGroup.getContext());

                adb.setTitle("Delete Offer?");
                adb.setMessage("Are you sure you want to delete your offer?");
                adb.setNegativeButton("Cancel", null); // dismisses by default
                adb.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Void> deleteRequest = TerawhereBackendServer.getApiInstance().deleteOffer(offers.get(position).getOfferId());
                        deleteRequest.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    offers.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, getItemCount());

                                } else {

                                    try {
                                        Log.i("ERROR_DELETE_MESSAGE", ": " + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
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
