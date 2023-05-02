package setting.settingEditPaymentMethod;

import common.AlertBox;
import customer.CustomerData;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import product.ProductController;
import product.ProductData;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class EditPaymentMethod implements Initializable {
    private dbConnection dc;
    Connection conn;
    @FXML
    private TextField paymentMethod;
    @FXML
    private Button submitBtn;
    @FXML
    private VBox setScene;
    @FXML
    private TableView<EditPaymentData> paymentTable;
    @FXML
    private TableColumn<EditPaymentData, String> col_paymentName;
    private ObservableList<EditPaymentData> data, delItem, addItem,otherItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.delItem = FXCollections.observableArrayList();
        this.addItem = FXCollections.observableArrayList();
        this.otherItem = FXCollections.observableArrayList();
        try {
            loadData();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        hotKey();
    }

    public void addPayment() throws Exception {
        conn = dbConnection.getConntection();
        for(EditPaymentData items : addItem){
            try {
                String sqlInsert = "INSERT INTO PaymentMethod(Name) VALUES (?)";
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);
                ps.setString(1, items.getPayment());
                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for(EditPaymentData item : data){
            for(EditPaymentData a : delItem){
                if(item.getPayment().equals(a.getPayment())){
                    //delItem.remove(a);
                    otherItem.add(a);
                    break;
                }
            }
        }
        for(EditPaymentData items : delItem){
            try {
                Statement stmt = null;
                String sqlD = "Delete from PaymentMethod Where Name = '" + items.getPayment()+"'";
                conn = dbConnection.getConntection();
                stmt = conn.createStatement();
                stmt.executeUpdate(sqlD);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        loadData();
        for(EditPaymentData a : otherItem){
            try {
                String sqlInsert = "INSERT INTO PaymentMethod(Name) VALUES (?)";
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);
                ps.setString(1, a.getPayment());
                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        closeWindow();

    }

    public void closeWindow() throws Exception{
        ProductController productController = ProductController.getInstance();
        Stage stage = (Stage) submitBtn.getScene().getWindow();
        stage.close();
    }

    public void confirmPayment(ActionEvent Event) throws Exception {
        addPayment();
    }

    public void loadData() throws SQLException {
        this.data = FXCollections.observableArrayList();
        conn = dbConnection.getConntection();
        String sql = "SELECT PaymentMethod.* FROM PaymentMethod";

        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            this.data.add(new EditPaymentData(rs.getString(1)));
        }
        conn.close();
        setTableData();
    }

    public void setDelItem(){
        EditPaymentData selectedForEdit = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            return;
        }
        delItem.add(selectedForEdit);
        for(EditPaymentData x : data){
            if(x.getPayment().equals(selectedForEdit.getPayment())){
                data.remove(x);
                break;
            }
        }
        setTableData();
    }

    public void addItem(){
        if(paymentMethod.getText()==null||paymentMethod.getText().equals("") ){
            AlertBox.informationBox("Doesn't fill the field");
        } else {
            boolean has = false;
            for(EditPaymentData x : data){
                if(this.paymentMethod.getText().equals(x.getPayment())){
                    has = true;
                }
            }
            if(!has){
                addItem.add(new EditPaymentData(this.paymentMethod.getText()));
                data.add(new EditPaymentData(this.paymentMethod.getText()));
            }else
                AlertBox.informationBox("Already exsit!");
        }
        setTableData();
        paymentMethod.clear();
    }

    public void setTableData(){
        this.col_paymentName.setCellValueFactory(new PropertyValueFactory<EditPaymentData, String>("payment"));
        this.paymentTable.setItems(null);
        this.paymentTable.setItems(this.data);
    }

    public void hotKey(){
        setScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                try {
                    if (event.getCode() == KeyCode.ENTER) {
                        addPayment();
                    }
                    if(event.getCode() == KeyCode.ESCAPE){
                        //closeWindow();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
