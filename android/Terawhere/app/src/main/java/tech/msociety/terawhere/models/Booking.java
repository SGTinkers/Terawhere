package tech.msociety.terawhere.models;

public class Booking extends Offer {

    private Integer bookingId;
    private String userId;
    private Integer offerId;
    private Integer seatsBooked;
    private BackendTimestamp bookingBackendTimestamp;


    public Booking(Integer bookingId, String userId, Integer offerId, Integer pax, BackendTimestamp bookingBackendTimestamp, Offer offer) {
        super(offer.getOfferId(), offer.getOffererId(), offer.getMeetupTime(),
                offer.getStartTerawhereLocation(), offer.getEndTerawhereLocation(),
                offer.getVehicle(), offer.getVacancy(), offer.getBackendTimestamp(),
                offer.getRemarks(), offer.getSeatsRemaining(), offer.getSeatsBooked(),
                offer.getDriverName());

        this.bookingId = bookingId;
        this.userId = userId;
        this.seatsBooked = pax;
        this.bookingBackendTimestamp = bookingBackendTimestamp;

    }

    public Integer getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public Integer getOfferId() {
        return offerId;
    }

    @Override
    public Integer getSeatsBooked() {
        return seatsBooked;
    }

    public BackendTimestamp getBookingBackendTimestamp() {
        return bookingBackendTimestamp;
    }


}
