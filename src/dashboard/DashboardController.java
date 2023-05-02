package dashboard;

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
import javafx.scene.chart.*;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import loginapp.LoginController;
import promotion.promotionImport.ImportData;
import transaction.TransactionData;
import common.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController extends PageController implements Initializable {
    PageModel dashboardModel = new PageModel();
    Connection conn;
    @FXML
    private Label staffName, staffID,dbStatus, totalCost,totalPoints, totalMember, newMember, sale;
    @FXML
    private ImageView staffIcon;
    @FXML
    private Button dashboardButton, exitButton;
    @FXML
    private TextField cidInput;
    @FXML
    private VBox chartView, cateChartView,tranChartView;
    @FXML
    private VBox dashcontent;
    @FXML
    private BorderPane basicScene;

    private double xOffset = 0;
    private double yOffset = 0;

    public ObservableList<PieChart.Data> pieData;
    public ObservableList<BarChart.Data> barData;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/resources/stafficon/"+staff+".png");
        Image image = new Image(file.toURI().toString());
        staffIcon.setImage(image);


        if(dashboardModel.isDatabaseConnected()){
            dbStatus.setText("Connected");
            dbStatus.setTextFill(Color.GREEN);
        }else{
            dbStatus.setText("Not Connected");
            dbStatus.setTextFill(Color.RED);
        }

        dashboardButton.setDisable(true);

        try {
            countTotalMember();
            newMember();
            getSale();
            getCategorySale();
            getTransactionSale();
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

    public void countTotalMember() throws SQLException {
        conn = dbConnection.getConntection();
        String result="";
        String sql = "SELECT COUNT(*) FROM CUSTOMER";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()){
            result = rs.getString(1);
        }
        conn.close();
        rs.close();
        totalMember.setText(result);
    }

    public void newMember() throws SQLException {
        conn = dbConnection.getConntection();
        String result = "";
        String sql = "SELECT MAX(CID) FROM CUSTOMER";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()){
            result = rs.getString(1);
        }
        rs.close();

        String cid = "", fname = "", lname = "";
        sql = "SELECT CID, FName, LName FROM CUSTOMER WHERE CID = "+result;
        ResultSet rs1 = conn.createStatement().executeQuery(sql);
        while(rs1.next()){
            cid = rs1.getString(1);
            fname = rs1.getString(2);
            lname = rs1.getString(3);
        }
        rs1.close();
        newMember.setText( "CID " + cid + " - " + fname + " " + lname);
    }

    public void getSale() throws SQLException {
        conn = dbConnection.getConntection();
        String result = "";
        String sql = "SELECT SUM(NetAmount) FROM TRANSACTIONS";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()){
            result = rs.getString(1);
        }
        rs.close();

        sale.setText(result);
    }

    public void getCategorySale(){
        try {
            conn = dbConnection.getConntection();

            int max = 0;
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) AS RESULT FROM TransactionDetail;");
            while(rs.next()){
                max = Integer.parseInt(rs.getString(1));
            }
            rs.close();


            String sql = "SELECT ItemID, NumberOfUnit FROM TransactionDetail";
            String [][]result = new String[max][4];
            ResultSet rs1 = conn.createStatement().executeQuery(sql);
            int i = 0;
            while(rs1.next()){
                result[i][0] = rs1.getString(1); //ItemID
                result[i][1] = rs1.getString(2); //Number of Unit
                i++;
            }
            rs1.close();

            //get the itemCategory
            for(int j=0 ; j<max;j++){
                String sql2 = "Select Product.Category From Product Where ItemID = " +result[j][0];
                ResultSet rs2 = conn.createStatement().executeQuery(sql2);
                while (rs2.next()) {
                    result[j][2]= rs2.getString(1); //Item Category
                }
                rs2.close();
            }


            //count category
            List <Pair<String, Integer>> categoryNo = new ArrayList();  //<Pair <CategoryName, Pair<No of Category, UnitPrice>>
            String sqlCate = "SELECT Product.Category FROM Product;";
            ResultSet rs4 = conn.createStatement().executeQuery(sqlCate);
            while (rs4.next()){
                if (categoryNo.size()<=0){
                    categoryNo.add(new Pair(rs4.getString(1),0));
                }else{
                    for(int j = 0 ; j<categoryNo.size();j++){
                        if(categoryNo.get(j).getFirst().equals(rs4.getString(1))){
                            continue;
                        }else{
                            categoryNo.add(new Pair(rs4.getString(1), 0));
                        }
                    }
                }
            }
            for(int j =0; j<max;j++){
                for(Pair<String, Integer> d : categoryNo){
                    if(result[j][2].equals(d.getFirst())){
                        int numberof = d.getSecond();
                        numberof = numberof + Integer.parseInt(result[j][1]);
                        d.setSecond(numberof);
                    }
                }
            }

            conn.close();
            cateChartView.getChildren().clear();
            //Create BarChart and assign data
            CategoryAxis xAxis    = new CategoryAxis();
            xAxis.setLabel("Category ");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("No. of sale");
            BarChart bChart = new BarChart(xAxis, yAxis);

            XYChart.Series dataSeries1 = new XYChart.Series();
            for(Pair<String, Integer> d : categoryNo){
                dataSeries1.getData().add(new BarChart.Data(d.getFirst(), d.getSecond()));
            }
            bChart.getData().add(dataSeries1);
            cateChartView.getChildren().add(bChart);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getTransactionSale(){
        try {
            conn = dbConnection.getConntection();

            int max = 0;
            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) AS RESULT FROM TransactionDetail;");
            while(rs.next()){
                max = Integer.parseInt(rs.getString(1));
            }
            rs.close();

            //result[][0]: ItemID, [1]:Number of Unit , [2]: Item Category, [3]: Item Price, [4]: Total Price (Number of Unit * Item Price)
            String sql = "SELECT ItemID, NumberOfUnit FROM TransactionDetail";
            String [][]result = new String[max][5];
            ResultSet rs1 = conn.createStatement().executeQuery(sql);
            int i = 0;
            while(rs1.next()){
                result[i][0] = rs1.getString(1); //ItemID
                result[i][1] = rs1.getString(2); //Number of Unit
                i++;
            }
            rs1.close();
            //get the itemCategory
            for(int j=0 ; j<max;j++){
                String sql2 = "Select Product.Category, Product.UnitPrice From Product Where ItemID = " +result[j][0];
                ResultSet rs2 = conn.createStatement().executeQuery(sql2);
                while (rs2.next()) {
                    result[j][2]= rs2.getString(1); //Item Category
                    result[j][3]= rs2.getString(2); //Item Price
                }
                rs2.close();
            }

            for(int k = 0; k< result.length;k++){
                result[k][4] = String.valueOf(Integer.parseInt(result[k][1])*Double.parseDouble(result[k][3]));
            }


            //count category
            List <Pair<String, Double>> categoryNo = new ArrayList();  //<Pair <CategoryName, Transaction Amount>>
            String sqlCate = "SELECT Product.Category FROM Product;";
            ResultSet rs4 = conn.createStatement().executeQuery(sqlCate);
            while (rs4.next()){
                if (categoryNo.size()<=0){
                    categoryNo.add(new Pair(rs4.getString(1), 0.0));
                }else{
                    for(int j = 0 ; j<categoryNo.size();j++){
                        if(categoryNo.get(j).getFirst().equals(rs4.getString(1))){
                            continue;
                        }else{
                            categoryNo.add(new Pair(rs4.getString(1), 0.0));
                        }
                    }
                }
            }
            for(int j =0; j<max;j++){
                for(Pair<String, Double> d : categoryNo){
                    if(result[j][2].equals(d.getFirst())){
                        double numberof = 0;
                        numberof = d.getSecond();
                        numberof += Double.parseDouble(result[j][4]);
                        d.setSecond(numberof);
                    }
                }
            }

            conn.close();
            tranChartView.getChildren().clear();
            //Create BarChart and assign data
            CategoryAxis xAxis = new CategoryAxis();
            xAxis.setLabel("Category ");
            NumberAxis yAxis = new NumberAxis();
            yAxis.setLabel("Amount. of sale");
            BarChart bChart = new BarChart(xAxis, yAxis);

            XYChart.Series dataSeries1 = new XYChart.Series();
            for(Pair<String, Double> d : categoryNo){
                dataSeries1.getData().add(new BarChart.Data(d.getFirst(), d.getSecond()));
            }
            bChart.getData().add(dataSeries1);
            tranChartView.getChildren().add(bChart);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    ////unuse
    public void searchData(ActionEvent event) throws SQLException {
        String sql1 = "Select MAX(CID) From Customer";
        String maxCid = null;
        Connection conn1 = dbConnection.getConntection();
        ResultSet rs1 = conn1.createStatement().executeQuery(sql1);

        while (rs1.next()) {
            maxCid = rs1.getString(1);
        }

        if(Integer.parseInt(cidInput.getText())<=Integer.parseInt(maxCid)) {
            try {
                totalCost.setText(String.valueOf(getTotalCost()));
                totalPoints.setText(String.valueOf(getPoint()));
                pieChart();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }else{
            dashcontent.setVisible(false);
        }
    }

    public double getPoint() throws SQLException {
        try{
            String sql = "Select Customer.Points From Customer Where CID = " +cidInput.getText();
            double amount = 0;
            Connection conn = dbConnection.getConntection();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                try{
                    amount+= Integer.parseInt(rs.getString(1));
                }catch (Exception e){
                    amount+=0;
                }
            }

            return amount;
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }
        return 0;
    }

    public double getTotalCost() throws SQLException {
        try{
            String sql = "Select TransactionAmount From Transactions Where customerID = " +cidInput.getText();
            double amount = 0;
            conn = dbConnection.getConntection();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                amount+= Double.parseDouble(rs.getString(1));
            }

            return amount;
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }
        return 0;
    }

    public void pieChart(){
        try{

            //get size of the TID
            int maxTID =0;
            String sql = "Select * From TransactionDetail";
            Connection conn = dbConnection.getConntection();
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                maxTID++;
            }
            conn.close();

            String sql1 = "Select Transactions.TransactionID From Transactions Where customerID = " +cidInput.getText();
            String[] getTID = new String[maxTID];
            int getTIDNUM=0;
            Connection conn1 = dbConnection.getConntection();
            ResultSet rs1 = conn1.createStatement().executeQuery(sql1);

            while (rs1.next()) {
                getTID[getTIDNUM]= rs1.getString(1);
                getTIDNUM++;
            }
            conn1.close();

            //find the customer's transaction id
            String[][] itemInf=new String[maxTID][3];
            int le = 0;
            for(int k=0 ; k<getTIDNUM;k++){
                String sql2 = "Select TransactionDetail.ItemID, TransactionDetail.ItemsTotal From TransactionDetail Where TransactionID = " +getTID[k];
                Connection conn2 = dbConnection.getConntection();
                ResultSet rs2 = conn2.createStatement().executeQuery(sql2);
                while (rs2.next()) {

                    itemInf[le][0]= rs2.getString(1);
                    itemInf[le][1]= rs2.getString(2);
                    le++;
                }
                conn2.close();
                rs2.close();
            }

            //get the itemCategory
            for(int j=0 ; j<le;j++){
                String sql3 = "Select Product.Category From Product Where ItemID = " +itemInf[j][0];
                Connection conn3 = dbConnection.getConntection();
                ResultSet rs3 = conn3.createStatement().executeQuery(sql3);
                while (rs3.next()) {
                    itemInf[j][2]= rs3.getString(1);
                }
                conn3.close();
            }


            String[][] customerInf=new String[le][2];
            int customerLen = 0;
            for(int j =0; j < le; j++) {
                if(customerLen != 0){
                    for(int k =0; k<customerLen;k++){
                        if(customerInf[k][0].equals(itemInf[j][2])){
                            customerInf[k][1] = String.valueOf(Double.parseDouble(itemInf[j][1])+Double.parseDouble(customerInf[k][1]));
                            break;
                        }else{
                            customerInf[customerLen][0] = itemInf[j][2];
                            customerInf[customerLen][1] = itemInf[j][1];
                            customerLen++;
                        }
                    }
                }else{
                    customerInf[customerLen][0] = itemInf[j][2];
                    customerInf[customerLen][1] = String.valueOf(Double.parseDouble(itemInf[j][1]));
                    customerLen++;
                }
            }
            chartView.getChildren().clear();
            this.pieData = FXCollections.observableArrayList();
            for(int k=0; k< customerLen;k++){
                this.pieData.add(new PieChart.Data(customerInf[k][0], Double.valueOf((customerInf[k][1]))));
            }
            //Create PieChart and assign data
            PieChart pChart = new PieChart(pieData);
            chartView.getChildren().add(pChart);


        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }
    }



}
