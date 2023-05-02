package product.productEdit;

import common.AlertBox;
import common.dataEdit.DataEditController;
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
import org.apache.commons.math3.stat.descriptive.summary.Product;
import payment.PaymentData;
import product.ProductController;
import product.ProductData;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class EditController  implements Initializable, DataEditController {

    private dbConnection dc;
    private Boolean isInEditMode = Boolean.FALSE;
    Connection conn;
    @FXML
    private VBox editScene;
    @FXML
    private TextField itemNumEdit, itemCatEdit, itemDesEdit, unitPriceEdit;
    @FXML
    private DatePicker effectFromEdit, effectToEdit;
    @FXML
    private Button editButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
    }
    public void confirmEdit(ActionEvent Event) throws Exception {
        String message = "Are you sure you want to edit?";
        String header = "Edit the customer information";
        if (checkfield()) {
            if (AlertBox.confirmationBox(message, header))
                if(dataVaild())
                    updateInfo();
                else
                    AlertBox.informationBox("Invalid Date");
            else
                return;
        } else {
            AlertBox.informationBox("Doesn't fill all field");
        }
    }

    public void inflateUI(ProductData selectedForEdit){
        itemNumEdit.setText(selectedForEdit.getItemID());
        itemCatEdit.setText(selectedForEdit.getCategroy());
        itemDesEdit.setText(selectedForEdit.getDesc());
        unitPriceEdit.setText(selectedForEdit.getUnitPrice());
        try{
            effectFromEdit.setValue(LocalDate.parse(selectedForEdit.getEffectiveFrom()));
            effectToEdit.setValue(LocalDate.parse(selectedForEdit.getEffectiveTo()));
        }catch(Exception e){
            effectFromEdit.setValue(null);
        }
        try{
            effectToEdit.setValue(LocalDate.parse(selectedForEdit.getEffectiveTo()));
        }catch(Exception e){
            effectToEdit.setValue(null);
        }
        itemNumEdit.setEditable(false);
        isInEditMode = Boolean.TRUE;
    }
    public void updateInfo() throws Exception {
        String sqlInsert = "Update product set ItemID = ?, Category = ?, Desc = ?, UnitPrice = ?, EffectiveFrom = ?, EffectiveTo = ? where ItemID = ?";
        try {
            conn = dbConnection.getConntection();
            PreparedStatement stmt = conn.prepareStatement(sqlInsert);

            stmt.setString(1, this.itemNumEdit.getText());
            stmt.setString(2, this.itemCatEdit.getText());
            stmt.setString(3, this.itemDesEdit.getText());
            stmt.setString(4, this.unitPriceEdit.getText());
            stmt.setString(5, String.valueOf(this.effectFromEdit.getValue()));
            stmt.setString(6, String.valueOf(this.effectToEdit.getValue()));
            stmt.setString(7, this.itemNumEdit.getText());

            stmt.execute();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeWindow();
    }

    public void closeWindow() throws Exception {
        ProductController productController = ProductController.getInstance();
        productController.loadData();
        Stage stage = (Stage) editButton.getScene().getWindow();
        stage.close();
    }

    //error check
    //check whether all field has enter
    public boolean checkfield() {
        return  !itemNumEdit.getText().equals("") && !itemCatEdit.getText().equals("") && !itemDesEdit.getText().equals("") && !unitPriceEdit.getText().equals("")&&
                !(effectFromEdit.getValue() == null) &&!(effectToEdit.getValue() == null);
    }

    public boolean dataVaild() throws ParseException {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdformat.parse(String.valueOf(effectFromEdit.getValue()));
        Date d2 = sdformat.parse(String.valueOf(effectToEdit.getValue()));
        if(d1.compareTo(d2) > 0){
            return false;
        }
        return true;
    }
}