package payment.paymentImport;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class WindowController {
    public static String action=null;
    @FXML
    private Label cidLabel, totalExist;
    @FXML
    private Button overWriteBtn, overWriteAllBtn, skipDataBtn, skipAllBtn;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.action = null;
    }

    public void overWrite(ActionEvent Event) throws Exception {
        action="overWrite";
        Stage stage = (Stage) overWriteBtn.getScene().getWindow();
        stage.close();
    }

    public void overWriteAll(ActionEvent Event) throws Exception {
        action="overWriteAll";
        Stage stage = (Stage) overWriteAllBtn.getScene().getWindow();
        stage.close();
    }

    public void skipData(ActionEvent Event) throws Exception {
        action="skipData";
        Stage stage = (Stage) skipDataBtn.getScene().getWindow();
        stage.close();
    }

    public void skipAll(ActionEvent Event) throws Exception {
        action="skipAll";
        Stage stage = (Stage) skipAllBtn.getScene().getWindow();
        stage.close();
    }

    public static String getData() {
        return action;
    }
    public static void setData(String a) {
        action = a;
    }
    public void setCid(String a) {
        cidLabel.setText(a);
    }
    public void totalExist(String a) {
        totalExist.setText(String.valueOf(a));
    }

}
