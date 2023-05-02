package setting;

import common.PageController;
import common.PageGuide;
import common.PageModel;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import loginapp.LoginController;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import promotion.PromotionController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class SettingController extends PageController implements Initializable {
    PageModel settingModel = new PageModel();
    @FXML
    private Label staffName, staffID, dbStatus;
    @FXML
    private ImageView staffIcon;
    @FXML
    private Button settingButton, exitButton, editLocationBtn;
    @FXML
    private BorderPane basicScene;

    private double xOffset = 0;
    private double yOffset = 0;
    private static SettingController instance = null;

    public SettingController(){
        instance = this;
    }

    public static SettingController getInstance(){
        return instance;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/resources/stafficon/"+staff+".png");
        Image image = new Image(file.toURI().toString());
        staffIcon.setImage(image);


        if(settingModel.isDatabaseConnected()){
            dbStatus.setText("Connected");
            dbStatus.setTextFill(Color.GREEN);
        }else{
            dbStatus.setText("Not Connected");
            dbStatus.setTextFill(Color.RED);
        }

        settingButton.setDisable(true);


        try {
            setStaff(staff, staffName, staffID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logOut(ActionEvent event) throws Exception{
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        switchLogin();
    }

    public void editLocation(ActionEvent event) throws IOException {
        Stage addStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane) loader.load(getClass().getResource("/setting/settingEditLocation/editLocation.fxml").openStream());
        Scene scene = new Scene(root);
        addStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        addStage.setScene(scene);
        addStage.setTitle("Inventory System");
        addStage.setResizable(false);
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.show();
    }

    public void editPaymentMethod(ActionEvent event) throws IOException {
        Stage addStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane) loader.load(getClass().getResource("/setting/settingEditPaymentMethod/editPaymentMethod.fxml").openStream());
        Scene scene = new Scene(root);
        addStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        addStage.setScene(scene);
        addStage.setTitle("Inventory System");
        addStage.setResizable(false);
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.show();
    }

    public void changeProPic(ActionEvent event) throws IOException {
        Stage addStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane) loader.load(getClass().getResource("/setting/settingChangeProPic/changeProPic.fxml").openStream());
        Scene scene = new Scene(root);
        addStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        addStage.setScene(scene);
        addStage.setTitle("Inventory System");
        addStage.setResizable(false);
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.show();
    }

    public void changePw(ActionEvent event) throws IOException {
        Stage addStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane) loader.load(getClass().getResource("/setting/settingChangePw/changePw.fxml").openStream());
        Scene scene = new Scene(root);
        addStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        addStage.setScene(scene);
        addStage.setTitle("Inventory System");
        addStage.setResizable(false);
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.show();
    }

    public void aboutCR(ActionEvent event) throws IOException {
        Stage addStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane) loader.load(getClass().getResource("/setting/settingAbout/about.fxml").openStream());
        Scene scene = new Scene(root);
        addStage.initStyle(StageStyle.UNDECORATED);
        addStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        addStage.setScene(scene);
        addStage.setTitle("Inventory System");
        addStage.setResizable(false);
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.show();
    }

    //switch the scene to the Login Page
    public void switchLogin(){
        try{
            Stage userStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            Pane root = (Pane)loader.load(getClass().getResource("/loginapp/login.fxml").openStream());
            LoginController loginController = (LoginController)loader.getController();

            Scene scene = new Scene(root);
            userStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
            userStage.initStyle(StageStyle.UNDECORATED);

            root.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    xOffset = event.getSceneX();
                    yOffset = event.getSceneY();
                }
            });
            root.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    userStage.setX(event.getScreenX() - xOffset);
                    userStage.setY(event.getScreenY() - yOffset);
                }
            });

            userStage.setScene(scene);
            userStage.setTitle("Inventory System");
            userStage.setResizable(false);
            userStage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadNewIcon(){
        File file = new File("src/resources/stafficon/"+staff+".png");
        Image image = new Image(file.toURI().toString());
        staffIcon.setImage(image);
    }



}