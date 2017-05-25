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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.msociety.terawhere.R;
import tech.msociety.terawhere.events.BookingDeletedEvent;
import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.networkcalls.server.TerawhereBackendServer;
import tech.msociety.terawhere.utils.DateUtils;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {
    private static final String LESS_DETAILS = "\u2014 LESS DETAILS";
    private static final String MORE_DETAILS = "+ MORE DETAILS";
    public static final String CANCEL_BOOKING_TITLE = "Confirm Cancel Booking?";
    private static final String CANCEL_BOOKING_INFO = "The driver will be informed of your cancellation.";
    public static final String LOG_ERROR_DELETE_MESSAGE = "ERROR_DELETE_MESSAGE";
    private static final String CANCEL = "Keep";
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
        }
        viewHolder.textViewVehicle.setText(offer.getVehicle().getDescription() + " " + offer.getVehicle().getModel() + " [" + offer.getVehicle().getPlateNumber() + "]");
        Picasso.with(context)
                .load(offer.getOffererDp())
                .transform(new CropCircleTransformation())
                .into(viewHolder.imageViewDriverAvatar);
        viewHolder.textViewDriver.setText(offer.getOffererName());
        if (booking.getBookingStatus() == 2) {
            // Past Booking
            viewHolder.textViewBookingPast.setText("Past Booking");
            viewHolder.textViewBookingPast.setVisibility(View.VISIBLE);
            viewHolder.textViewCancel.setVisibility(View.GONE);
        } else if (booking.getBookingStatus() == 1) {
            // Cancelled
            viewHolder.textViewBookingPast.setText("Cancelled");
            viewHolder.textViewBookingPast.setVisibility(View.VISIBLE);
            viewHolder.textViewCancel.setVisibility(View.GONE);
        } else {
            // Neither
            viewHolder.textViewBookingPast.setVisibility(View.GONE);
            viewHolder.textViewCancel.setVisibility(View.VISIBLE);
        }

        // check card collapse/expand
        final boolean[] shouldExpand = isCollapse(viewHolder, booking);

        // set listeners for collapse/expand offer details
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

                deleteConfirmationDialog.setTitle(CANCEL_BOOKING_TITLE);
                deleteConfirmationDialog.setMessage(CANCEL_BOOKING_INFO);
                deleteConfirmationDialog.setNegativeButton(CANCEL, null); // dismisses by default
                deleteConfirmationDialog.setPositiveButton(CONFIRM, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Booking booking = bookings.get(position);
                        Call<Void> deleteRequest = TerawhereBackendServer.getApiInstance().deleteBooking(booking.getBookingId());
                        deleteRequest.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    EventBus.getDefault().post(new BookingDeletedEvent(booking));
                                } else {
                                    try {
                                        TerawhereBackendServer.ErrorDatum.ParseErrorAndToast(context, response);
                                    } catch (IOException e) {
                                        onFailure(call, e);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                TerawhereBackendServer.ErrorDatum.ToastUnknownError(context, t);
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
                viewHolder.textViewDriverLabel.getVisibility() == View.GONE,
                viewHolder.imageViewDriverAvatar.getVisibility() == View.GONE,
                viewHolder.textViewDriver.getVisibility() == View.GONE,
        };
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
            if (viewHolder.textViewRemarks.getText() != null && !viewHolder.textViewRemarks.getText().toString().isEmpty()) {
                viewHolder.textViewRemarksLabel.setVisibility(View.VISIBLE);
                viewHolder.textViewRemarks.setVisibility(View.VISIBLE);
            }
            viewHolder.textViewDriverLabel.setVisibility(View.VISIBLE);
            viewHolder.imageViewDriverAvatar.setVisibility(View.VISIBLE);
            viewHolder.textViewDriver.setVisibility(View.VISIBLE);
            viewHolder.textViewViewMore.setText(LESS_DETAILS);
            shouldExpand[0] = false;
        } else {
            viewHolder.textViewEndLocationAddress.setVisibility(View.GONE);
            viewHolder.textViewStartLocationAddress.setVisibility(View.GONE);
            viewHolder.textViewSeatsBookedLabel.setVisibility(View.GONE);
            viewHolder.textViewSeatsBooked.setVisibility(View.GONE);
            if (viewHolder.textViewRemarks.getText() != null && !viewHolder.textViewRemarks.getText().toString().isEmpty()) {
                viewHolder.textViewRemarksLabel.setVisibility(View.GONE);
                viewHolder.textViewRemarks.setVisibility(View.GONE);
            }
            viewHolder.textViewDriverLabel.setVisibility(View.GONE);
            viewHolder.imageViewDriverAvatar.setVisibility(View.GONE);
            viewHolder.textViewDriver.setVisibility(View.GONE);
            viewHolder.textViewViewMore.setText(MORE_DETAILS);
            shouldExpand[0] = true;
        }

        TransitionManager.beginDelayedTransition(viewGroup);
        viewHolder.itemView.setActivated(shouldExpand[0]);
    }

    private void createAlertDialog(AlertDialog alert) {
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.parseColor(TERAWHERE_PRIMARY_COLOR));
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
        private TextView textViewDriverLabel;
        private ImageView imageViewDriverAvatar;
        private TextView textViewDriver;

        private TextView textViewViewMore;
        private TextView textViewCancel;

        private TextView textViewBookingPast;

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
            textViewDriverLabel = (TextView) view.findViewById(R.id.text_view_driver_label);
            imageViewDriverAvatar = (ImageView) view.findViewById(R.id.image_view_driver_avatar);
            textViewDriver = (TextView) view.findViewById(R.id.text_view_driver);
            textViewBookingPast = (TextView) view.findViewById(R.id.text_view_booking_past);
        }
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

}
