package customer.customerImport;

import common.AlertBox;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class WindowController {
    public static String action=null;
    @FXML
    private Label cidLabel, totalExist;
    @FXML
    private VBox importSelScene;
    @FXML
    private Button overWriteBtn, overWriteAllBtn, skipDataBtn, skipAllBtn, keepNewBtn;

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

    public void keepNew(ActionEvent Event) throws Exception {
        action="keepNew";
        Stage stage = (Stage) keepNewBtn.getScene().getWindow();
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
