package tech.msociety.terawhere.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.events.OfferDeletedEvent;
import tech.msociety.terawhere.globals.TerawhereApplication;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.screens.activities.BookingInfoActivity;
import tech.msociety.terawhere.screens.activities.CreateOfferActivity;
import tech.msociety.terawhere.utils.DateUtils;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {
    private static final String LESS_DETAILS = "\u2014 LESS DETAILS";
    private static final String MORE_DETAILS = "+ MORE DETAILS";
    private static final String DELETE_OFFER_TITLE = "Confirm Cancel Offer?";
    private static final String DELETE_OFFER_INFO = "The passengers will be informed of your cancellation.";
    private static final String CANCEL = "Keep";
    private static final String DELETE = "Confirm";
    private static final String LOG_ERROR_DELETE_MESSAGE = "ERROR_DELETE_MESSAGE";
    private static final String CONFIRM = "Confirm";
    private static final String TERAWHERE_PRIMARY_COLOR = "#54d8bd";

    private Context context;

    private List<Offer> offers;

    private ViewGroup viewGroup;

    public OffersAdapter(Context context) {
        this.context = context;
    }

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

        String day = DateUtils.dateToString(offer.getMeetupTime(), DateUtils.DAY_OF_MONTH_FORMAT);
        String month = DateUtils.dateToString(offer.getMeetupTime(), DateUtils.MONTH_ABBREVIATED_FORMAT);

        viewHolder.textViewMonth.setText(month);
        viewHolder.textViewDay.setText(day);
        viewHolder.textViewMeetupTime.setText(meetUpTime);
        viewHolder.textViewEndLocationName.setText(offer.getEndTerawhereLocation().getName());
        viewHolder.textViewEndLocationAddress.setText(offer.getEndTerawhereLocation().getAddress());
        viewHolder.textViewStartLocationName.setText(offer.getStartTerawhereLocation().getName());
        viewHolder.textViewStartLocationAddress.setText(offer.getStartTerawhereLocation().getAddress());
        viewHolder.textViewSeatsLeft.setText(offer.getSeatsRemaining() + " of " + offer.getVacancy());
        if (offer.getRemarks() != null && !offer.getRemarks().isEmpty()) {
            viewHolder.textViewRemarks.setText(offer.getRemarks());
            viewHolder.textViewRemarksLabel.setVisibility(View.VISIBLE);
            viewHolder.textViewRemarks.setVisibility(View.VISIBLE);
        } else {
            viewHolder.textViewRemarksLabel.setVisibility(View.GONE);
            viewHolder.textViewRemarks.setVisibility(View.GONE);
        }
        viewHolder.textViewVehicle.setText(offer.getVehicle().getDescription() + " " + offer.getVehicle().getModel() + " [" + offer.getVehicle().getPlateNumber() + "]");

        // Hide action buttons if offer has passed
        if (offer.isPast()) {
            viewHolder.textViewEdit.setVisibility(View.GONE);
            viewHolder.textViewCancel.setVisibility(View.GONE);
            viewHolder.textViewOfferPast.setVisibility(View.VISIBLE);
        } else {
            viewHolder.textViewEdit.setVisibility(View.VISIBLE);
            viewHolder.textViewCancel.setVisibility(View.VISIBLE);
            viewHolder.textViewOfferPast.setVisibility(View.INVISIBLE);
        }

        final boolean[] shouldExpand = isCollapse(viewHolder, offer);

        setOfferItemRelativeLayoutListener(viewHolder, shouldExpand);
        setDetailsTextViewListener(viewHolder, shouldExpand);

        viewHolder.textViewEndLocationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:" + offer.getEndTerawhereLocation().getLatitude() + "," + offer.getEndTerawhereLocation().getLongitude() + "?q=" + offer.getEndTerawhereLocation().getName());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
            }
        });
        viewHolder.textViewStartLocationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:" + offer.getStartTerawhereLocation().getLatitude() + "," + offer.getStartTerawhereLocation().getLongitude() + "?q=" + offer.getStartTerawhereLocation().getName());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
            }
        });

        // set listeners for edit/delete offer
        setEditOfferButtonListener(viewHolder, offer);
        setDeleteOfferButtonListener(viewHolder, position);
    }

    private boolean[] isCollapse(ViewHolder viewHolder, Offer offer) {
        return new boolean[]{
                viewHolder.textViewEndLocationAddress.getVisibility() == View.GONE,
                viewHolder.textViewStartLocationAddress.getVisibility() == View.GONE,
                offer.getRemarks() != null && !offer.getRemarks().isEmpty() && viewHolder.textViewRemarks.getVisibility() == View.GONE,
                viewHolder.textViewVehicleLabel.getVisibility() == View.GONE,
                viewHolder.textViewVehicle.getVisibility() == View.GONE,
        };
    }

    private void setOfferItemRelativeLayoutListener(final ViewHolder viewHolder, final boolean[] shouldExpand) {
        viewHolder.relativeLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Offer offer = offers.get(viewHolder.getAdapterPosition());
                try {
                    JSONObject props = new JSONObject();
                    props.put("offer_id", offer.getOfferId());
                    props.put("destination", offer.getEndTerawhereLocation().getName());
                    ((TerawhereApplication) context.getApplicationContext()).getMixpanel().track("Launched Offer Passengers", props);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Context context = viewGroup.getContext();
                Intent intent = new Intent(context, BookingInfoActivity.class);
                intent.putExtra(BookingInfoActivity.INTENT_OFFER_ID, offer.getOfferId());
                context.startActivity(intent);
            }
        });
    }

    private void setDetailsTextViewListener(final ViewHolder viewHolder, final boolean[] shouldExpand) {
        viewHolder.textViewViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpand(viewHolder, shouldExpand);
            }
        });
    }

    private void toggleExpand(final ViewHolder viewHolder, final boolean[] shouldExpand) {
        if (shouldExpand[0]) {
            viewHolder.textViewEndLocationAddress.setVisibility(View.VISIBLE);
            viewHolder.textViewStartLocationAddress.setVisibility(View.VISIBLE);
            viewHolder.textViewVehicleLabel.setVisibility(View.VISIBLE);
            viewHolder.textViewVehicle.setVisibility(View.VISIBLE);
            viewHolder.textViewViewMore.setText(LESS_DETAILS);
            shouldExpand[0] = false;
        } else {
            viewHolder.textViewEndLocationAddress.setVisibility(View.GONE);
            viewHolder.textViewStartLocationAddress.setVisibility(View.GONE);
            viewHolder.textViewVehicleLabel.setVisibility(View.GONE);
            viewHolder.textViewVehicle.setVisibility(View.GONE);
            viewHolder.textViewViewMore.setText(MORE_DETAILS);
            shouldExpand[0] = true;
        }

        TransitionManager.beginDelayedTransition(viewGroup);
        viewHolder.itemView.setActivated(shouldExpand[0]);
    }

    private void setEditOfferButtonListener(ViewHolder viewHolder, final Offer offer) {
        viewHolder.textViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject props = new JSONObject();
                    props.put("offer_id", offer.getOfferId());
                    props.put("destination", offer.getEndTerawhereLocation().getName());
                    ((TerawhereApplication) context.getApplicationContext()).getMixpanel().track("Launched Edit Offer", props);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Context context = viewGroup.getContext();
                Intent intent = CreateOfferActivity.getIntentToStartInEditMode(context, offer);
                context.startActivity(intent);
            }
        });
    }

    private void setDeleteOfferButtonListener(ViewHolder viewHolder, final int position) {
        viewHolder.textViewCancel.setOnClickListener(new View.OnClickListener() {
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
                final Offer offer = offers.get(position);
                Call<Void> deleteRequest = TerawhereBackendServer.getApiInstance().deleteOffer(offer.getOfferId());
                deleteRequest.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            EventBus.getDefault().post(new OfferDeletedEvent(offer));
                            try {
                                JSONObject props = new JSONObject();
                                props.put("offer_id", offer.getOfferId());
                                props.put("destination", offer.getEndTerawhereLocation().getName());
                                ((TerawhereApplication) context.getApplicationContext()).getMixpanel().track("Offer Cancelled", props);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                Log.i(LOG_ERROR_DELETE_MESSAGE, ": " + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                JSONObject props = new JSONObject();
                                props.put("offer_id", offer.getOfferId());
                                props.put("destination", offer.getEndTerawhereLocation().getName());
                                ((TerawhereApplication) context.getApplicationContext()).getMixpanel().track("Offer Cancellation Failed", props);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        try {
                            JSONObject props = new JSONObject();
                            props.put("offer_id", offer.getOfferId());
                            props.put("destination", offer.getEndTerawhereLocation().getName());
                            ((TerawhereApplication) context.getApplicationContext()).getMixpanel().track("Offer Cancellation Failed", props);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void setAdbDeleteOfferCancelButton(AlertDialog.Builder adbDeleteOffer) {
        adbDeleteOffer.setNegativeButton(CANCEL, null);
    }

    private void setAdbDeleteOfferMessage(AlertDialog.Builder adbDeleteOffer) {
        adbDeleteOffer.setMessage(DELETE_OFFER_INFO);
    }

    private void setAdbDeleteOfferTitle(AlertDialog.Builder adbDeleteOffer) {
        adbDeleteOffer.setTitle(DELETE_OFFER_TITLE);
    }

    private void setAdbDeleteOfferStyle(AlertDialog.Builder adbDeleteOffer) {
        AlertDialog deleteOfferAlertDialog = adbDeleteOffer.create();
        deleteOfferAlertDialog.show();
        setDeleteOfferDialogStyle(deleteOfferAlertDialog);
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

    public Offer getLastOffer() {
        if (offers == null || offers.isEmpty()) return null;
        return offers.get(offers.size() - 1);
    }

    @Override
    public int getItemCount() {
        return offers == null ? 0 : offers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewDay;
        private TextView textViewMonth;
        private TextView textViewEndLocationName;
        private TextView textViewEndLocationAddress;
        private TextView textViewStartLocationName;
        private TextView textViewStartLocationAddress;
        private TextView textViewMeetupTime;
        private TextView textViewRemarksLabel;
        private TextView textViewRemarks;
        private TextView textViewSeatsLeft;

        private TextView textViewVehicleLabel;
        private TextView textViewVehicle;

        private TextView textViewViewMore;
        private TextView textViewEdit;
        private TextView textViewCancel;

        private TextView textViewOfferPast;

        private RelativeLayout relativeLayoutItem;

        private ViewHolder(View view) {
            super(view);

            relativeLayoutItem = (RelativeLayout) view.findViewById(R.id.relative_layout_item);
            textViewDay = (TextView) view.findViewById(R.id.text_view_day);
            textViewMonth = (TextView) view.findViewById(R.id.text_view_month);
            textViewEndLocationName = (TextView) view.findViewById(R.id.text_view_end_location_name);
            textViewEndLocationAddress = (TextView) view.findViewById(R.id.text_view_end_location_address);
            textViewStartLocationName = (TextView) view.findViewById(R.id.text_view_start_location_name);
            textViewStartLocationAddress = (TextView) view.findViewById(R.id.text_view_start_location_address);
            textViewMeetupTime = (TextView) view.findViewById(R.id.text_view_meetup_time);
            textViewSeatsLeft = (TextView) view.findViewById(R.id.text_view_seats_left);
            textViewRemarksLabel = (TextView) view.findViewById(R.id.text_view_remarks_label);
            textViewRemarks = (TextView) view.findViewById(R.id.text_view_remarks);
            textViewViewMore = (TextView) view.findViewById(R.id.text_view_view_more);
            textViewCancel = (TextView) view.findViewById(R.id.text_view_cancel);
            textViewEdit = (TextView) view.findViewById(R.id.text_view_edit);
            textViewVehicleLabel = (TextView) view.findViewById(R.id.text_view_vehicle_label);
            textViewVehicle = (TextView) view.findViewById(R.id.text_view_vehicle);
            textViewOfferPast = (TextView) view.findViewById(R.id.text_view_offer_past);
        }
    }
}
