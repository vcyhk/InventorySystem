package payment.paymentEdit;

import common.AlertBox;
import common.dataEdit.DataEditController;
import customer.CustomerController;
import customer.CustomerData;
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
import payment.PaymentData;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class EditController  implements Initializable, DataEditController {

    private dbConnection dc;
    Connection conn;

    @FXML
    private TextField tidEdit, amountEdit;
    @FXML
    private ComboBox methodEdit;
    @FXML
    private DatePicker dateEdit;
    @FXML
    private VBox editScene;
    @FXML
    private Button editButton;
    private Boolean isInEditMode = Boolean.FALSE;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
        loopPaymentCombo();
    }

    public void inflateUI(PaymentData selectedForEdit) {
        tidEdit.setText(selectedForEdit.getTid());
        amountEdit.setText(selectedForEdit.getPaymentAmount());
        methodEdit.setValue(selectedForEdit.getPaymentMethod());
        dateEdit.setValue(LocalDate.parse(selectedForEdit.getTransactionDate()));
        tidEdit.setEditable(false);
        isInEditMode = Boolean.TRUE;
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

    public void updateInfo() throws Exception {
        String sqlInsert = "Update Payment set TransactionID = ?, Method = ?, Amount = ?, TransactionDate = ? where TransactionID = ?";
        try {
            conn = dbConnection.getConntection();
            PreparedStatement stmt = conn.prepareStatement(sqlInsert);

            stmt.setString(1, this.tidEdit.getText());
            stmt.setString(2, String.valueOf(this.methodEdit.getValue()));
            stmt.setString(3, this.amountEdit.getText());
            stmt.setString(4, String.valueOf(this.dateEdit.getValue()));
            stmt.setString(5, this.tidEdit.getText());

            stmt.execute();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeWindow();

    }

    public void closeWindow() throws Exception {
        PaymentController paymentController = PaymentController.getInstance();
        paymentController.loadData();
        Stage stage = (Stage) editButton.getScene().getWindow();
        stage.close();
    }

    //loop the combobox of payment
    public void loopPaymentCombo(){
        try{
            String sql = "SELECT PaymentMethod.name FROM PaymentMethod";
            conn = dbConnection.getConntection();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String paymentName = rs.getString(1);
                methodEdit.getItems().add(paymentName);
            }
            conn.close();
            rs.close();
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }
    }

    public boolean checkfield() {
        return !tidEdit.getText().equals("") && !tidEdit.getText().equals("") &&
                !amountEdit.getText().equals("") && !amountEdit.getText().equals("") &&
                !dateEdit.getValue().equals("")&& !(dateEdit.getValue() == null) &&
                !(methodEdit.getSelectionModel().getSelectedItem() == null ||methodEdit.getSelectionModel().getSelectedItem() == "");
    }
}
