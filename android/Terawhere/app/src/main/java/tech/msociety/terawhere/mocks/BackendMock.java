package tech.msociety.terawhere.mocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.msociety.terawhere.models.BackendTimestamp;
import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.models.OfferRevamp;
import tech.msociety.terawhere.models.TerawhereLocation;
import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.utils.faker.Faker;

public class BackendMock {
    private static Faker faker = new Faker();
    
    public static List<OfferRevamp> getOffers() {
        List<OfferRevamp> offerRevamps = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            Integer offerId = (int) faker.number(1);
            String offererId = faker.words(1);
            Date meetupTime = new Date();
            TerawhereLocation startTerawhereLocation = LocationMock.getRandomLocation();
            TerawhereLocation endTerawhereLocation = LocationMock.getRandomLocation();
            Vehicle vehicle = VehicleMock.getRandomVehicle();
            Integer vacancy = (int) faker.number(1);
            BackendTimestamp backendTimestamp = BackendTimestampMock.getRandomBackendTimestamp();
            String remarks = faker.words(1);
    
            OfferRevamp offerRevamp = new OfferRevamp(offerId, offererId, meetupTime, startTerawhereLocation, endTerawhereLocation, vehicle, vacancy, backendTimestamp, remarks);
            offerRevamps.add(offerRevamp);
        }
        
        return offerRevamps;
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
