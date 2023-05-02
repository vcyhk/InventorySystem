package loginapp;

import com.sun.javafx.css.StyleCache;
import customer.CustomerController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    LoginModel loginModel = new LoginModel();
    public static String nameS;

    @FXML
    private Label dbStatus, loginMessage;
    @FXML
    private TextField nameInput;
    @FXML
    private PasswordField passInput;
    @FXML
    private Button loginButton, closeButton;
    @FXML
    private ImageView userloginIcon, passloginIcon;
    @FXML
    private HBox loginBackground;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        File file = new File("src/resources/userlogin_icon.png");
        Image image = new Image(file.toURI().toString());
        userloginIcon.setImage(image);

        File file1 = new File("src/resources/passlogin_icon.png");
        Image image1 = new Image(file1.toURI().toString());
        passloginIcon.setImage(image1);
        hotKey();
        combinationHotKey();
    }


    //check the username and password whether correct
    public void Login(ActionEvent event){
        loginAction();
    }

    public void loginAction(){
        try{
            if(loginModel.isLogin(this.nameInput.getText(), this.passInput.getText())){
                Stage stage = (Stage)this.loginButton.getScene().getWindow();
                stage.close();
                nameS = this.nameInput.getText();
                switchCustomer();
            }else{
                this.loginMessage.setText("Please check your entries and try again.");
            }
        }catch (Exception localException){
        }
    }

    public void handleCloseButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    //switch to customer scene
    public void switchCustomer(){
        try{
            Stage userStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Pane root = (Pane)loader.load(getClass().getResource("/customer/customer.fxml").openStream());
            CustomerController customerController = (CustomerController)loader.getController();
            Scene scene = new Scene(root);
            userStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
            userStage.setScene(scene);
            userStage.setTitle("Inventory System");
            userStage.setResizable(false);
            userStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void exitSystem() throws SQLException {
        loginModel.exitDatabase();
        System.exit(0);
    }

    public void hotKey() {
        loginBackground.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                try {
                    if (event.getCode() == KeyCode.ENTER) {
                        loginAction();
                    }
                    if (event.getCode() == KeyCode.ESCAPE) {
                        //exitSystem();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void combinationHotKey(){
        loginBackground.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(KeyCode.F4,
                    KeyCombination.CONTROL_DOWN);

            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    //handleCloseButtonAction();
                }
            }
        });
    }


}
