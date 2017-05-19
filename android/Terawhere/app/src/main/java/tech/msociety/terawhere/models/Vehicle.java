package tech.msociety.terawhere.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Vehicle implements Parcelable {
    private String plateNumber;
    private String description;
    private String model;
    
    public Vehicle(String plateNumber, String description, String model) {
        this.plateNumber = plateNumber;
        this.description = description;
        this.model = model;
    }

    protected Vehicle(Parcel in) {
        this.plateNumber = in.readString();
        this.description = in.readString();
        this.model = in.readString();
    }

    public static final Creator<Vehicle> CREATOR = new Creator<Vehicle>() {
        @Override
        public Vehicle createFromParcel(Parcel in) {
            return new Vehicle(in);
        }

        @Override
        public Vehicle[] newArray(int size) {
            return new Vehicle[size];
        }
    };

    public String getPlateNumber() {
        return plateNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getModel() {
        return model;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(plateNumber);
        dest.writeString(description);
        dest.writeString(model);
    }
}
