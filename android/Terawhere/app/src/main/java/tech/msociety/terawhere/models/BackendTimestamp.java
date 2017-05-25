package tech.msociety.terawhere.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class BackendTimestamp implements Parcelable {
    private Date dateCreated;
    private Date dateUpdated;
    private Date dateDeleted;
    
    public BackendTimestamp(Date dateCreated, Date dateUpdated, Date dateDeleted) {
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.dateDeleted = dateDeleted;
    }
    
    public Date getDateCreated() {
        return dateCreated;
    }
    
    public Date getDateUpdated() {
        return dateUpdated;
    }
    
    public Date getDateDeleted() {
        return dateDeleted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.dateCreated != null ? this.dateCreated.getTime() : -1);
        dest.writeLong(this.dateUpdated != null ? this.dateUpdated.getTime() : -1);
        dest.writeLong(this.dateDeleted != null ? this.dateDeleted.getTime() : -1);
    }

    protected BackendTimestamp(Parcel in) {
        long tmpDateCreated = in.readLong();
        this.dateCreated = tmpDateCreated == -1 ? null : new Date(tmpDateCreated);
        long tmpDateUpdated = in.readLong();
        this.dateUpdated = tmpDateUpdated == -1 ? null : new Date(tmpDateUpdated);
        long tmpDateDeleted = in.readLong();
        this.dateDeleted = tmpDateDeleted == -1 ? null : new Date(tmpDateDeleted);
    }

    public static final Creator<BackendTimestamp> CREATOR = new Creator<BackendTimestamp>() {
        @Override
        public BackendTimestamp createFromParcel(Parcel source) {
            return new BackendTimestamp(source);
        }

        @Override
        public BackendTimestamp[] newArray(int size) {
            return new BackendTimestamp[size];
        }
    };

}
