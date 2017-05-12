package tech.msociety.terawhere.mocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tech.msociety.terawhere.models.Booking;
import tech.msociety.terawhere.models.Offer;
import tech.msociety.terawhere.utils.faker.Faker;

public class BackendMock {
    private static Faker faker = new Faker();

    public static List<Offer> getOffers() {
        List<Offer> offers = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String offerId = String.valueOf(faker.number(3));
            String driverId = String.valueOf(faker.number(5));
            String destination = faker.words(2);
            Integer numberOfSeats = (int) faker.number(1);
            Date timestamp = new Date();
            String remarks = faker.words(2);
            String vehicleColor = faker.words(1);
            String vehiclePlateNumber = faker.words(1);

            Offer offer = new Offer(i, driverId, destination, numberOfSeats, timestamp, remarks, vehicleColor, vehiclePlateNumber);
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

            Booking booking = new Booking(bookingId, passengerId, numberOfSeats, timestamp);
            bookings.add(booking);
        }

        return bookings;
    }
}
