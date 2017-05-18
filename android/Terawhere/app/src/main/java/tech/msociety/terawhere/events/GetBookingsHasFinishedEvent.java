package tech.msociety.terawhere.events;

import java.util.List;

import tech.msociety.terawhere.models.Booking;

public class GetBookingsHasFinishedEvent {
    private List<Booking> bookings;
    
    public GetBookingsHasFinishedEvent(List<Booking> bookings) {
        this.bookings = bookings;
    }
    
    public List<Booking> getBookings() {
        return bookings;
    }
}
