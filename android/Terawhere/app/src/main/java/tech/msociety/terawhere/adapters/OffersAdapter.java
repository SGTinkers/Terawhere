package tech.msociety.terawhere.adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
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

        /***** set offer object *****/
        final Offer offer = offers.get(position);

        /***** set meet up time to be in hh:mm am/pm format *****/
        String meetUpTime = DateUtils.toFriendlyTimeString(offer.getMeetupTime());

        /***** set meet up date to be in day and month abbreviated format *****/
        String day = DateUtils.toString(offer.getMeetupTime(), DateUtils.DAY_OF_MONTH_FORMAT);
        String month = DateUtils.toString(offer.getMeetupTime(), DateUtils.MONTH_ABBREVIATED_FORMAT);

        /***** offer fields setText *****/
        setTextEndLocation(viewHolder, offer);
        setTextStartLocation(viewHolder, offer);
        setTextMeetUpTime(viewHolder, meetUpTime);
        setTextDay(viewHolder, day);
        setTextMonth(viewHolder, month);
        setTextRemarks(viewHolder, offer);

        /***** check card collapse/expand *****/
        final boolean[] shouldExpand = isCollapse(viewHolder);

        /***** set listeners for collapse/expand offer details *****/
        setOfferItemRelativeLayoutListener(viewHolder, shouldExpand);
        setDetailsTextViewListener(viewHolder, shouldExpand);

        /***** set listeners for edit/delete offer *****/
        setEditOfferButtonListener(viewHolder, offer);
        setDeleteOfferButtonListener(viewHolder, position);

    }

    private void setTextRemarks(ViewHolder viewHolder, Offer offer) {
        viewHolder.remarksTextView.setText(getOfferRemarksText(offer));
    }

    private void setTextMonth(ViewHolder viewHolder, String month) {
        viewHolder.monthTextView.setText(month);
    }

    private void setTextDay(ViewHolder viewHolder, String day) {
        viewHolder.dayTextView.setText(day);
    }

    private void setTextMeetUpTime(ViewHolder viewHolder, String meetUpTime) {
        viewHolder.meetUpTimeTextView.setText(getOfferMeetUpTimeText(meetUpTime));
    }

    private void setTextStartLocation(ViewHolder viewHolder, Offer offer) {
        viewHolder.startLocationTextView.setText(getOfferStartLocationText(offer));
    }

    private void setTextEndLocation(ViewHolder viewHolder, Offer offer) {
        viewHolder.endLocationTextView.setText(getOfferEndLocationText(offer));
    }

    private boolean[] isCollapse(ViewHolder viewHolder) {
        return new boolean[]{viewHolder.remarksTextView.getVisibility() == View.GONE};
    }

    private void setOfferItemRelativeLayoutListener(final ViewHolder viewHolder, final boolean[] shouldExpand) {
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
    }

    private void setDetailsTextViewListener(final ViewHolder viewHolder, final boolean[] shouldExpand) {
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
    }

    private void setEditOfferButtonListener(ViewHolder viewHolder, final Offer offer) {
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
    }

    private void setDeleteOfferButtonListener(ViewHolder viewHolder, final int position) {
        viewHolder.deleteOfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder adbDeleteOffer = new AlertDialog.Builder(viewGroup.getContext());
                createAdbDeleteOffer(adbDeleteOffer, position);

            }
        });
    }

    private void createAdbDeleteOffer(AlertDialog.Builder adbDeleteOffer, final int position) {
        setAdbDeleteOfferTitle(adbDeleteOffer);
        setAdbDeleteOfferMessage(adbDeleteOffer);
        setAdbDeleteOfferCancelButton(adbDeleteOffer);
        setAdbDeleteOfferConfirmButton(adbDeleteOffer, position);
        setAdbDeleteOfferStyle(adbDeleteOffer);
    }

    private void setAdbDeleteOfferConfirmButton(AlertDialog.Builder adbDeleteOffer, final int position) {
        adbDeleteOffer.setPositiveButton(DELETE, new DialogInterface.OnClickListener() {
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
    }

    private void setAdbDeleteOfferCancelButton(AlertDialog.Builder adbDeleteOffer) {
        adbDeleteOffer.setNegativeButton(CANCEL, null);
    }

    private void setAdbDeleteOfferMessage(AlertDialog.Builder adbDeleteOffer) {
        adbDeleteOffer.setMessage(ARE_YOU_SURE_YOU_WANT_TO_DELETE_YOUR_OFFER);
    }

    private void setAdbDeleteOfferTitle(AlertDialog.Builder adbDeleteOffer) {
        adbDeleteOffer.setTitle(DELETE_OFFER);
    }

    private void setAdbDeleteOfferStyle(AlertDialog.Builder adbDeleteOffer) {
        AlertDialog deleteOfferAlertDialog = adbDeleteOffer.create();
        deleteOfferAlertDialog.show();
        setDeleteOfferDialogStyle(deleteOfferAlertDialog);
    }

    @NonNull
    private String getOfferRemarksText(Offer offer) {
        return REMARKS + offer.getRemarks();
    }

    @NonNull
    private String getOfferMeetUpTimeText(String meetUpTime) {
        return PICK_UP_TIME + meetUpTime;
    }

    @NonNull
    private String getOfferStartLocationText(Offer offer) {
        return MEETING_POINT + offer.getStartTerawhereLocation().getAddress();
    }

    @NonNull
    private String getOfferEndLocationText(Offer offer) {
        return DESTINATION + offer.getEndTerawhereLocation().getAddress();
    }

    private void deleteOffer(int position) {
        offers.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }


    private void setDeleteOfferDialogStyle(AlertDialog alert) {
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);
        nbutton.setText(CANCEL);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor(TERAWHERE_PRIMARY_COLOR));
        pbutton.setText(CONFIRM);
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
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

            /***** initialization *****/
            initializeEndLocationTextView(view);
            initializeStartLocationTextView(view);
            initializeMeetUpTimeTextView(view);
            initializeSeatsOfferedTextView(view);
            initializeSeatsLeftTextView(view);
            initializeDayTextView(view);
            initializeRemarksTextView(view);
            initializeMonthTextView(view);
            initializeEditOfferButton(view);
            initializeDeleteOfferButton(view);
            initializeDetailsTextView(view);
            initializeOfferItemRelativeLayout(view);

        }

        private void initializeOfferItemRelativeLayout(View view) {
            offerItemRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_offer_item);
        }

        private void initializeDetailsTextView(View view) {
            detailsTextView = (TextView) view.findViewById(R.id.text_view_offer_view_more);
        }

        private void initializeDeleteOfferButton(View view) {
            deleteOfferButton = (ImageButton) view.findViewById(R.id.image_button_offer_delete);
        }

        private void initializeEditOfferButton(View view) {
            editOfferButton = (ImageButton) view.findViewById(R.id.image_button_offer_edit);
        }

        private void initializeMonthTextView(View view) {
            monthTextView = (TextView) view.findViewById(R.id.text_view_offer_month);
        }

        private void initializeRemarksTextView(View view) {
            remarksTextView = (TextView) view.findViewById(R.id.text_view_offer_remarks);
        }

        private void initializeDayTextView(View view) {
            dayTextView = (TextView) view.findViewById(R.id.text_view_offer_day);
        }

        private void initializeSeatsLeftTextView(View view) {
            seatsLeftTextView = (TextView) view.findViewById(R.id.text_view_offer_seats_left);
        }

        private void initializeSeatsOfferedTextView(View view) {
            seatsOfferedTextView = (TextView) view.findViewById(R.id.text_view_offer_seats_offered);
        }

        private void initializeMeetUpTimeTextView(View view) {
            meetUpTimeTextView = (TextView) view.findViewById(R.id.text_view_offer_meet_up_time);
        }

        private void initializeStartLocationTextView(View view) {
            startLocationTextView = (TextView) view.findViewById(R.id.text_view_offer_start_location);
        }

        private void initializeEndLocationTextView(View view) {
            endLocationTextView = (TextView) view.findViewById(R.id.text_view_offer_end_location);
        }
    }

}
