package tech.msociety.terawhere.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Offer implements Parcelable {
    private Integer offerId;
    private String offererId;
    private Date meetupTime;
    private TerawhereLocation startTerawhereLocation;
    private TerawhereLocation endTerawhereLocation;
    private Vehicle vehicle;
    private Integer vacancy;
    private BackendTimestamp backendTimestamp;
    private String remarks;


    private Integer seatsBooked;
    private Integer seatsRemaining;
    private String driverName;

    public Offer(Integer offerId, String offererId, Date meetupTime, TerawhereLocation startTerawhereLocation, TerawhereLocation endTerawhereLocation, Vehicle vehicle, Integer vacancy, BackendTimestamp backendTimestamp, String remarks, Integer seatsRemaining, Integer seatsBooked, String driverName) {
        this.offerId = offerId;
        this.offererId = offererId;
        this.meetupTime = meetupTime;
        this.startTerawhereLocation = startTerawhereLocation;
        this.endTerawhereLocation = endTerawhereLocation;
        this.vehicle = vehicle;
        this.vacancy = vacancy;
        this.backendTimestamp = backendTimestamp;
        this.remarks = remarks != null ? remarks : "";
        this.seatsRemaining = seatsRemaining != null ? seatsRemaining : 0;
        this.seatsBooked = seatsBooked != null ? seatsBooked : 0;
        this.driverName = driverName;
    }

    public Offer(Integer offerId, String offererId, Date meetupTime, TerawhereLocation startTerawhereLocation, TerawhereLocation endTerawhereLocation, Vehicle vehicle, Integer vacancy, BackendTimestamp backendTimestamp, String remarks) {
        this.offerId = offerId;
        this.offererId = offererId;
        this.meetupTime = meetupTime;
        this.startTerawhereLocation = startTerawhereLocation;
        this.endTerawhereLocation = endTerawhereLocation;
        this.vehicle = vehicle;
        this.vacancy = vacancy;
        this.backendTimestamp = backendTimestamp;
        this.remarks = remarks;

    }


    protected Offer(Parcel in) {

        startTerawhereLocation = in.readParcelable(TerawhereLocation.class.getClassLoader());
        endTerawhereLocation = in.readParcelable(TerawhereLocation.class.getClassLoader());
        vehicle = in.readParcelable(Vehicle.class.getClassLoader());
        meetupTime = (Date) in.readSerializable();
        offererId = in.readString();
        remarks = in.readString();
        driverName = in.readString();
        offerId = in.readInt();
        vacancy = in.readInt();
        /*seatsRemaining = in.readInt();
        seatsBooked = in.readInt();
*/




    }

    public static final Creator<Offer> CREATOR = new Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };

    public Integer getOfferId() {
        return offerId;
    }

    public String getOffererId() {
        return offererId;
    }

    public Date getMeetupTime() {
        return meetupTime;
    }

    public TerawhereLocation getStartTerawhereLocation() {
        return startTerawhereLocation;
    }

    public TerawhereLocation getEndTerawhereLocation() {
        return endTerawhereLocation;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Integer getVacancy() {
        return vacancy;
    }

    public BackendTimestamp getBackendTimestamp() {
        return backendTimestamp;
    }

    public String getRemarks() {
        return remarks;
    }

    public Integer getSeatsBooked() {
        return seatsBooked;
    }

    public Integer getSeatsRemaining() {
        return seatsRemaining;
    }

    public String getDriverName() {
        return driverName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(startTerawhereLocation, flags);
        dest.writeParcelable(endTerawhereLocation, flags);
        dest.writeParcelable(vehicle, flags);
        dest.writeSerializable(meetupTime);
        dest.writeString(offererId);

        dest.writeString(remarks);
        dest.writeString(driverName);

        dest.writeInt(offerId);
        dest.writeInt(vacancy);

        /*dest.writeInt(seatsRemaining);

        dest.writeInt(seatsBooked);*/




    }
}
