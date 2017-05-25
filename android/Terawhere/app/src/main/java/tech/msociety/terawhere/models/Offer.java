package tech.msociety.terawhere.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Offer implements Parcelable {

    public static int STATUS_CANCELLED = 0;

    public static int STATUS_PENDING = 1;

    public static int STATUS_ONGOING = 2;

    public static int STATUS_COMPLETED = 3;

    public static int STATUS_EXPIRED = 4;

    private Integer offerId;

    private String offererId;
    
    private String offererName;
    
    private String offererDp;

    private Date meetupTime;

    private TerawhereLocation startTerawhereLocation;

    private TerawhereLocation endTerawhereLocation;

    private Vehicle vehicle;

    private Integer vacancy;

    private Integer seatsBooked;
    
    private BackendTimestamp backendTimestamp;
    
    private String remarks;

    private Integer status;
    
    public Offer(Integer offerId, String offererId, String offererName, Date meetupTime, TerawhereLocation startTerawhereLocation, TerawhereLocation endTerawhereLocation, Vehicle vehicle, Integer vacancy, Integer seatsBooked, Integer status, BackendTimestamp backendTimestamp) {
        this.offerId = offerId;
        this.offererId = offererId;
        this.offererName = offererName;
        this.meetupTime = meetupTime;
        this.startTerawhereLocation = startTerawhereLocation;
        this.endTerawhereLocation = endTerawhereLocation;
        this.vehicle = vehicle;
        this.vacancy = vacancy;
        this.seatsBooked = seatsBooked;
        this.backendTimestamp = backendTimestamp;
        this.status = status;
    }
    
    public Integer getOfferId() {
        return offerId;
    }
    
    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }
    
    public String getOffererId() {
        return offererId;
    }
    
    public void setOffererId(String offererId) {
        this.offererId = offererId;
    }
    
    public String getOffererName() {
        return offererName;
    }
    
    public void setOffererName(String offererName) {
        this.offererName = offererName;
    }
    
    public String getOffererDp() {
        return offererDp;
    }
    
    public void setOffererDp(String offererDp) {
        this.offererDp = offererDp;
    }

    public Date getMeetupTime() {
        return meetupTime;
    }
    
    public void setMeetupTime(Date meetupTime) {
        this.meetupTime = meetupTime;
    }

    public TerawhereLocation getStartTerawhereLocation() {
        return startTerawhereLocation;
    }
    
    public void setStartTerawhereLocation(TerawhereLocation startTerawhereLocation) {
        this.startTerawhereLocation = startTerawhereLocation;
    }

    public TerawhereLocation getEndTerawhereLocation() {
        return endTerawhereLocation;
    }
    
    public void setEndTerawhereLocation(TerawhereLocation endTerawhereLocation) {
        this.endTerawhereLocation = endTerawhereLocation;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    public Integer getVacancy() {
        return vacancy;
    }
    
    public void setVacancy(Integer vacancy) {
        this.vacancy = vacancy;
    }

    public Integer getSeatsBooked() {
        return seatsBooked;
    }
    
    public void setSeatsBooked(Integer seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public Integer getSeatsRemaining() {
        return vacancy - seatsBooked;
    }
    
    public BackendTimestamp getBackendTimestamp() {
        return backendTimestamp;
    }
    
    public void setBackendTimestamp(BackendTimestamp backendTimestamp) {
        this.backendTimestamp = backendTimestamp;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean isPast() {
        if (getMeetupTime().before(new Date())) {
            return true;
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.offerId);
        dest.writeString(this.offererId);
        dest.writeString(this.offererName);
        dest.writeString(this.offererDp);
        dest.writeLong(this.meetupTime != null ? this.meetupTime.getTime() : -1);
        dest.writeParcelable(this.startTerawhereLocation, flags);
        dest.writeParcelable(this.endTerawhereLocation, flags);
        dest.writeParcelable(this.vehicle, flags);
        dest.writeValue(this.vacancy);
        dest.writeValue(this.seatsBooked);
        dest.writeParcelable(this.backendTimestamp, flags);
        dest.writeString(this.remarks);
        dest.writeValue(this.status);
    }

    protected Offer(Parcel in) {
        this.offerId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.offererId = in.readString();
        this.offererName = in.readString();
        this.offererDp = in.readString();
        long tmpMeetupTime = in.readLong();
        this.meetupTime = tmpMeetupTime == -1 ? null : new Date(tmpMeetupTime);
        this.startTerawhereLocation = in.readParcelable(TerawhereLocation.class.getClassLoader());
        this.endTerawhereLocation = in.readParcelable(TerawhereLocation.class.getClassLoader());
        this.vehicle = in.readParcelable(Vehicle.class.getClassLoader());
        this.vacancy = (Integer) in.readValue(Integer.class.getClassLoader());
        this.seatsBooked = (Integer) in.readValue(Integer.class.getClassLoader());
        this.backendTimestamp = in.readParcelable(BackendTimestamp.class.getClassLoader());
        this.remarks = in.readString();
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Offer> CREATOR = new Creator<Offer>() {
        @Override
        public Offer createFromParcel(Parcel source) {
            return new Offer(source);
        }

        @Override
        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };
}
