package model;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import dashboard.DashboardController;

public class Parcel {
    private int id;
    private int weight;
    private int transport;
    private String title;
    private String note;
    private Integer COD;
    private Integer CODStatus;
    private int status;
    private String sender;
    private String recipient;
    private Date sendDate;

    public int getId() {
        return id;
    }

    public int getWeight() {
        return weight;
    }

    public int getTransport() {
        return transport;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public Integer getCOD() {
        return COD;
    }

    public Integer getCODStatus() {
        return CODStatus;
    }

    public String getStatus() {
        return dashboard.DashboardController.STATUS[status+1];
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public Date getSendDate(){
        return sendDate;
    }

    public Parcel(ResultSet parcelInfo) throws SQLException {
        this.id = parcelInfo.getInt("parcel#");
        this.weight = parcelInfo.getInt("weight");
        this.transport = parcelInfo.getInt("transport#");
        this.title = parcelInfo.getString("title");
        this.note = parcelInfo.getString("note");
        this.COD = (parcelInfo.getObject("COD")==null)?null:parcelInfo.getInt("COD");
        this.CODStatus = (parcelInfo.getObject("COD_status")==null)?null:parcelInfo.getInt("COD_status");
        this.status = parcelInfo.getInt("status");
        this.sender = parcelInfo.getString("sender");
        this.recipient = parcelInfo.getString("recipient");
        this.sendDate = parcelInfo.getDate("send_date");
    }

    
}
