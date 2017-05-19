package tech.msociety.terawhere.mocks;

import tech.msociety.terawhere.models.Vehicle;
import tech.msociety.terawhere.utils.faker.Faker;

public class VehicleMock {
    private static Faker faker = new Faker();
    
    public static Vehicle getRandomVehicle() {
        return new Vehicle(faker.words(1), faker.words(2), faker.words(1));
    }
}
