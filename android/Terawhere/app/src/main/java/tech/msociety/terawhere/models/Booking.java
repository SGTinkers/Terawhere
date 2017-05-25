package tech.msociety.terawhere.models;

public class Booking {
    
    public Booking(Integer bookingId, String userId, String userName, String userDp, String userEmail, Integer seatsBooked, BackendTimestamp bookingBackendTimestamp, Offer offer) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.userName = userName;
        this.userDp = userDp;
        this.userEmail = userEmail;
        this.seatsBooked = seatsBooked;
        this.bookingBackendTimestamp = bookingBackendTimestamp;
        this.offer = offer;
    }

    private Integer bookingId;
    private String userId;
    
    private String userName;
    private String userDp;
    private String userEmail;
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
    
    public String getUserName() {
        return userName;
    }
    
    public String getUserDp() {
        return userDp;
    }
    
    public String getUserEmail() {
        return userEmail;
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

    public Integer getBookingStatus() {
        if (offer == null) {
            return 0;
        }

        if (offer.getStatus() == 1 && getBookingBackendTimestamp().getDateDeleted() != null) {
            return 1;
        } else if (offer.getStatus() != 1 && getBookingBackendTimestamp().getDateDeleted() != null) {
            return 2;
        }

        return 0;
    }
}
