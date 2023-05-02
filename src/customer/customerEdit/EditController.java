package customer.customerEdit;

import common.AlertBox;
import common.dataEdit.DataEditController;
import customer.CustomerController;
import customer.CustomerData;
import customer.customerEdit.customerTrans.TransCusController;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditController implements Initializable, DataEditController {
    public static String getcidT,getcNameT;
    private dbConnection dc;
    Connection conn;
    private Boolean isInEditMode = Boolean.FALSE;
    private int editTime = 0;
    @FXML
    private TextField cidEdit, lnameEdit, fnameEdit, phoneEdit, emailEdit, addressEdit, address2Edit,address3Edit,districtEdit;
    @FXML
    private ComboBox genderEdit, agegpEdit,countryEdit;
    @FXML
    private Button editButton;
    @FXML
    private VBox editScene;

    private static EditController instance = null;

    public EditController(){
        instance = this;
    }

    public static EditController getInstance(){
        return instance;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
        loopCombo();
        notEdit();
    }

    public void confirmEdit(ActionEvent Event) throws Exception {
        String message = "Are you sure you want to edit?";
        String header = "Edit the customer information";
        if(checkfield()){
            if(AlertBox.confirmationBox(message, header))
                updateInfo();
            else
                return;
        }else{
            AlertBox.informationBox("Doesn't fill all field");
        }
    }

    public void inflateUI(CustomerData selectedForEdit) {
        cidEdit.setText(selectedForEdit.getCid());
        lnameEdit.setText(selectedForEdit.getLname());
        fnameEdit.setText(selectedForEdit.getFname());
        emailEdit.setText(selectedForEdit.getEmail());
        phoneEdit.setText(selectedForEdit.getPhone());
        agegpEdit.setValue(selectedForEdit.getAgegroup());
        genderEdit.setValue(selectedForEdit.getGender());
        countryEdit.setValue(selectedForEdit.getCountry());
        districtEdit.setText(selectedForEdit.getDistrict());
        isInEditMode = Boolean.TRUE;

        try{
            String sql = "SELECT Customer.Address1, Customer.Address2, Customer.Address3 FROM Customer Where CID =" + selectedForEdit.getCid() ;
            conn = dbConnection.getConntection();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                addressEdit.setText(rs.getString(1));
                address2Edit.setText(rs.getString(2));
                address3Edit.setText(rs.getString(3));
            }
            conn.close();

        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }
    }
    public void notEdit() {
            cidEdit.setDisable(true);
            lnameEdit.setDisable(true);
            fnameEdit.setDisable(true);
            emailEdit.setDisable(true);
            addressEdit.setDisable(true);
            phoneEdit.setDisable(true);
            agegpEdit.setDisable(true);
            genderEdit.setDisable(true);
            countryEdit.setDisable(true);
            districtEdit.setDisable(true);
            address2Edit.setDisable(true);
            address3Edit.setDisable(true);


    }


    public void canEdit() {
        editTime+=1;
        System.out.println(editTime);
        System.out.println((editTime/2) != 0);
        if(editTime%(2) != 0){
            cidEdit.setEditable(false);
            lnameEdit.setDisable(false);
            fnameEdit.setDisable(false);
            emailEdit.setDisable(false);
            addressEdit.setDisable(false);
            phoneEdit.setDisable(false);
            agegpEdit.setDisable(false);
            genderEdit.setDisable(false);
            countryEdit.setDisable(false);
            districtEdit.setDisable(false);
            address2Edit.setDisable(false);
            address3Edit.setDisable(false);
        }else{
            notEdit();
        }

    }


    public void updateInfo() throws Exception {
        String sqlInsert = "Update customer set FName = ?, LName = ?, Gender = ?, AgeGroup = ?, Country = ?, Address1 = ?, Address2 = ?, Address3 = ?, District = ?, Email = ?, Phone = ? where CID = ?";
        try{
            conn = dbConnection.getConntection();
            PreparedStatement stmt = conn.prepareStatement(sqlInsert);

            stmt.setString(1, this.fnameEdit.getText());
            stmt.setString(2, this.lnameEdit.getText());
            stmt.setString(3, (String) this.genderEdit.getValue());
            stmt.setString(4, String.valueOf(this.agegpEdit.getValue()));
            stmt.setString(5, (String) this.countryEdit.getValue());
            stmt.setString(6, this.addressEdit.getText());
            stmt.setString(7, this.address2Edit.getText());
            stmt.setString(8, this.address3Edit.getText());
            stmt.setString(9, this.districtEdit.getText());
            stmt.setString(10, this.emailEdit.getText());
            stmt.setString(11, this.phoneEdit.getText());
            stmt.setString(12,this.cidEdit.getText());

            stmt.execute();
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
                countryEdit.getItems().add(countryName);
            }
            conn.close();

        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }

    }

    public void closeWindow() throws Exception{
        CustomerController customerController = CustomerController.getInstance();
        customerController.loadData();
        Stage stage = (Stage) editButton.getScene().getWindow();
        stage.close();
    }

    public void switchTransCus() throws IOException {
        getcidT = cidEdit.getText();
        getcNameT = fnameEdit.getText()+" "+lnameEdit.getText();
        Stage transStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/customer/customerEdit/customerTrans/TransCus.fxml").openStream());
        customer.customerEdit.customerTrans.TransCusController transController = (customer.customerEdit.customerTrans.TransCusController)loader.getController();
        Scene scene = new Scene(root);
        transStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        transStage.setScene(scene);
        transStage.setTitle("Inventory System");
        transStage.setResizable(false);
        transStage.initModality(Modality.APPLICATION_MODAL);
        transStage.show();
    }

    public boolean checkfield() {
        return !cidEdit.getText().equals("") && !lnameEdit.getText().equals("") && !fnameEdit.getText().equals("") && !phoneEdit.getText().equals("") && !emailEdit.getText().equals("")&&
                !addressEdit.getText().equals("")&&!districtEdit.getText().equals("")&&
                !(genderEdit.getSelectionModel().getSelectedItem() == null ||genderEdit.getSelectionModel().getSelectedItem() == "")
                && !(agegpEdit.getSelectionModel().getSelectedItem() == null ||agegpEdit.getSelectionModel().getSelectedItem() == "")
                && !(countryEdit.getSelectionModel().getSelectedItem() == null ||countryEdit.getSelectionModel().getSelectedItem() == "");
    }
}
