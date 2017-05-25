package tech.msociety.terawhere.events;

import tech.msociety.terawhere.models.Booking;

public class BookingDeletedEvent {

    private Booking booking;

    public BookingDeletedEvent(Booking booking) {
        this.booking = booking;
    }

    public Booking getBooking() {
        return booking;
    }
}
