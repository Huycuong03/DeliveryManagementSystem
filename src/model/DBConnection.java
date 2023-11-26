package model;

import java.sql.*;
import java.time.YearMonth;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

public class DBConnection {
    private Connection con;
    private PreparedStatement state;
    private ResultSet res;

    public DBConnection(){
        try {
            this.con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "sys as sysdba", "huycuong03");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getTotalParcel(int month){
        try {
            state = con.prepareStatement("select count(parcel#) as total_parcel from sending where extract(month from send_date) = ?");
            state.setInt(1, month);
            res = state.executeQuery();
            res.next();
            return String.valueOf(res.getInt("total_parcel"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public String getDeliveredParcel(int month){
        try {
            state = con.prepareStatement("select count(parcel#) as delivered_parcel from sending natural join parcel where extract(month from send_date) = ? and status = 4");
            state.setInt(1, month);
            res = state.executeQuery();
            res.next();
            return String.valueOf(res.getInt("delivered_parcel"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public String getDeliveringParcel(int month){
        try {
            state = con.prepareStatement("select count(parcel#) as delivering_parcel from sending natural join parcel where extract(month from send_date) = ? and status between 0 and 3");
            state.setInt(1, month);
            res = state.executeQuery();
            res.next();
            return String.valueOf(res.getInt("delivering_parcel"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "0";
    }


    public XYChart.Series<String, Integer> getParcelPerDayData(int month){
        XYChart.Series<String, Integer> dataSeries = new XYChart.Series<>();
        try {
            state = con.prepareStatement("select extract(day from send_date) as day, count(parcel#) as num_parcel from sending where extract(month from send_date) = ? group by send_date order by send_date");
            state.setInt(1, month);
            res = state.executeQuery();
            int day = 1;
            while(res.next()){
                while(res.getInt("day")>day){
                    dataSeries.getData().add(new XYChart.Data<String, Integer>(String.valueOf(day), 0));
                    day++;
                }
                dataSeries.getData().add(new XYChart.Data<String, Integer>(res.getString("day"), res.getInt("num_parcel")));
                day++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSeries;
    }

    public ObservableList<Parcel> getParcelList(String filter){
        ObservableList<Parcel> parcelList = FXCollections.observableArrayList();
        try {
            state = con.prepareStatement("select parcel#, weight, transport#, title, note, COD, COD_status, status, send_date, A.full_name as sender, B.full_name as recipient from parcel natural join sending, customer A, customer B where sender# = A.customer# and recipient# = B.customer# "+filter);
            res = state.executeQuery();
            while(res.next()){
                parcelList.add(new Parcel(res));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return parcelList;
    }

}