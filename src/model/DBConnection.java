package model;

import java.sql.*;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

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

    public ObservableList<TrackingLog> getTrackingLog(int parcelId){
        ObservableList<TrackingLog> trackingLogList = FXCollections.observableArrayList();
        try {
            state = con.prepareStatement("select send_date, address, warehouse.wh# from sending natural join parcel natural join packing, warehouse where packing.wh# = warehouse.wh# and parcel# = ?");
            state.setInt(1, parcelId);
            res = state.executeQuery();
            while(res.next()){
                trackingLogList.add(new TrackingLog(res.getDate("send_date"), String.format("WH%02d (%s)", res.getInt("wh#"), res.getString("address")), null));
            }

            state = con.prepareStatement("select * from packing where parcel# = ?");
            state.setInt(1, parcelId);
            res = state.executeQuery();
            PreparedStatement tmp = con.prepareStatement("select * from transition, warehouse where  transition.wh# = ? and transition.pack_date = to_date(?,'dd-mm-yy') and transition.cargo# = ? and transition.dst_wh# = warehouse.wh#");
            while(res.next()){
                tmp.setInt(1, res.getInt("wh#"));
                tmp.setDate(2, res.getDate("pack_date"));
                tmp.setInt(3, res.getInt("cargo#"));
                ResultSet temp = tmp.executeQuery();
                while(temp.next()){
                    trackingLogList.add(new TrackingLog(temp.getDate("transit_date"), String.format("WH%02d (%s)", temp.getInt("dst_wh#"), temp.getString("address")), temp.getString("note")));
                }
            }

            state = con.prepareStatement("select * from delivery join parcel using (parcel#) natural join sending, customer where recipient# = customer# and parcel# = ?");
            state.setInt(1, parcelId);
            res = state.executeQuery();
            while(res.next()){
                trackingLogList.add(new TrackingLog(res.getDate("delivery_date"), res.getString("address"), res.getString("note")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trackingLogList;
    }

    public void updateParcel(String title, String note, int parcelId){
        try {
            PreparedStatement state = con.prepareStatement("update parcel set title = ?, note = ? where parcel# = ?");
            state.setString(1, title);
            state.setString(2, note);
            state.setInt(3, parcelId);
            state.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCountCustomer(){
        try {
            res = con.createStatement().executeQuery("select count(*) as num_customer from customer");
            res.next();
            return res.getInt("num_customer");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCountParcel(){
        try {
            res = con.createStatement().executeQuery("select count(*) as num_parcel from parcel");
            res.next();
            return res.getInt("num_parcel");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isNewCustomer(Customer customer){
        try {
            state = con.prepareStatement("select * from customer where full_name = ? and phone# = ?");
            state.setString(1, customer.getName());
            state.setString(2, customer.getPhoneNumber());
            return (!state.executeQuery().next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void registerNewParcel(Customer sender, Customer recipient, Parcel parcel){
        try {
            Statement temp = con.createStatement();
            String senderSql = createRegisterCustomerSql(sender);
            if(senderSql==null) return;
            else temp.addBatch(senderSql);

            String recipientSql = createRegisterCustomerSql(recipient);
            if(recipientSql==null) return;
            else temp.addBatch(recipientSql);

            String sqlFormat = "insert into parcel values (%d,%d,%d,'%s','%s',%s,%s,0)"; 
            temp.addBatch(String.format(sqlFormat, parcel.getId(),parcel.getWeight(),parcel.getTransport(),parcel.getTitle(),parcel.getNote(),(parcel.getCOD()==null)?null:parcel.getCOD(),(parcel.getCOD()==null)?null:0));   
            sqlFormat = "insert into sending (sending#,send_date,sender#,recipient#,parcel#) values (%d,to_date('%s','yyyy-mm-dd'),%d,%d,%d)";
            temp.addBatch(String.format(sqlFormat, parcel.getId(),parcel.getSendDate(),sender.getId(),recipient.getId(),parcel.getId()));

            temp.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String createRegisterCustomerSql(Customer customer){
        if(isNewCustomer(customer)){
            String sqlFormat = "insert into customer values(%d,'%s','%s','%s','%s',null,null,'%s')";
            return String.format(sqlFormat, customer.getId(),customer.getName(),customer.getPhoneNumber(),customer.getZip(),customer.getAddress(),customer.getEmail());           
        }else{
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setHeaderText("Customer "+customer.getName()+"'s account already existed");
            alert.setContentText("Do you want to update customer's information?");
            if(alert.showAndWait().get()==ButtonType.OK){
                String sqlFormat = "update customer set zip# = '%s', address = '%s', email = '%s' where full_name = '%s' and phone# ='%s'";
                return String.format(sqlFormat, customer.getZip(),customer.getAddress(),customer.getEmail(),customer.getName(),customer.getPhoneNumber());                                            
            }
        }       
        return null;
    }

    public TreeMap<String, Integer> getTransportRevenue(int month){
        TreeMap<String, Integer> treeMap = new TreeMap<>();       
        try{
            state = con.prepareStatement("select title, revenue from transport natural join (select transport#, sum(payment)as revenue from parcel natural join sending where extract(month from send_date) = ? group by transport#)");
            state.setInt(1, month);
            res = state.executeQuery();
            while(res.next()){
                treeMap.put(res.getString("title"), res.getInt("revenue"));
            }            
        }catch(SQLException e){
            e.printStackTrace();
        }        
        return treeMap;
    }

    public XYChart.Series<String, Integer> getWarehouseMonthlyParcel(int month){
        XYChart.Series<String, Integer> dataSeries = new XYChart.Series<>();
        try {
            state = con.prepareStatement("select wh#,(num_parcel1+num_parcel2) as total_parcel " +
                    "from(select wh#, count(parcel#) as num_parcel1 " + 
                    "from employee inner join shipper on (employee# = shipper#) natural join delivery where extract(month from delivery_date) = ? group by wh#) " + 
                    "natural join " + 
                    "(select wh#, count(parcel#) as num_parcel2 from packing where extract(month from pack_date) = ? group by wh#) order by wh#");
            state.setInt(1, month);
            state.setInt(2, month);
            res = state.executeQuery();
            while(res.next()){
                dataSeries.getData().add(new XYChart.Data<String, Integer>(res.getString("wh#"), res.getInt("total_parcel")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSeries;
    }

    public String getTotalMonthlyRevenue(int month){
        try {
            state = con.prepareStatement("select sum(payment) as revenue from sending where extract(month from send_date) = ?");
            state.setInt(1, month);
            res = state.executeQuery();
            res.next();
            return String.format("%dM", res.getInt("revenue")/1_000_000);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "NaN";       
    }

}