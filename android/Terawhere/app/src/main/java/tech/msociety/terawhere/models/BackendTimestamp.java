package tech.msociety.terawhere.models;

import java.util.Date;

public class BackendTimestamp {
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
}
