package model;

import java.sql.Date;

public class TrackingLog {
    private Date date;
    private String destination;
    private String note;
    
    public Date getDate() {
        return date;
    }

    public String getDestination() {
        return destination;
    }

    public String getNote() {
        return note;
    }

    public TrackingLog(Date date, String destination, String note) {
        this.date = date;
        this.destination = destination;
        this.note = note;
    }

}
