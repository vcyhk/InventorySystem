package common;

import dbUtil.dbConnection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import loginapp.LoginController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PageController {

    private double xOffset = 0;
    private double yOffset = 0;

    public static String staff = LoginController.nameS;

    public void setStaff(String staff, Label staffName, Label staffID) throws SQLException {
        Statement stmt = null;
        String sql = "Select FName, LName, SID From Staff Where Account = '"+ staff +"'";
        String fname = null, lname = null, sid = null;
        Connection conn = dbConnection.getConntection();
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while(rs.next()){
            fname = rs.getString("FName");
            lname = rs.getString("LName");
            sid = rs.getString("SID");

        }rs.close();
        conn.close();
        staffName.setText(fname+" "+lname);
        staffID.setText(sid);
    }

    @FXML
    public void logOut(Button exitButton) throws Exception{
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        switchLogin();
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

    public void switchCustomer(ActionEvent event) throws IOException {
        Parent view1 = FXMLLoader.load(getClass().getResource("/customer/customer.fxml"));
        Scene scene1 = new Scene(view1);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene1);
        window.show();
    }

    public void switchPayment(ActionEvent event) throws IOException {
        Parent view3 = FXMLLoader.load(getClass().getResource("/payment/payment.fxml"));
        Scene scene3 = new Scene(view3);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene3);
        window.show();
    }

    public void switchTransaction(ActionEvent event) throws IOException {
        Parent view4 = FXMLLoader.load(getClass().getResource("/transaction/transaction.fxml"));
        Scene scene4 = new Scene(view4);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene4);
        window.show();
    }

    public void switchProduct(ActionEvent event) throws IOException {
        Parent view4 = FXMLLoader.load(getClass().getResource("/product/product.fxml"));
        Scene scene4 = new Scene(view4);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene4);
        window.show();
    }

    public void switchDashboard(ActionEvent event) throws IOException {
        Parent view5 = FXMLLoader.load(getClass().getResource("/dashboard/dashboard.fxml"));
        Scene scene5 = new Scene(view5);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene5);
        window.show();
    }

    public void switchSetting(ActionEvent event) throws IOException {
        Parent view5 = FXMLLoader.load(getClass().getResource("/setting/setting.fxml"));
        Scene scene5 = new Scene(view5);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene5);
        window.show();
    }


    public void switchPromotion(ActionEvent event) throws IOException {
        Parent view6 = FXMLLoader.load(getClass().getResource("/promotion/promotion.fxml"));
        Scene scene6 = new Scene(view6);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene6);
        window.show();
    }


}
