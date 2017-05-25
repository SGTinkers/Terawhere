package tech.msociety.terawhere.adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.transition.TransitionManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.utils.DateUtils;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {
    private static final String LESS_DETAILS = "\u2014 LESS DETAILS";
    private static final String MORE_DETAILS = "+ MORE DETAILS";
    public static final String DELETE_BOOKING = "Delete Booking?";
    private static final String ARE_YOU_SURE_YOU_WANT_TO_DELETE_YOUR_BOOKING = "Are you sure you want to delete your booking?";
    public static final String LOG_ERROR_DELETE_MESSAGE = "ERROR_DELETE_MESSAGE";
    private static final String CANCEL = "Cancel";
    private static final String CONFIRM = "Confirm";
    public static final String TERAWHERE_PRIMARY_COLOR = "#54d8bd";

    private Context context;

    private List<Booking> bookings;

    private ViewGroup viewGroup;

    public BookingsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_booking, parent, false);
        viewGroup = parent;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Booking booking = bookings.get(position);

        String meetUpTime = DateUtils.toFriendlyTimeString(booking.getOffer().getMeetupTime());
        String day = DateUtils.dateToString(booking.getOffer().getMeetupTime(), DateUtils.DAY_OF_MONTH_FORMAT);
        String month = DateUtils.dateToString(booking.getOffer().getMeetupTime(), DateUtils.MONTH_ABBREVIATED_FORMAT);

        // Set the value of the text
        final Offer offer = booking.getOffer();
        viewHolder.textViewMonth.setText(month);
        viewHolder.textViewDay.setText(day);
        viewHolder.textViewMeetupTime.setText(meetUpTime);
        viewHolder.textViewEndLocationName.setText(offer.getEndTerawhereLocation().getName());
        viewHolder.textViewEndLocationAddress.setText(offer.getEndTerawhereLocation().getAddress());
        viewHolder.textViewStartLocationName.setText(offer.getStartTerawhereLocation().getName());
        viewHolder.textViewStartLocationAddress.setText(offer.getStartTerawhereLocation().getAddress());
        viewHolder.textViewSeatsBooked.setText(Integer.toString(booking.getSeatsBooked()));
        if (offer.getRemarks() != null && !offer.getRemarks().isEmpty()) {
            viewHolder.textViewRemarks.setText(offer.getRemarks());
            viewHolder.textViewRemarksLabel.setVisibility(View.VISIBLE);
            viewHolder.textViewRemarks.setVisibility(View.VISIBLE);
        } else {
            viewHolder.textViewRemarksLabel.setVisibility(View.GONE);
            viewHolder.textViewRemarks.setVisibility(View.GONE);
        }
        viewHolder.textViewVehicle.setText(offer.getVehicle().getDescription() + " / " + offer.getVehicle().getPlateNumber());
        viewHolder.textViewVehicleModel.setText(offer.getVehicle().getModel());

        // check card collapse/expand
        final boolean[] shouldExpand = isCollapse(viewHolder, booking);

        // set listeners for collapse/expand offer details
        setOfferItemRelativeLayoutListener(viewHolder, shouldExpand);
        setDetailsTextViewListener(viewHolder, shouldExpand);

        // set listeners for directions
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

        viewHolder.textViewCancel.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder deleteConfirmationDialog = new AlertDialog.Builder(viewGroup.getContext());

                deleteConfirmationDialog.setTitle(DELETE_BOOKING);
                deleteConfirmationDialog.setMessage(ARE_YOU_SURE_YOU_WANT_TO_DELETE_YOUR_BOOKING);
                deleteConfirmationDialog.setNegativeButton("Cancel", null); // dismisses by default
                deleteConfirmationDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Void> deleteRequest = TerawhereBackendServer.getApiInstance().deleteBooking(bookings.get(position).getBookingId());
                        deleteRequest.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    deleteBooking(position);

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

                createAlertDialog(alert);
            }
        });

    }

    private boolean[] isCollapse(ViewHolder viewHolder, Booking booking) {
        return new boolean[]{
                viewHolder.textViewEndLocationAddress.getVisibility() == View.GONE,
                viewHolder.textViewStartLocationAddress.getVisibility() == View.GONE,
                viewHolder.textViewSeatsBooked.getVisibility() == View.GONE,
                booking.getOffer().getRemarks() != null && !booking.getOffer().getRemarks().isEmpty() && viewHolder.textViewRemarks.getVisibility() == View.GONE,
                viewHolder.textViewVehicleModelLabel.getVisibility() == View.GONE,
                viewHolder.textViewVehicleModel.getVisibility() == View.GONE,
        };
    }

    private void setOfferItemRelativeLayoutListener(final ViewHolder viewHolder, final boolean[] shouldExpand) {
        viewHolder.relativeLayoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExpand(viewHolder, shouldExpand);
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
            viewHolder.textViewSeatsBookedLabel.setVisibility(View.VISIBLE);
            viewHolder.textViewSeatsBooked.setVisibility(View.VISIBLE);
            viewHolder.textViewVehicleModelLabel.setVisibility(View.VISIBLE);
            viewHolder.textViewVehicleModel.setVisibility(View.VISIBLE);
            viewHolder.textViewViewMore.setText(LESS_DETAILS);
            shouldExpand[0] = false;
        } else {
            viewHolder.textViewEndLocationAddress.setVisibility(View.GONE);
            viewHolder.textViewStartLocationAddress.setVisibility(View.GONE);
            viewHolder.textViewSeatsBookedLabel.setVisibility(View.GONE);
            viewHolder.textViewSeatsBooked.setVisibility(View.GONE);
            viewHolder.textViewRemarks.setVisibility(View.GONE);
            viewHolder.textViewVehicleModelLabel.setVisibility(View.GONE);
            viewHolder.textViewVehicleModel.setVisibility(View.GONE);
            viewHolder.textViewViewMore.setText(MORE_DETAILS);
            shouldExpand[0] = true;
        }

        TransitionManager.beginDelayedTransition(viewGroup);
        viewHolder.itemView.setActivated(shouldExpand[0]);
    }

    private void createAlertDialog(AlertDialog alert) {
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);
        nbutton.setText(CANCEL);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor(TERAWHERE_PRIMARY_COLOR));
        pbutton.setText(CONFIRM);
    }

    private Spanned setTextBold(String title, String text) {
        return Html.fromHtml(title + "<b>" + text + "</b>");
    }

    @Override
    public int getItemCount() {
        return bookings == null ? 0 : bookings.size();
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
        private TextView textViewSeatsBookedLabel;
        private TextView textViewSeatsBooked;

        private TextView textViewVehicleLabel;
        private TextView textViewVehicle;
        private TextView textViewVehicleModelLabel;
        private TextView textViewVehicleModel;

        private TextView textViewViewMore;
        private TextView textViewCancel;

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
            textViewSeatsBookedLabel = (TextView) view.findViewById(R.id.text_view_seats_booked_label);
            textViewSeatsBooked = (TextView) view.findViewById(R.id.text_view_seats_booked);
            textViewRemarksLabel = (TextView) view.findViewById(R.id.text_view_remarks_label);
            textViewRemarks = (TextView) view.findViewById(R.id.text_view_remarks);
            textViewViewMore = (TextView) view.findViewById(R.id.text_view_view_more);
            textViewCancel = (TextView) view.findViewById(R.id.text_view_cancel);
            textViewVehicleLabel = (TextView) view.findViewById(R.id.text_view_vehicle_label);
            textViewVehicle = (TextView) view.findViewById(R.id.text_view_vehicle);
            textViewVehicleModelLabel = (TextView) view.findViewById(R.id.text_view_vehicle_model_label);
            textViewVehicleModel = (TextView) view.findViewById(R.id.text_view_vehicle_model);
        }
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    private void deleteBooking(int position) {
        bookings.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }
}
