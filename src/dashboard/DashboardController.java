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
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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
    private CheckBox codCheckBox;

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
    private TextField parcelCOD;

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
    private TableColumn<TrackingLog, String> trackDstColumn;

    @FXML
    private TableColumn<TrackingLog, Date> trackDateColumn;

    @FXML
    private TableView<TrackingLog> trackingTable;

    @FXML
    private TableColumn<TrackingLog, String> trackNoteColumn;

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
        newCOD.disableProperty().bind(codCheckBox.selectedProperty().not());
        resetHomePane();
        initParcelTable();
        initTrackingTable();
    }

    public static final String[] MONTHS = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    public static final String[] TRANSPORT = {"Saving", "Standard", "Express", "Instant"};
    public static final String[] SEARCH_OPTION = {"ID", "Title", "Sender", "Recipient"};
    public static final String[] STATUS = {"All", "In-Stock", "Transporting", "Delivering", "For Re-delivery", "Delivered", "Return Processing", "For Return", "Returning", "Returned"};
    public static final String[] COD = {"All", "No COD", "For Collection", "Collected", "Returning","Returned","Storing"};
    
// Initializations

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

    public void initParcelTable(){
        parcelIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        parcelSendDateColumn.setCellValueFactory(new PropertyValueFactory<>("sendDate"));
        parcelSenderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
        parcelRecipientColumn.setCellValueFactory(new PropertyValueFactory<>("recipient"));
        parcelStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        parcelTable.setItems(db.getParcelList(""));
    }

    public void initTrackingTable(){
        trackDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        trackDstColumn.setCellValueFactory(new PropertyValueFactory<>("destination"));
        trackNoteColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
    }


// Event Handling

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

    public void resetParcelTable(){
        int searchOption = searchComboBox.getSelectionModel().getSelectedIndex();
        int statusFilterOption = statusComboBox.getSelectionModel().getSelectedIndex();
        int codfilterOption = codComboBox.getSelectionModel().getSelectedIndex();
        String weightFrom = fromWeight.getText();
        String weightTo = toWeight.getText();
        String searchKeyword = searchBar.getText();

        String filter = "";
        if(!searchKeyword.isBlank()){
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
        
        if(!weightFrom.isBlank()){
            filter += " and weight >= "+weightFrom;
        }

        if(!weightTo.isBlank()){
            filter += " and weight <="+weightTo;
        }

        parcelTable.setItems(db.getParcelList(filter));
    }

    private Parcel selectedParcel = null;

    public void selectParcelTable(){
        selectedParcel = parcelTable.getSelectionModel().getSelectedItem();
        if(selectedParcel==null) return;

        if(selectedParcel.getTitle()==null){
            parcelTitle.setText(null);
        }else{
            parcelTitle.setText(selectedParcel.getTitle());
        }

        if(selectedParcel.getCOD()==null){
            parcelCOD.setText(null);
        }else{
            parcelCOD.setText(String.valueOf(selectedParcel.getCOD()));
        }
        
        if(selectedParcel.getNote()==null){
            parcelNote.setText(null);
        }else{
            parcelNote.setText(selectedParcel.getNote());
        }

        trackingTable.setItems(db.getTrackingLog(selectedParcel.getId()));
    }

    public void updateParcelInfo(){
        if(selectedParcel==null) return;
        db.updateParcel(parcelTitle.getText(), parcelNote.getText(), selectedParcel.getId());
        resetParcelTable();
    }

    public void enableCOD(){
        parcelCOD.setDisable(false);
    }

    public boolean isValidForm(){
        if(senderFirstName.getText().isBlank()) return false;
        if(senderLastName.getText().isBlank()) return false;
        if(senderPhoneNumber.getText().isBlank()) return false;
        if(senderZip.getText().isBlank()) return false;
        if(senderAddress.getText().isBlank()) return false;

        if(recipientFirstName.getText().isBlank()) return false;
        if(recipientLastName.getText().isBlank()) return false;
        if(recipientPhoneNumber.getText().isBlank()) return false;
        if(recipientZip.getText().isBlank()) return false;
        if(recipientAddress.getText().isBlank()) return false;

        if(codCheckBox.selectedProperty().get()&&parcelCOD.getText().isBlank()) return false;

        return (!newParcelWeight.getText().isBlank());
    }

    public void registerNewParcel(){        
        if(!isValidForm()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Message");
            alert.setHeaderText("Invalid Information");
            alert.setContentText("You must fill in all mandatory information (*)");
            alert.show();
            return;
        }
        int cid = db.getCountCustomer();
        int pid = db.getCountParcel();
        Customer sender = new Customer(cid, senderFirstName.getText()+" "+senderLastName.getText(), senderPhoneNumber.getText(), senderZip.getText(), senderAddress.getText(), senderEmail.getText());
        Customer recipient = new Customer(cid+1, recipientFirstName.getText()+" "+recipientLastName.getText(), recipientPhoneNumber.getText(), recipientZip.getText(), recipientAddress.getText(), recipientEmail.getText());
        Parcel parcel = new Parcel(pid, Integer.parseInt(newParcelWeight.getText()), transportComboBox.getSelectionModel().getSelectedIndex(), newParceTitle.getText(), newParcelNote.getText(), (codCheckBox.selectedProperty().get())?Integer.parseInt(parcelCOD.getText()):null);
        db.registerNewParcel(sender, recipient, parcel);
    }

}
