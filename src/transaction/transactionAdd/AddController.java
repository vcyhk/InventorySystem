package transaction.transactionAdd;

import common.AlertBox;
import common.dataAdd.DataAddController;
import customer.CustomerController;
import customer.CustomerData;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import transaction.TransactionController;
import transaction.TransactionData;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddController implements Initializable, DataAddController {

    String maxTID;
    int max = 0;
    private dbConnection dc;
    Connection conn;
    @FXML
    private Button createButton, itemAddBtn, itemDelBtn;
    @FXML
    private TextField cidAdd, itemNoAdd, itemDescAdd, itemCatAdd, unitPriceAdd, noOfUnitAdd, disAdd, itemTotAdd, transDTAdd;
    @FXML
    private ComboBox locationAdd, paymentMethodAdd;
    @FXML
    private VBox addScene;
    @FXML
    private TableView<AddData> addTable;
    @FXML
    private TableColumn<AddData, String> col_tid, col_itemNo, col_teamCat, col_itemDesc, col_unitPrice, col_noOfUnit, col_dis, col_itemTot, col_sel;

    private ObservableList<AddData> data;
    private ObservableList<AddData> items;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
        itemDescAdd.setDisable(true);
        itemCatAdd.setDisable(true);
        //unitPriceAdd.setDisable(true);
        itemTotAdd.setDisable(true);
        transDTAdd.setText(String.valueOf(LocalDate.now()));
        locationCombo();
        paymentMethodCombo();
        this.data = FXCollections.observableArrayList();
    }

    public void confirmAdd(ActionEvent Event) throws Exception {
        String message = "Is it insert customer information?";
        String header = "Confirm create customer information";
        if(AlertBox.confirmationBox(message, header)){
            if(data.size()>0)
                createData();
            else
                AlertBox.informationBox("Doesn't fill any data");
        }else{
            return;
        }
    }


    public void clearField(){
        itemNoAdd.clear();
        itemDescAdd.clear();
        itemCatAdd.clear();
        unitPriceAdd.clear();
        noOfUnitAdd.clear();
        disAdd.clear();
        itemTotAdd.clear();
    }

    public void itemAdd(ActionEvent Event) throws Exception{
        if(checkfield()) {
            if (!(checkCid())) {
                AlertBox.informationBox("Doesn't exist this Customer ID");
                cidAdd.setDisable(false);
            }else {
                cidAdd.setDisable(true);
                locationAdd.setDisable(true);

                String sql = "SELECT MAX(Transactions.TransactionID) FROM Transactions";
                conn = dbConnection.getConntection();

                ResultSet rs = conn.createStatement().executeQuery(sql);
                while (rs.next()) {
                    maxTID = rs.getString(1);
                }
                conn.close();

                if(maxTID!=null)
                    max = Integer.parseInt(maxTID) + 1;
                else
                    max = 1;

                this.data.add(new AddData(String.valueOf(max), itemNoAdd.getText(), itemCatAdd.getText(),
                        itemDescAdd.getText(), unitPriceAdd.getText(), noOfUnitAdd.getText(), disAdd.getText(),
                        itemTotAdd.getText()
                ));

                loadTable();
                clearField();
            }
        }else{
                AlertBox.informationBox("Doesn't fill all field");
            }
    }

    public void itemDel(ActionEvent Event) throws Exception{

        for (int i = 0; i< data.size();i++) {
            if ((data.get(i)).getRemark().isSelected()) {
                this.data.remove(i);
                i--;
            }
        }
        loadTable();
    }

    public void createData() throws Exception {
        //Inserts to Transactions
        String sqlInsert = "INSERT INTO transactions( TransactionID, CustomerID, Location, TransactionAmount, DiscountAmount, NetAmount, TransactionDateTime ) VALUES (?, ?, ?, ?, ?, ?, ?)";
        double disAmount = 0.0;
        double transAmount = 0.0;
        double netAmount = 0.0;
        try {
            for (int i = 0; i < data.size(); i++) {
                disAmount += Double.parseDouble(data.get(i).getDiscount());
                transAmount += Double.parseDouble(data.get(i).getUnitPrice()) * Double.parseDouble(data.get(i).getNoOfUnit());
            }
        } catch (Exception e) {
            /*disAmount = 0;*/
            for (int i = 0; i < data.size(); i++) {
                transAmount += Double.parseDouble(data.get(i).getUnitPrice()) * Double.parseDouble(data.get(i).getNoOfUnit());
            }
        }
        if (transAmount - disAmount > 0)
            netAmount = transAmount - disAmount;

        try {
            System.out.println(disAmount);
            conn = dbConnection.getConntection();
            PreparedStatement ps = conn.prepareStatement(sqlInsert);

            ps.setString(1, String.valueOf(max));
            ps.setString(2, this.cidAdd.getText());
            ps.setString(3, (String) this.locationAdd.getValue());
            ps.setString(4, String.valueOf(transAmount));
            ps.setString(5, String.valueOf(disAmount));
            ps.setString(6, String.valueOf(netAmount));
            ps.setString(7, this.transDTAdd.getText());

            ps.execute();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sqlInsert3 = "INSERT INTO Payment( TransactionID, Method, Amount, TransactionDate) VALUES ( ?, ?, ?, ?)";
        try {
            conn = dbConnection.getConntection();
            PreparedStatement ps1 = conn.prepareStatement(sqlInsert3);

            ps1.setString(1, String.valueOf(max));
            ps1.setString(2, String.valueOf(this.paymentMethodAdd.getValue()));
            ps1.setString(3, String.valueOf(transAmount));
            ps1.setString(4, this.transDTAdd.getText());

            ps1.execute();
            ps1.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Inserts to Transaction Details
        for (int i = 0; i < data.size(); i++) {
            String sqlInsert2 = "INSERT INTO TransactionDetail( TransactionID, ItemID, NumberOfUnit, Discount, ItemsTotal, UnitPrice) VALUES ( ?, ?, ?, ?, ?, ?)";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert2);

                ps.setString(1, data.get(i).getTid());
                ps.setString(2, data.get(i).getItemNo());
                ps.setString(3, data.get(i).getNoOfUnit());
                ps.setString(4, data.get(i).getDiscount());
                ps.setString(5, data.get(i).getItemsTotal());
                ps.setString(6, data.get(i).getUnitPrice());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

        closeWindow();
    }

    public void closeWindow() throws Exception {
        TransactionController transactionController = TransactionController.getInstance();
        transactionController.loadData();
        Stage stage = (Stage) createButton.getScene().getWindow();
        stage.close();
    }

    public void loadItem() throws Exception{
        try{
            String sql1 = "SELECT MAX(Product.ItemID) FROM Product";
            conn = dbConnection.getConntection();
            int maxPID = 0;
            ResultSet rs1 = conn.createStatement().executeQuery(sql1);
            while (rs1.next()){
                maxPID = Integer.parseInt(rs1.getString(1));
            }
            conn.close();
            try{
                int a =Integer.parseInt(itemNoAdd.getText());

                if(a <= maxPID){
                    String sql = "SELECT Product.Category, Product.Desc, Product.UnitPrice FROM Product";
                    sql+=" where Product.ItemID = "+itemNoAdd.getText();
                    conn = dbConnection.getConntection();

                    ResultSet rs = conn.createStatement().executeQuery(sql);
                    while (rs.next()){
                        String category = rs.getString(1);
                        String desc = rs.getString(2);
                        String unitPrice = rs.getString(3);
                        itemCatAdd.setText(category);
                        itemDescAdd.setText(desc);
                        unitPriceAdd.setText(unitPrice);
                    }
                    rs.close();
                    conn.close();

                    String sql2 = "Select Promotion.discount From Promotion Where EffectiveFrom <= '" + LocalDate.now() +"' and EffectiveTo >= '" + LocalDate.now() +"' and ItemID = " + itemNoAdd.getText();
                    conn = dbConnection.getConntection();

                    ResultSet rs2 = conn.createStatement().executeQuery(sql2);
                    while (rs2.next()){
                        String discount = rs2.getString(1);
                        disAdd.setText(discount);
                    }
                    rs2.close();
                    conn.close();

                }else{
                    itemCatAdd.clear();
                    itemDescAdd.clear();
                    unitPriceAdd.clear();
                }
            }catch (Exception e){
                System.err.println("Error " + e);
                itemCatAdd.clear();
                itemDescAdd.clear();
                unitPriceAdd.clear();
            }
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
            itemCatAdd.clear();
            itemDescAdd.clear();
            unitPriceAdd.clear();
        }

    }

    public void countItem() throws Exception{
        double unitP = 0, unitNo = 0, discount = 0, itemtotal = 0;
        try{
            unitP =Double.parseDouble(unitPriceAdd.getText());
            unitNo = Double.parseDouble(noOfUnitAdd.getText());
            discount = Double.parseDouble(disAdd.getText());
            itemtotal = (unitP * unitNo)- (discount);
            itemTotAdd.setText(String.valueOf(itemtotal));
        }catch (Exception e){
            itemTotAdd.setText(String.valueOf(unitP * unitNo));
        }
    }
    //error check
    //check whether the cid exist
    public boolean checkCid() throws SQLException {
        String sql1 = "SELECT MAX(Customer.CID) FROM Customer";
        conn = dbConnection.getConntection();
        int maxCID = 0;
        ResultSet rs1 = conn.createStatement().executeQuery(sql1);
        while (rs1.next()){
            maxCID = Integer.parseInt(rs1.getString(1));
        }
        conn.close();
        if(maxCID< Integer.parseInt(cidAdd.getText()))
            return false;
        else
            return true;
    }

    //check whether all field has enter
    public boolean checkfield() {
        return !cidAdd.getText().equals("") && !itemNoAdd.getText().equals("") && !noOfUnitAdd.getText().equals("") &&
                !(locationAdd.getSelectionModel().getSelectedItem() == null ||locationAdd.getSelectionModel().getSelectedItem() == "");
    }

    //loop the combobox of location
    public void locationCombo(){
        try{
            String sql = "SELECT Location.name FROM Location";
            conn = dbConnection.getConntection();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String locationName = rs.getString(1);
                locationAdd.getItems().add(locationName);
            }
            conn.close();
            rs.close();

        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }

    }

    //loop the combobox of paymentMethod
    public void paymentMethodCombo(){
        try{
            String sql = "SELECT PaymentMethod.name FROM PaymentMethod";
            conn = dbConnection.getConntection();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String paymentMethodName = rs.getString(1);
                paymentMethodAdd.getItems().add(paymentMethodName);
            }
            conn.close();
            rs.close();

        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }
    }

    //load add Table
    public void loadTable(){
        this.col_tid.setCellValueFactory(new PropertyValueFactory<AddData, String>("tid"));
        this.col_itemNo.setCellValueFactory(new PropertyValueFactory<AddData, String>("itemNo"));
        this.col_teamCat.setCellValueFactory(new PropertyValueFactory<AddData, String>("itemCat"));
        this.col_itemDesc.setCellValueFactory(new PropertyValueFactory<AddData, String>("itemDesc"));
        this.col_unitPrice.setCellValueFactory(new PropertyValueFactory<AddData, String>("unitPrice"));
        this.col_noOfUnit.setCellValueFactory(new PropertyValueFactory<AddData, String>("noOfUnit"));
        this.col_dis.setCellValueFactory(new PropertyValueFactory<AddData, String>("discount"));
        this.col_itemTot.setCellValueFactory(new PropertyValueFactory<AddData, String>("itemsTotal"));
        this.col_sel.setCellValueFactory(new PropertyValueFactory<AddData, String>("remark"));

        this.addTable.setItems(null);
        this.addTable.setItems(this.data);
    }
}
