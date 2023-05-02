package product.productAdd;

import common.AlertBox;
import common.dataAdd.DataAddController;
import customer.CustomerController;
import dbUtil.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import product.ProductController;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class AddController implements Initializable, DataAddController {

    Connection conn;
    private dbConnection dc;
    private String maxPID;
    private int maxPID1 =0;
    @FXML
    private Button createButton;
    @FXML
    private TextField itemNumAdd,itemCatAdd, itemDesAdd, unitPriceAdd;
    @FXML
    private DatePicker effectFromAdd, effectToAdd;
    @FXML
    private VBox addScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
        String sql = "SELECT MAX(Product.ItemID) FROM Product";
        try {
            conn = dbConnection.getConntection();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                maxPID = rs.getString(1);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(maxPID==null){
            maxPID1 =0;
        }else{
            maxPID1 = Integer.parseInt(maxPID)+1;
        }

        itemNumAdd.setText(String.valueOf(maxPID1));
        itemNumAdd.setEditable(false);
    }

    public void confirmAdd(ActionEvent Event) throws Exception {
        String message = "Is it insert product information?";
        String header = "Confirm create product information";
        if (checkfield()) {
            if (AlertBox.confirmationBox(message, header))
                if(dataVaild())
                    createData();
                else
                    AlertBox.informationBox("Invalid Date");
            else
                return;
        } else {
            AlertBox.informationBox("Doesn't fill all field");
        }
    }

    public void createData() throws Exception {
        String sqlInsert = "INSERT INTO product( ItemID, Category, Desc, UnitPrice, EffectiveFrom, EffectiveTo) VALUES ( ?, ?, ?, ?, ?, ?)";
        try {
            conn = dbConnection.getConntection();
            PreparedStatement ps = conn.prepareStatement(sqlInsert);

            ps.setString(1, this.itemNumAdd.getText());
            ps.setString(2, this.itemCatAdd.getText());
            ps.setString(3, this.itemDesAdd.getText());
            ps.setString(4, this.unitPriceAdd.getText());
            ps.setString(5, String.valueOf(this.effectFromAdd.getValue()));
            ps.setString(6, String.valueOf(this.effectToAdd.getValue()));

            ps.execute();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeWindow();
    }

    public void closeWindow() throws Exception{
        ProductController productController = ProductController.getInstance();
        productController.loadData();
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }

    //error check
    //check whether all field has enter
    public boolean checkfield() {
        return !itemNumAdd.getText().equals("") && !itemCatAdd.getText().equals("") && !itemDesAdd.getText().equals("") && !unitPriceAdd.getText().equals("") &&
                !(effectFromAdd.getValue() == null) &&!(effectToAdd.getValue() == null);
    }

    public boolean dataVaild() throws ParseException {
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdformat.parse(String.valueOf(effectFromAdd.getValue()));
        Date d2 = sdformat.parse(String.valueOf(effectToAdd.getValue()));
        if(d1.compareTo(d2) > 0){
            return false;
        }
        return true;
    }
}
