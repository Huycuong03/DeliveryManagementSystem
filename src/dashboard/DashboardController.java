package dashboard;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import model.*;

public class DashboardController implements Initializable{
    @FXML
    private AnchorPane billPane;

    @FXML
    private ComboBox<String> codComboBox;

    @FXML
    private TextField customerDistance;

    @FXML
    private Text deliveredParcel;

    @FXML
    private Text deliveringParcel;

    @FXML
    private TextField fromWeight;

    @FXML
    private AnchorPane homePane;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private TextField newCOD;

    @FXML
    private TextField newParceTitle;

    @FXML
    private TextArea newParcelNote;

    @FXML
    private AnchorPane newParcelPane;

    @FXML
    private TextField newParcelWeight;

    @FXML
    private TableColumn<Parcel, Integer> parcelIdColumn;

    @FXML
    private TextArea parcelNote;

    @FXML
    private LineChart<String, Integer> parcelPerDayChart;

    @FXML
    private TableColumn<Parcel, String> parcelRecipientColumn;

    @FXML
    private TableColumn<Parcel, Date> parcelSendDateColumn;

    @FXML
    private TableColumn<Parcel, String> parcelSenderColumn;

    @FXML
    private TableColumn<Parcel, Integer> parcelStatusColumn;

    @FXML
    private TableView<Parcel> parcelTable;

    @FXML
    private TextField parcelTitle;

    @FXML
    private TextField parcelWeight;

    @FXML
    private TextField recipientAddress;

    @FXML
    private TextField recipientEmail;

    @FXML
    private TextField recipientFirstName;

    @FXML
    private TextField recipientLastName;

    @FXML
    private TextField recipientPhoneNumber;

    @FXML
    private TextField recipientZip;

    @FXML
    private TextField recipientZipBill;

    @FXML
    private AnchorPane reportPane;

    @FXML
    private TextField searchBar;

    @FXML
    private ComboBox<String> searchComboBox;

    @FXML
    private AnchorPane searchPane;

    @FXML
    private TextField senderAddress;

    @FXML
    private TextField senderEmail;

    @FXML
    private TextField senderFirstName;

    @FXML
    private TextField senderLastName;

    @FXML
    private TextField senderPhoneNumber;

    @FXML
    private TextField senderZip;

    @FXML
    private TextField senderZipBill;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private TextField toWeight;

    @FXML
    private TextField totalFee;

    @FXML
    private Text totalParcel;

    @FXML
    private TableColumn<?, ?> trackAddressColumn;

    @FXML
    private TableColumn<?, ?> trackDateColumn;

    @FXML
    private TableView<?> trackingTable;

    @FXML
    private TableColumn<?, ?> tracskNoteColumn;

    @FXML
    private ComboBox<String> transportComboBox;

    @FXML
    private TextField transportFee;

    @FXML
    private PieChart transportRevenueChart;

    @FXML
    private TextField weightRate;

    private DBConnection db = new DBConnection();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initComboBox();
        resetHomePane();
        initParcelTable();
    }

    final String[] MONTHS = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    final String[] TRANSPORT = {"Saving", "Standard", "Express", "Instant"};
    final String[] SEARCH_OPTION = {"ID", "Title", "Sender", "Recipient"};
    final String[] STATUS = {"All", "In-Stock", "Transporting", "Delivering", "For Re-delivery", "Delivered", "Return Processing", "For Return", "Returning", "Returned"};
    final String[] COD = {"All", "No COD", "For Collection", "Collected", "Returning","Returned","Storing"};
    

    public void initComboBox(){
        ObservableList<String> comboBoxItems = FXCollections.observableArrayList(MONTHS);
        monthComboBox.setItems(comboBoxItems);
        monthComboBox.setValue(MONTHS[java.time.LocalDate.now().getMonthValue()-1]);

        comboBoxItems = FXCollections.observableArrayList(TRANSPORT);
        transportComboBox.setItems(comboBoxItems);
        transportComboBox.setValue(TRANSPORT[0]);

        comboBoxItems = FXCollections.observableArrayList(SEARCH_OPTION);
        searchComboBox.setItems(comboBoxItems);
        searchComboBox.setValue(SEARCH_OPTION[0]);

        comboBoxItems = FXCollections.observableArrayList(STATUS);
        statusComboBox.setItems(comboBoxItems);
        statusComboBox.setValue(STATUS[0]);

        comboBoxItems = FXCollections.observableArrayList(COD);
        codComboBox.setItems(comboBoxItems);
        codComboBox.setValue(COD[0]);
    }

    public void resetHomePane(){
        int month = monthComboBox.getSelectionModel().getSelectedIndex()+1;
        totalParcel.setText(db.getTotalParcel(month));
        deliveredParcel.setText(db.getDeliveredParcel(month));
        deliveringParcel.setText(db.getDeliveringParcel(month));

        parcelPerDayChart.getData().clear();
        parcelPerDayChart.getData().add(db.getParcelPerDayData(month));
    }

    public void toHomePane(){
        homePane.setVisible(true);
        monthComboBox.setVisible(true);
        newParcelPane.setVisible(false);
        searchPane.setVisible(false);
        reportPane.setVisible(false);
    }

    public void toNewParcelPane(){
        homePane.setVisible(false);
        monthComboBox.setVisible(false);
        newParcelPane.setVisible(true);
        searchPane.setVisible(false);
        reportPane.setVisible(false);
    }

    public void toSearchPane(){
        homePane.setVisible(false);
        monthComboBox.setVisible(false);
        newParcelPane.setVisible(false);
        searchPane.setVisible(true);
        reportPane.setVisible(false);
    }

    public void toReportPane(){
        homePane.setVisible(false);
        monthComboBox.setVisible(true);
        newParcelPane.setVisible(false);
        searchPane.setVisible(false);
        reportPane.setVisible(true);
    }

    public void initParcelTable(){
        parcelIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        parcelSendDateColumn.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
        parcelSenderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        parcelRecipientColumn.setCellValueFactory(new PropertyValueFactory<>("recipient"));
        parcelStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        parcelTable.setItems(db.getParcelList(""));
    }

    public void resetParcelTable(){
        int searchOption = searchComboBox.getSelectionModel().getSelectedIndex();
        int statusFilterOption = statusComboBox.getSelectionModel().getSelectedIndex();
        int codfilterOption = codComboBox.getSelectionModel().getSelectedIndex();
        String searchKeyword = searchBar.getText();

        String filter = "";
        if(!searchKeyword.equals("")){
            switch(searchOption){
                case 0 -> filter += " and parcel# = "+searchKeyword;
                case 1 -> filter += " and title like '%"+searchKeyword+"%'";
                case 2 -> filter += " and A.full_name like '%"+searchKeyword+"%'";
                case 3 -> filter += " and B.full_name like '%"+searchKeyword+"%'";
            }
        }

        if(statusFilterOption!=0){
            filter += "and status = " + (statusFilterOption-1);
        }

        if(codfilterOption!=0) {
            if(codfilterOption==1) filter += " and  COD is null";
            else{
                filter += " and COD_status = " + (codfilterOption-2);
            }
        }
        System.out.println(filter);
        parcelTable.setItems(db.getParcelList(filter));
    }
}
