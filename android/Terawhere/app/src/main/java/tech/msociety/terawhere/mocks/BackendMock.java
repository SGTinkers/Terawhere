package tech.msociety.terawhere.mocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.msociety.terawhere.models.BackendTimestamp;
import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.utils.faker.Faker;

public class BackendMock {
    private static Faker faker = new Faker();
    
    public static List<Offer> getOffers() {
        List<Offer> offers = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            Integer offerId = (int) faker.number(1);
            String offererId = faker.words(1);
            Date meetupTime = new Date();
            TerawhereLocation startTerawhereLocation = TerawhereLocationMock.getRandomLocation();
            TerawhereLocation endTerawhereLocation = TerawhereLocationMock.getRandomLocation();
            Vehicle vehicle = VehicleMock.getRandomVehicle();
            Integer vacancy = (int) faker.number(1);
            BackendTimestamp backendTimestamp = BackendTimestampMock.getRandomBackendTimestamp();
            String remarks = faker.words(1);
    
            Offer offer = new Offer(offerId, offererId, meetupTime, startTerawhereLocation, endTerawhereLocation, vehicle, vacancy, backendTimestamp, remarks);
            offers.add(offer);
        }
        
        return offers;
    }
    
    public static List<Booking> getBookings() {
        List<Booking> bookings = new ArrayList<>();
        
        for (int i = 0; i < 3; i++) {
            String bookingId = String.valueOf(faker.number(3));
            String passengerId = String.valueOf(faker.number(5));
            Integer numberOfSeats = (int) faker.number(1);
            String timestamp = faker.words(2);
            String destination = "DESTINATION";
            
            Booking booking = new Booking("DRIVERNAME", bookingId, passengerId, "0", timestamp, destination);
            bookings.add(booking);
        }
        
        return bookings;
    }
}
