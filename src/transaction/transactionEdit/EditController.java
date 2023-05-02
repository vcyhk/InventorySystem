package transaction.transactionEdit;
/////[1] : first step ,[n]: anywhere, [l]: last step
import common.AlertBox;
import common.dataEdit.DataEditController;
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
import org.apache.commons.math3.analysis.function.Exp;
import product.ProductController;
import transaction.TransactionController;
import transaction.TransactionData;
import transaction.transactionAdd.AddData;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditController  implements Initializable, DataEditController {

    private dbConnection dc;
    private Boolean isInEditMode = Boolean.FALSE;
    private int editTime = 0;
    private int istID;
    private int isLoad = 0;
    public boolean hasmodify = false;
    public boolean editOver = false;
    Connection conn;

    @FXML
    private TextField transTidShow, transCidShow, transLocatShow, transDTShow, transTamountShow, transDamountShow, transNetamountShow;
    @FXML
    private TextField cidEdit, itemNoEdit, itemDescEdit, unitPriceEdit, noOfUnitEdit, disEdit,itemTotEdit, itemCatEdit;
    @FXML
    private Button editButton, itemConfirmBtn, itemAddBtn, itemDelBtn, editOverBtn;
    @FXML
    private ComboBox transPayMethodShow;
    @FXML
    private VBox editScene;
    @FXML
    private TableView<TransactionDetailData> editTable;
    @FXML
    private TableColumn<TransactionDetailData, String> col_tid, col_itemNo, col_teamCat, col_itemDesc, col_unitPrice, col_noOfUnit, col_dis, col_itemTot, col_payMethod, col_sel;
    private ObservableList<TransactionDetailData> data;
    List<TransactionDetailData> originalData = new ArrayList();
    List<TransactionDetailData> moditfyData = new ArrayList();
    List<TransactionDetailData> addData = new ArrayList();
    List<TransactionDetailData> deleteData = new ArrayList();
    List<TransactionDetailData> items = new ArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
        notEdit();
        istID = TransactionController.thisTid;
        try {
            loadDetail();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        itemConfirmBtn.setDisable(true);

        paymentMethodCombo();
        //loopCombo();
    }

    //[l]
    public void confirmEdit(ActionEvent Event) throws Exception {

        String message = "Are you sure you want to edit?";
        String header = "Edit the customer information";
        if (AlertBox.confirmationBox(message, header)) {
            if (hasmodify)
                updateInfo();
            else
                closeWindow();
        } else {
            return;
        }
    }

    //[1]
    public void inflateUI(TransactionData selectedForEdit) {

        transCidShow.setText(selectedForEdit.getCid());
        cidEdit.setText(selectedForEdit.getCid());
        transTidShow.setText(selectedForEdit.getTid());
        transLocatShow.setText(selectedForEdit.getLocation());
        transDTShow.setText(selectedForEdit.getTranDT());
        transTamountShow.setText(selectedForEdit.getTranAmt());
        transDamountShow.setText(selectedForEdit.getDisAmt());
        transNetamountShow.setText(selectedForEdit.getNetAmt());
        isInEditMode = Boolean.TRUE;
    }

    //[l]
    public void updateInfo() throws Exception {

        //Inserts to Transaction Details
        for (int i = 0; i < addData.size(); i++) {
            String sqlInsert2 = "INSERT INTO TransactionDetail( TransactionID, ItemID, NumberOfUnit, Discount, ItemsTotal, UnitPrice) VALUES (?, ?, ?, ?, ?, ?)";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert2);

                ps.setString(1, addData.get(i).getTid());
                ps.setString(2, addData.get(i).getItemID());
                ps.setString(3, addData.get(i).getNumberOfUnit());
                ps.setString(4, addData.get(i).getDiscount());
                ps.setString(5, addData.get(i).getItemsTotal());
                ps.setString(6, addData.get(i).getUnitPrice());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        //modify
        int index1 = 0;
        for (TransactionDetailData a : this.items) {
            try {
                conn = dbConnection.getConntection();
                Statement stmt1 = null;
                stmt1 = conn.createStatement();
                String sqlInsert = "Update TransactionDetail set ";
                if (!moditfyData.get(index1).getTid().equals(""))
                    sqlInsert += "  TransactionID = " + this.moditfyData.get(index1).getTid();
                if (!moditfyData.get(index1).getItemID().equals(""))
                    sqlInsert += "  , ItemID = " + this.moditfyData.get(index1).getItemID();
                if (!moditfyData.get(index1).getNumberOfUnit().equals(""))
                    sqlInsert += "  , NumberOfUnit = " + this.moditfyData.get(index1).getNumberOfUnit();
                if (!moditfyData.get(index1).getDiscount().equals(""))
                    sqlInsert += "  , Discount =" + this.moditfyData.get(index1).getDiscount();
                if (!moditfyData.get(index1).getItemsTotal().equals(""))
                    sqlInsert += "  , ItemsTotal = " + this.moditfyData.get(index1).getItemsTotal();
                if(!moditfyData.get(index1).getUnitPrice().equals(""))
                    sqlInsert += "  , UnitPrice = " + this.moditfyData.get(index1).getUnitPrice();
                if (!items.get(index1).getTid().equals(""))
                    sqlInsert += "  where TransactionID = " + a.getTid();
                if (!items.get(index1).getItemID().equals(""))
                    sqlInsert += "  and ItemID = " + a.getItemID();
                if (!items.get(index1).getNumberOfUnit().equals(""))
                    sqlInsert += "  and NumberOfUnit = " + a.getNumberOfUnit();
                if (!items.get(index1).getDiscount().equals(""))
                    sqlInsert += "  and Discount =" + a.getDiscount();
                if (!items.get(index1).getItemsTotal().equals(""))
                    sqlInsert += "  and ItemsTotal = " + a.getItemsTotal();
                stmt1.executeUpdate(sqlInsert);
                stmt1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            index1++;
        }


        //delete
        for (TransactionDetailData b : this.deleteData) {
            conn = dbConnection.getConntection();
            String[] sqlNo = new String[7];
            String sqlInsert = null;
            int k = 0;
            if (!(b.getTid().equals(""))) {
                sqlInsert = "DELETE FROM TransactionDetail Where TransactionID = ? ";
                sqlNo[k] = b.getTid();
                k++;
            }else
                break;
            if (!(b.getItemID().equals(""))) {
                sqlInsert += "and ItemID = ? ";
                sqlNo[k] = b.getItemID();
                k++;
            }
            if (!b.getNumberOfUnit().equals("")) {
                sqlInsert += "and NumberOfUnit = ? ";
                sqlNo[k] = b.getNumberOfUnit();
                k++;
            }

            if (!b.getDiscount().equals("")) {
                sqlInsert += "and Discount = ? ";
                sqlNo[k] = b.getDiscount();
                k++;
            }
            if (!b.getItemsTotal().equals("")) {
                sqlInsert += "and ItemsTotal = ? ";
                sqlNo[k] = b.getItemsTotal();
                k++;
            }

            PreparedStatement stmt2 = conn.prepareStatement(sqlInsert);
            for (int m = 0; m < k; m++) {
                stmt2.setString(m+1, sqlNo[m]);
            }
            stmt2.execute();
            stmt2.close();
        }

        updateTransaction();
        updatePayment();

        closeWindow();
    }

    //[l]
    public void updateTransaction() throws Exception {
        //Update Transaction
        double transAmount = 0,disAmount=0,netAmount=0;
        String sql = "SELECT TransactionDetail.Discount, TransactionDetail.ItemsTotal FROM TransactionDetail";
        sql+=" where TransactionDetail.TransactionID = "+transTidShow.getText();
        conn = dbConnection.getConntection();
        ResultSet rs1 = conn.createStatement().executeQuery(sql);
        while (rs1.next()){
            try {
                disAmount += Double.parseDouble(rs1.getString(1));
            }catch (Exception e){disAmount += 0;}
            try{
            transAmount += Double.parseDouble(rs1.getString(2));
            }catch (Exception e){transAmount += 0;}
        }
        conn.close();
        netAmount = transAmount - disAmount;
        try {
            conn = dbConnection.getConntection();
            Statement stmt1 = null;
            stmt1 = conn.createStatement();
            String sqlInsert = "Update Transactions set ";
            sqlInsert+="  TransactionAmount = " +transAmount;
            sqlInsert+="  , DiscountAmount = " +disAmount;
            sqlInsert+="  , NetAmount = " +netAmount;
            sqlInsert+="  where TransactionID = " +transTidShow.getText();
            sqlInsert+="  and CustomerID = " +transCidShow.getText();
            stmt1.executeUpdate(sqlInsert);

/*            String sqlInsert3 = "Update Payment set";
            sqlInsert3 += " Method = '" + transPayMethodShow.getText()+"' ";
            sqlInsert3 += ", Amount = " + transAmount +" ";
            sqlInsert3 +="  where TransactionID = " +transTidShow.getText();
            PreparedStatement ps1 = conn.prepareStatement(sqlInsert3);
            ps1.execute();
            ps1.close();*/
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePayment() throws Exception {
        try {
            conn = dbConnection.getConntection();
            String sql1 = "SELECT TransactionAmount, TransactionDateTime FROM Transactions where TransactionID = " + transTidShow.getText();
            ResultSet rs = conn.createStatement().executeQuery(sql1);
            String amount = null, date = null;

            while (rs.next()) {
                amount = rs.getString(1);
                date = rs.getString(2);
            }
            rs.close();
            String sql2 = "SELECT COUNT(*) FROM Payment where TransactionID = " + transTidShow.getText();
            ResultSet rs2 = conn.createStatement().executeQuery(sql2);
            int value1 = 0;
            if (Integer.parseInt(rs2.getString(1)) > 0) {
                value1++;
                System.out.println(amount);
            }
            System.out.println(amount);
            rs2.close();
            if(value1 == 0 ){

                String sqlInsert2 = "INSERT INTO Payment( TransactionID, Method, Amount, TransactionDate) VALUES ( ?, ?, ?, ?)";
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert2);

                ps.setString(1, transTidShow.getText());
                ps.setString(2, (String) this.transPayMethodShow.getValue());
                ps.setString(3, amount);
                ps.setString(4, date);

                ps.execute();
                ps.close();

            }else{

                String sqlInsert = "Update Payment set Method = ?, Amount = ?, TransactionDate = ? where TransactionID = ?";
                PreparedStatement stmt = conn.prepareStatement(sqlInsert);

                stmt.setString(1, (String) this.transPayMethodShow.getValue());
                stmt.setString(2, amount);
                stmt.setString(3, date);
                stmt.setString(4, transTidShow.getText());
                stmt.execute();
            }
            conn.close();
        }catch (Exception e){System.out.println(e);}
    }



    //[l]
    public void closeWindow() throws Exception {
        TransactionController transactionController = TransactionController.getInstance();
        transactionController.loadData();
        Stage stage = (Stage) editButton.getScene().getWindow();
        stage.close();
    }


    //[1]
    public void notEdit(){
        transTidShow.setDisable(true);
        transCidShow.setDisable(true);
        transLocatShow.setDisable(true);
        transDTShow.setDisable(true);
        transTamountShow.setDisable(true);
        transDamountShow.setDisable(true);
        transNetamountShow.setDisable(true);
        transPayMethodShow.setDisable(true);
        itemCatEdit.setDisable(true);
        itemDescEdit.setDisable(true);
        //unitPriceEdit.setDisable(true);
        cidEdit.setDisable(true);
    }

    //[1]load the transaction's detail
    public void loadDetail() throws SQLException {
        conn = dbConnection.getConntection();
        this.data = FXCollections.observableArrayList();
        String sql1 = "SELECT TransactionDetail.TransactionID, TransactionDetail.ItemID,TransactionDetail.NumberOfUnit, TransactionDetail.Discount, TransactionDetail.ItemsTotal, Product.Category, Product.Desc, TransactionDetail.UnitPrice FROM TransactionDetail, Product  where TransactionID = " + istID + " and TransactionDetail.ItemID = Product.ItemID";
        ResultSet rs = conn.createStatement().executeQuery(sql1);
        while (rs.next()) {
            this.data.add(new TransactionDetailData(rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)
            ));
        }
        rs.close();

        String sql2 = "SELECT Method FROM Payment where TransactionID = " + istID ;
        ResultSet rs1 = conn.createStatement().executeQuery(sql2);

        while (rs1.next()) {
            transPayMethodShow.setValue(rs1.getString(1));
        }
        rs1.close();
        conn.close();
        loadTable();

    }


    //[2]set the selected data of the item
    public void setFieldValue(TransactionDetailData selectedForEdit) throws Exception {
        itemNoEdit.setText(selectedForEdit.getItemID());
        noOfUnitEdit.setText(selectedForEdit.getNumberOfUnit());
        disEdit.setText(selectedForEdit.getDiscount());
        itemCatEdit.setText(selectedForEdit.getItemCategory());
        itemDescEdit.setText(selectedForEdit.getItemDesc());
        unitPriceEdit.setText(selectedForEdit.getUnitPrice());
        itemTotEdit.setText(selectedForEdit.getItemsTotal());
    }

    //[2] modify the data of the transaction details
    public void modifyData(){
        if(checkfield()) {
            hasmodify = true;
            itemAddBtn.setDisable(false);
            itemDelBtn.setDisable(false);
            itemConfirmBtn.setDisable(true);
            this.moditfyData.add(new TransactionDetailData(transTidShow.getText(), itemNoEdit.getText(), noOfUnitEdit.getText(),
                    disEdit.getText(), itemTotEdit.getText(), itemCatEdit.getText(), itemDescEdit.getText(),
                    unitPriceEdit.getText()
            ));
            int index = 0;
            boolean same = false;
            for (TransactionDetailData a : this.data) {
                for (TransactionDetailData b : this.originalData) {
                    if (a.getAll().equals(b.getAll())) {
                        same = true;
                        items.add(data.get(index));
                        this.data.set(index, this.moditfyData.get(moditfyData.size() - 1));
                        break;
                    }
                }
                if (same)
                    break;
                index++;
            }
            loadTable();
            clearField();
        }else{
            AlertBox.informationBox("Doesn't fill all field");
        }
    }

    //edit overview
    public void modifyOverData(){
        if(!editOver){
            transPayMethodShow.setEditable(true);
            transPayMethodShow.setDisable(false);
            itemNoEdit.setDisable(true);
            noOfUnitEdit.setDisable(true);
            disEdit.setDisable(true);
            itemTotEdit.setDisable(true);
            editButton.setDisable(true);
            itemConfirmBtn.setDisable(true);
            itemAddBtn.setDisable(true);
            itemDelBtn.setDisable(true);
            editOver = true;
        }else{
            transPayMethodShow.setEditable(false);
            transPayMethodShow.setDisable(true);
            itemNoEdit.setDisable(false);
            noOfUnitEdit.setDisable(false);
            disEdit.setDisable(false);
            itemTotEdit.setDisable(false);
            editButton.setDisable(false);
            itemConfirmBtn.setDisable(false);
            itemAddBtn.setDisable(false);
            itemDelBtn.setDisable(false);
            hasmodify = true;
            editOver = false;
        }
    }

    //delete the item in the table which show
    public void itemDel(ActionEvent Event) throws Exception{
            hasmodify = true;
            for (int i = 0; i< data.size();i++) {
                if ((data.get(i)).getRemark().isSelected()) {
                    this.deleteData.add(new TransactionDetailData( (data.get(i)).getTid(), (data.get(i)).getItemID(), (data.get(i)).getNumberOfUnit(),
                            (data.get(i)).getDiscount(), (data.get(i)).getItemsTotal(), (data.get(i)).getItemCategory(), (data.get(i)).getItemDesc(),
                            (data.get(i)).getUnitPrice()
                    ));
                    this.data.remove(i);
                    i--;
                }
            }
        loadTable();
    }

    // add the new transaction detail into the whole transaction into the table [not really into the data into database]
    public void itemAdd(ActionEvent Event) throws Exception{
        if(checkfield()) {

            hasmodify = true;
            this.addData.add(new TransactionDetailData( transTidShow.getText(), itemNoEdit.getText(), noOfUnitEdit.getText(),
                    disEdit.getText(), itemTotEdit.getText(), itemCatEdit.getText(), itemDescEdit.getText(),
                    unitPriceEdit.getText())
            );

            this.data.add(new TransactionDetailData( transTidShow.getText(), itemNoEdit.getText(), noOfUnitEdit.getText(),
                    disEdit.getText(), itemTotEdit.getText(), itemCatEdit.getText(), itemDescEdit.getText(),
                    unitPriceEdit.getText())
            );

            loadTable();
            clearField();
        }else{
            AlertBox.informationBox("Doesn't fill all field");
        }
    }

    public void clearField(){
        itemNoEdit.clear();
        itemDescEdit.clear();
        itemCatEdit.clear();
        unitPriceEdit.clear();
        noOfUnitEdit.clear();
        disEdit.clear();
        itemTotEdit.clear();
    }

    //put the selected data of the table into the field which let people modify
    public void handlEditOption(ActionEvent event) throws Exception {
        TransactionDetailData selectedForEdit = editTable.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            return;
        }
        setFieldValue(selectedForEdit);
        itemAddBtn.setDisable(true);
        itemDelBtn.setDisable(true);
        itemConfirmBtn.setDisable(false);

        this.originalData.add(new TransactionDetailData( selectedForEdit.getTid(), selectedForEdit.getItemID(), selectedForEdit.getNumberOfUnit(),
                selectedForEdit.getDiscount(), selectedForEdit.getItemsTotal(), selectedForEdit.getItemCategory(), selectedForEdit.getItemDesc(),
                selectedForEdit.getUnitPrice()
        ));
    }

    //[n]load the data into table
    public void loadTable(){
        this.col_tid.setCellValueFactory(new PropertyValueFactory<TransactionDetailData, String>("tid"));
        this.col_itemNo.setCellValueFactory(new PropertyValueFactory<TransactionDetailData, String>("itemID"));
        this.col_teamCat.setCellValueFactory(new PropertyValueFactory<TransactionDetailData, String>("itemCategory"));
        this.col_itemDesc.setCellValueFactory(new PropertyValueFactory<TransactionDetailData, String>("itemDesc"));
        this.col_unitPrice.setCellValueFactory(new PropertyValueFactory<TransactionDetailData, String>("unitPrice"));
        this.col_noOfUnit.setCellValueFactory(new PropertyValueFactory<TransactionDetailData, String>("numberOfUnit"));
        this.col_dis.setCellValueFactory(new PropertyValueFactory<TransactionDetailData, String>("discount"));
        this.col_itemTot.setCellValueFactory(new PropertyValueFactory<TransactionDetailData, String>("itemsTotal"));
        this.col_sel.setCellValueFactory(new PropertyValueFactory<TransactionDetailData, String>("remark"));

        this.editTable.setItems(null);
        this.editTable.setItems(this.data);
    }

    //check whether all field has enter
    public boolean checkfield() {
        return !cidEdit.getText().equals("") && !itemNoEdit.getText().equals("") && !noOfUnitEdit.getText().equals("");
    }

    //[1]loop the combobox of paymentMethod
    public void paymentMethodCombo(){
        try{
            String sql = "SELECT PaymentMethod.name FROM PaymentMethod";
            conn = dbConnection.getConntection();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String paymentMethodName = rs.getString(1);
                transPayMethodShow.getItems().add(paymentMethodName);
            }
            conn.close();
            rs.close();

        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }
    }

    //[1]load the product price, Category, Desc
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
                int a =Integer.parseInt(itemNoEdit.getText());

                if(a <= maxPID){
                    String sql = "SELECT Product.Category, Product.Desc, Product.UnitPrice FROM Product";
                    sql+=" where Product.ItemID = "+itemNoEdit.getText();
                    conn = dbConnection.getConntection();

                    ResultSet rs = conn.createStatement().executeQuery(sql);
                    while (rs.next()){
                        String category = rs.getString(1);
                        String desc = rs.getString(2);
                        String unitPrice = rs.getString(3);
                        itemCatEdit.setText(category);
                        itemDescEdit.setText(desc);
                        unitPriceEdit.setText(unitPrice);
                        itemTotEdit.clear();
                    }
                    conn.close();
                }else{
                    itemCatEdit.clear();
                    itemDescEdit.clear();
                    unitPriceEdit.clear();
                    itemTotEdit.clear();
                }
            }catch (Exception e){
                System.err.println("Error " + e);
                itemCatEdit.clear();
                itemDescEdit.clear();
                unitPriceEdit.clear();
                itemTotEdit.clear();
            }
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
            itemCatEdit.clear();
            itemDescEdit.clear();
            unitPriceEdit.clear();
            itemTotEdit.clear();
        }

    }

    //forgot what is this. maybe unuse. JUST HOLD IT
    public void countItem() throws Exception{
        double unitP = 0, unitNo = 0, discount = 0, itemtotal = 0;
        try{
            unitP =Double.parseDouble(unitPriceEdit.getText());
            unitNo = Double.parseDouble(noOfUnitEdit.getText());
            discount = Double.parseDouble(disEdit.getText());
            itemtotal = (unitP * unitNo)- (discount);
            itemTotEdit.setText(String.valueOf(itemtotal));
        }catch (Exception e){
            itemTotEdit.setText(String.valueOf(unitP * unitNo));
        }
    }


}