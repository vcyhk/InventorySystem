package promotion.promotionAdd;

import common.AlertBox;
import common.dataAdd.DataAddController;
import dbUtil.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import promotion.PromotionController;

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


    private dbConnection dc;
    private int maxPID =0;
    Connection conn;
    @FXML
    private Button createButton;
    @FXML
    private TextField itemNumAdd, discountAdd;
    @FXML
    private DatePicker effectFromAdd, effectToAdd;
    @FXML
    private VBox addScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
    }

    public void confirmAdd(ActionEvent Event) throws Exception {
        String message = "Is it insert product promotion information?";
        String header = "Confirm create product promotion information";
        if (checkfield()) {
            if (AlertBox.confirmationBox(message, header))
                if (dataVaild())
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
        if (isItemIDExist(itemNumAdd.getText())) {
            String sqlInsert = "INSERT INTO Promotion( ItemID, Discount, EffectiveFrom, EffectiveTo) VALUES ( ?, ?, ?, ?)";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, this.itemNumAdd.getText());
                ps.setString(2, this.discountAdd.getText());
                ps.setString(3, String.valueOf(this.effectFromAdd.getValue()));
                ps.setString(4, String.valueOf(this.effectToAdd.getValue()));

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeWindow();
        } else {
            AlertBox.informationBox("Doesn't exist this Item ID");
        }
    }

    public void closeWindow() throws Exception{
        PromotionController promotionController = PromotionController.getInstance();
        promotionController.loadData();
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }

    //error check
    //check whether all field has enter
    public boolean checkfield() {
        return !itemNumAdd.getText().equals("") && !discountAdd.getText().equals("") &&
                !(effectFromAdd.getValue() == null) &&!(effectToAdd.getValue() == null);

    }

    //check the cid whether exist
    public boolean isItemIDExist(String itemID) throws SQLException {
        conn = dbConnection.getConntection();
        String sql = "SELECT COUNT(*) AS RESULT FROM PRODUCT WHERE ItemID = " +  itemID + ";";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        if (Integer.parseInt(rs.getString(1)) > 0) {
            conn.close();
            rs.close();
            return true;
        }
        conn.close();
        rs.close();
        return false;
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
