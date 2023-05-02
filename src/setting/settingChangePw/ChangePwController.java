package setting.settingChangePw;

import common.AlertBox;
import common.PageController;
import dbUtil.dbConnection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import loginapp.LoginController;
import promotion.PromotionController;
import setting.SettingController;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ChangePwController extends PageController implements Initializable {
    private dbConnection dc;
    Connection conn;
    @FXML
    private TextField currentPass, newPass, confirmPass;
    @FXML
    private Button changePwButton;
    @FXML
    private Label informationBox;
    @FXML
    private VBox setScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        hotKey();
    }

    public void confirmChange(ActionEvent Event) throws Exception {
        changeNow();
    }

    public void closeWindow() throws Exception {
        SettingController promotionController = SettingController.getInstance();
        Stage stage = (Stage) changePwButton.getScene().getWindow();
        stage.close();
    }

    public void clearPW(){
        currentPass.clear();
        confirmPass.clear();
        newPass.clear();
    }
    public void changeNow() throws Exception {
        if(newPass.getText()==null||newPass.getText().equals("") || confirmPass.getText()==null||confirmPass.getText().equals("")){
            informationBox.setText("You did not fill all field");
            clearPW();
        }else {
            String checkCurrentPW = "Select Password From Staff Where Account = '" + LoginController.nameS + "'";

            String password = "";
            try {
                conn = dbConnection.getConntection();
                ResultSet rs = conn.createStatement().executeQuery(checkCurrentPW);
                while (rs.next()) {
                    password = rs.getString(1);
                }
                rs.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (password.equals(currentPass.getText())) {
                if (!confirmPass.getText().equals(password)) {
                    if (newPass.getText().equals(confirmPass.getText())) {
                        String sqlUpdate = "Update Staff set Password = ? where Account = ?";
                        try {
                            Connection conn = dbConnection.getConntection();
                            PreparedStatement stmt = conn.prepareStatement(sqlUpdate);

                            stmt.setString(1, this.confirmPass.getText());
                            stmt.setString(2, LoginController.nameS);

                            stmt.execute();
                            conn.close();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        closeWindow();
                    } else {
                        informationBox.setText("Those passwords didn't match. Please try again.");
                        clearPW();
                    }
                } else {
                    informationBox.setText("You used this password recently. Please enter a new password.");
                    clearPW();
                }
            } else {
                informationBox.setText("The current password didn't correct.");
                clearPW();
            }
        }
    }

    public void hotKey(){
        setScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                try {
                    if (event.getCode() == KeyCode.ENTER) {
                        changeNow();
                    }
                    if(event.getCode() == KeyCode.ESCAPE){
                        closeWindow();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
