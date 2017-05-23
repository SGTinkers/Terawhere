package tech.msociety.terawhere.models;

public class Booking {

    private Integer bookingId;
    private String userId;
    private Integer seatsBooked;
    private BackendTimestamp bookingBackendTimestamp;
    private Offer offer;


    public Booking(Integer bookingId, String userId, Integer pax, BackendTimestamp bookingBackendTimestamp, Offer offer) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.seatsBooked = pax;
        this.bookingBackendTimestamp = bookingBackendTimestamp;
        this.offer = offer;

    }

    public Integer getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public Integer getSeatsBooked() {
        return seatsBooked;
    }

    public BackendTimestamp getBookingBackendTimestamp() {
        return bookingBackendTimestamp;
    }

    public Offer getOffer() {
        return offer;
    }
}
