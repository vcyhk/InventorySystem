package payment.paymentAdd;

import common.AlertBox;
import common.dataAdd.DataAddController;
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
import payment.PaymentController;
import product.ProductController;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddController implements Initializable, DataAddController {

    private dbConnection dc;
    Connection conn;
    @FXML
    private TextField tidAdd, amountAdd;
    @FXML
    private ComboBox methodAdd;
    @FXML
    private DatePicker dateAdd;
    @FXML
    private VBox editScene;
    @FXML
    private Button createButton;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
        loopPaymentCombo();
    }

    public void confirmAdd(ActionEvent Event) throws Exception {
        String message = "Is it insert payment information?";
        String header = "Confirm create payment information";
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
        String sqlInsert = "INSERT INTO Payment(TransactionID,  Method, Amount, TransactionDate) VALUES ( ?, ?, ?, ?)";
        try{
            conn = dbConnection.getConntection();
            PreparedStatement ps = conn.prepareStatement(sqlInsert);

            ps.setString(1, this.tidAdd.getText());
            ps.setString(2, (String) this.methodAdd.getValue());
            ps.setString(3, this.amountAdd.getText());
            ps.setString(4, String.valueOf(this.dateAdd.getValue()));

            ps.execute();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeWindow();
    }

    //loop the combobox of payment
    public void loopPaymentCombo(){
        try{
            String sql = "SELECT PaymentMethod.name FROM PaymentMethod";
            conn = dbConnection.getConntection();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String paymentName = rs.getString(1);
                methodAdd.getItems().add(paymentName);
            }
            conn.close();
            rs.close();
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }
    }


    public void closeWindow() throws Exception {
        PaymentController paymentController = PaymentController.getInstance();
        paymentController.loadData();
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }

    public boolean checkfield() {
        return !tidAdd.getText().equals("") && !tidAdd.getText().equals("") &&
                !amountAdd.getText().equals("") && !amountAdd.getText().equals("") &&
                !dateAdd.getValue().equals("")&& !(dateAdd.getValue() == null) &&
                !(methodAdd.getSelectionModel().getSelectedItem() == null ||methodAdd.getSelectionModel().getSelectedItem() == "");
    }
}
