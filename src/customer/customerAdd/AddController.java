package customer.customerAdd;

import common.AlertBox;
import common.dataAdd.DataAddController;
import customer.CustomerController;
import dbUtil.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AddController implements Initializable, DataAddController {

    private dbConnection dc;
    Connection conn;
    @FXML
    private TextField lnameAdd, fnameAdd, phoneAdd, emailAdd, addressAdd, address2Add,address3Add,districtAdd, cidAdd;
    @FXML
    private ComboBox genderAdd, agegpAdd,countryAdd;
    @FXML
    private Button createButton;
    @FXML
    private VBox addScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
        loopCombo();
        try {
            cidAdd.setText(String.valueOf(cidMax()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cidAdd.setEditable(false);
    }

    public void confirmAdd(ActionEvent Event) throws Exception {
        String message = "Is it insert customer information?";
        String header = "Confirm create customer information";
        if (checkfield()){
            if (AlertBox.confirmationBox(message, header)) {
                createData();
            } else {
                return;
            }
        }
        else
            AlertBox.informationBox("Doesn't fill all field");
    }

    public void createData() throws Exception {
        String sqlInsert = "INSERT INTO customer(CID,  FName, LName, Gender, AgeGroup, Country, Address1, Address2, Address3, District, Email, Phone) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            conn = dbConnection.getConntection();
            PreparedStatement ps = conn.prepareStatement(sqlInsert);

            ps.setString(1, this.cidAdd.getText());
            ps.setString(2, this.fnameAdd.getText());
            ps.setString(3, this.lnameAdd.getText());
            ps.setString(4, (String) this.genderAdd.getValue());
            ps.setString(5, (String)this.agegpAdd.getValue());
            ps.setString(6, (String) this.countryAdd.getValue());
            ps.setString(7, this.addressAdd.getText());
            ps.setString(8, this.address2Add.getText());
            ps.setString(9, this.address3Add.getText());
            ps.setString(10, this.districtAdd.getText());
            ps.setString(11, this.emailAdd.getText());
            ps.setString(12, this.phoneAdd.getText());

            ps.execute();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeWindow();
    }
    public void loopCombo(){
        try{
            String sql = "SELECT Country.name FROM Country";
            conn = dbConnection.getConntection();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String countryName = rs.getString(1);
                countryAdd.getItems().add(countryName);
            }
            conn.close();
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }

    }

    public void closeWindow() throws Exception{
        CustomerController customerController = CustomerController.getInstance();
        customerController.loadData();
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }

    //error check
    //check whether all field has enter
    public boolean checkfield() {
        return !lnameAdd.getText().equals("") && !fnameAdd.getText().equals("") && !phoneAdd.getText().equals("") && !addressAdd.getText().equals("") && !districtAdd.getText().equals("")&&
                !(genderAdd.getSelectionModel().getSelectedItem() == null ||genderAdd.getSelectionModel().getSelectedItem() == "")
                && !(agegpAdd.getSelectionModel().getSelectedItem() == null ||agegpAdd.getSelectionModel().getSelectedItem() == "")
                && !(countryAdd.getSelectionModel().getSelectedItem() == null ||countryAdd.getSelectionModel().getSelectedItem() == "");
    }

    public int cidMax() throws SQLException {
        String sql1 = "SELECT MAX(Customer.CID) FROM Customer";
        conn = dbConnection.getConntection();
        String maxCID = null;
        int max = 0;
        ResultSet rs1 = conn.createStatement().executeQuery(sql1);
        while (rs1.next()){
            maxCID = rs1.getString(1);
        }
        if(maxCID!=null){
            max = Integer.parseInt(maxCID)+1;
        }else{
            max =1;
        }
        return max;
    }

}
