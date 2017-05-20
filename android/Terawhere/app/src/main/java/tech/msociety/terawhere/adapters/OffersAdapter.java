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
import android.widget.RelativeLayout;
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
    public static final String DESTINATION = "Destination: ";
    public static final String MEETING_POINT = "Meeting Point: ";
    public static final String PICK_UP_TIME = "Pick Up Time: ";
    public static final String REMARKS = "Remarks: ";
    public static final String LESS_DETAILS = "\u2014 LESS DETAILS";
    public static final String MORE_DETAILS = "+ MORE DETAILS";
    public static final String IS_EDIT = "isEdit";
    public static final String START_TERAWHERE_LOCATION = "startTerawhereLocation";
    public static final String END_TERAWHERE_LOCATION = "endTerawhereLocation";
    public static final String VEHICLE = "vehicle";
    public static final String ID = "id";
    public static final String DRIVER_ID = "driverId";
    public static final String MEET_UP_TIME = "meetUpTime";
    public static final String DRIVER_REMARKS = "driverRemarks";
    public static final String SEATS_AVAILABLE = "seatsAvailable";
    public static final String DELETE_OFFER = "Delete Offer?";
    public static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_YOUR_OFFER = "Are you sure you want to delete your offer?";
    public static final String CANCEL = "Cancel";
    public static final String DELETE = "Delete";
    public static final String LOG_ERROR_DELETE_MESSAGE = "ERROR_DELETE_MESSAGE";
    public static final String CONFIRM = "Confirm";
    public static final String TERAWHERE_PRIMARY_COLOR = "#54d8bd";
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

        viewHolder.endLocationTextView.setText(DESTINATION + offer.getEndTerawhereLocation().getAddress());
        viewHolder.startLocationTextView.setText(MEETING_POINT + offer.getStartTerawhereLocation().getAddress());
        viewHolder.meetUpTimeTextView.setText(PICK_UP_TIME + meetUpTime);
        viewHolder.dayTextView.setText(day);
        viewHolder.monthTextView.setText(month);
        viewHolder.remarksTextView.setText(REMARKS + offer.getRemarks());

        final boolean[] shouldExpand = {viewHolder.remarksTextView.getVisibility() == View.GONE};
        ChangeBounds transition = new ChangeBounds();
        transition.setDuration(125);

        viewHolder.offerItemRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldExpand[0]) {
                    viewHolder.remarksTextView.setVisibility(View.VISIBLE);
                    viewHolder.detailsTextView.setText(LESS_DETAILS);
                    shouldExpand[0] = false;
                } else {
                    viewHolder.remarksTextView.setVisibility(View.GONE);
                    viewHolder.detailsTextView.setText(MORE_DETAILS);
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
                    viewHolder.detailsTextView.setText(LESS_DETAILS);

                    shouldExpand[0] = false;
                } else {
                    viewHolder.remarksTextView.setVisibility(View.GONE);
                    viewHolder.detailsTextView.setText(MORE_DETAILS);

                    shouldExpand[0] = true;
                }

                TransitionManager.beginDelayedTransition(viewGroup);
                viewHolder.itemView.setActivated(shouldExpand[0]);

            }
        });

        viewHolder.editOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*********** start location ************/
                TerawhereLocation startTerawhereLocation = new TerawhereLocation(
                        offer.getStartTerawhereLocation().getName(),
                        offer.getStartTerawhereLocation().getAddress(),
                        offer.getStartTerawhereLocation().getLatitude(),
                        offer.getStartTerawhereLocation().getLongitude(),
                        offer.getStartTerawhereLocation().getGeohash());

                /*********** end location ************/
                TerawhereLocation endTerawhereLocation = new TerawhereLocation(
                        offer.getEndTerawhereLocation().getName(),
                        offer.getEndTerawhereLocation().getAddress(),
                        offer.getEndTerawhereLocation().getLatitude(),
                        offer.getEndTerawhereLocation().getLongitude(),
                        offer.getEndTerawhereLocation().getGeohash());

                /*********** vehicle info ************/
                Vehicle vehicle = new Vehicle(
                        offer.getVehicle().getPlateNumber(),
                        offer.getVehicle().getDescription(),
                        offer.getVehicle().getModel());

                /******** store values for create offer activity *******/
                Intent intent = new Intent(new Intent(viewGroup.getContext(), CreateOfferActivity.class));
                intent.putExtra(IS_EDIT, true);
                intent.putExtra(START_TERAWHERE_LOCATION, startTerawhereLocation);
                intent.putExtra(END_TERAWHERE_LOCATION, endTerawhereLocation);
                intent.putExtra(VEHICLE, vehicle);
                intent.putExtra(ID, offer.getOfferId());
                intent.putExtra(DRIVER_ID, offer.getOffererId());
                intent.putExtra(MEET_UP_TIME, offer.getMeetupTime());
                intent.putExtra(DRIVER_REMARKS, offer.getRemarks());
                intent.putExtra(SEATS_AVAILABLE, offer.getVacancy());

                /******** start create offer activity *******/
                viewGroup.getContext().startActivity(intent);
            }
        });

        viewHolder.deleteOfferButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder deleteConfirmationDialog = new AlertDialog.Builder(viewGroup.getContext());

                deleteConfirmationDialog.setTitle(DELETE_OFFER);
                deleteConfirmationDialog.setMessage(ARE_YOU_SURE_YOU_WANT_TO_DELETE_YOUR_OFFER);
                deleteConfirmationDialog.setNegativeButton(CANCEL, null); // dismisses by default
                deleteConfirmationDialog.setPositiveButton(DELETE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Void> deleteRequest = TerawhereBackendServer.getApiInstance().deleteOffer(offers.get(position).getOfferId());
                        deleteRequest.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    deleteOffer(position);

                                } else {
                                    try {
                                        Log.i(LOG_ERROR_DELETE_MESSAGE, ": " + response.errorBody().string());
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

                AlertDialog alert = deleteConfirmationDialog.create();
                alert.show();

                decorateAlertDialog(alert);
            }
        });
    }

    private void deleteOffer(int position) {
        offers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    private void decorateAlertDialog(AlertDialog alert) {
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);
        nbutton.setText(CANCEL);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor(TERAWHERE_PRIMARY_COLOR));
        pbutton.setText(CONFIRM);
    }

    @Override
    public int getItemCount() {
        return offers == null ? 0 : offers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView dayTextView;
        private TextView monthTextView;
        private TextView endLocationTextView;
        private TextView startLocationTextView;
        private TextView meetUpTimeTextView;
        private TextView remarksTextView;

        private TextView seatsOfferedTextView;
        private TextView seatsLeftTextView;
        private ImageButton editOfferButton;
        private ImageButton deleteOfferButton;
        private TextView detailsTextView;

        private RelativeLayout offerItemRelativeLayout;

        private ViewHolder(View view) {
            super(view);
            endLocationTextView = (TextView) view.findViewById(R.id.text_view_offer_end_location);
            startLocationTextView = (TextView) view.findViewById(R.id.text_view_offer_start_location);
            meetUpTimeTextView = (TextView) view.findViewById(R.id.text_view_offer_meet_up_time);
            seatsOfferedTextView = (TextView) view.findViewById(R.id.text_view_offer_seats_offered);
            seatsLeftTextView = (TextView) view.findViewById(R.id.text_view_offer_seats_left);
            dayTextView = (TextView) view.findViewById(R.id.text_view_offer_day);
            remarksTextView = (TextView) view.findViewById(R.id.text_view_offer_remarks);
            monthTextView = (TextView) view.findViewById(R.id.text_view_offer_month);
            editOfferButton = (ImageButton) view.findViewById(R.id.image_button_offer_edit);
            deleteOfferButton = (ImageButton) view.findViewById(R.id.image_button_offer_delete);
            detailsTextView = (TextView) view.findViewById(R.id.text_view_offer_view_more);
            offerItemRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_offer_item);
        }
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }
}
