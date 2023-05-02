package setting.settingEditLocation;

import common.AlertBox;
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
import setting.settingEditLocation.EditLocationData;
import setting.settingEditLocation.EditLocationData;
import setting.settingEditPaymentMethod.EditPaymentData;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class EditLocation implements Initializable {
    private dbConnection dc;
    Connection conn;
    @FXML
    private TextField location;
    @FXML
    private Button submitBtn;
    @FXML
    private VBox setScene;
    @FXML
    private TableView<EditLocationData> locationTable;
    @FXML
    private TableColumn<EditLocationData, String> col_locationName;
    private ObservableList<EditLocationData> data, delItem, addItem,otherItem;

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

    public void addLocation() throws Exception {
        conn = dbConnection.getConntection();
        for(EditLocationData items : addItem){
            try {
                String sqlInsert = "INSERT INTO Location(Name) VALUES (?)";
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);
                ps.setString(1, items.getLocation());
                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for(EditLocationData item : data){
            for(EditLocationData a : delItem){
                if(item.getLocation().equals(a.getLocation())){
                    //delItem.remove(a);
                    otherItem.add(a);
                    break;
                }
            }
        }
        for(EditLocationData items : delItem){
            try {
                Statement stmt = null;
                String sqlD = "Delete from Location Where Name = '" + items.getLocation()+"'";
                conn = dbConnection.getConntection();
                stmt = conn.createStatement();
                stmt.executeUpdate(sqlD);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        loadData();
        for(EditLocationData a : otherItem){
            try {
                String sqlInsert = "INSERT INTO Location(Name) VALUES (?)";
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);
                ps.setString(1, a.getLocation());
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

    public void confirmLocation(ActionEvent Event) throws Exception {
        addLocation();
    }
    public void loadData() throws SQLException {
        this.data = FXCollections.observableArrayList();
        conn = dbConnection.getConntection();
        String sql = "SELECT Location.* FROM Location";

        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            this.data.add(new EditLocationData(rs.getString(1)));
        }
        conn.close();
        setTableData();
    }


    public void setDelItem(){
        EditLocationData selectedForEdit = locationTable.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            return;
        }
        delItem.add(selectedForEdit);
        for(EditLocationData x : data){
            if(x.getLocation().equals(selectedForEdit.getLocation())){
                data.remove(x);
                break;
            }
        }
        setTableData();
    }


    public void addItem(){
        if(location.getText()==null||location.getText().equals("") ){
            AlertBox.informationBox("Doesn't fill the field");
        } else {
            boolean has = false;
            for(EditLocationData x : data){
                if(this.location.getText().equals(x.getLocation())){
                    has = true;
                }
            }
            if(!has){
                addItem.add(new EditLocationData(this.location.getText()));
                data.add(new EditLocationData(this.location.getText()));
            }else
                AlertBox.informationBox("Already exsit!");
        }
        setTableData();
        location.clear();
    }

    public void setTableData(){
        this.col_locationName.setCellValueFactory(new PropertyValueFactory<EditLocationData, String>("location"));
        this.locationTable.setItems(null);
        this.locationTable.setItems(this.data);
    }

    public void hotKey(){
        setScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                try {
                    if (event.getCode() == KeyCode.ENTER) {
                        addLocation();
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
