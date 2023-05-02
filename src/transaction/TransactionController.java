package transaction;

import common.*;
import customer.CustomerController;
import customer.CustomerData;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import transaction.*;


import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class TransactionController extends PageController implements Initializable, PageGuide {
    public static int thisTid;
    PageModel transactionModel = new PageModel();
    public boolean andT = false;
    int index = 0;
    Connection conn;
    private String sql = "SELECT Transactions.* FROM Transactions";
    @FXML
    private Label staffName, staffID,dbStatus;
    @FXML
    private ImageView staffIcon;
    @FXML
    private Button exitButton, transactionButton;
    @FXML
    private TextField tidInput, cidInput, tranAmtInput, disAmtInput, netAmtInput;
    @FXML
    private DatePicker tranDtInput;
    @FXML
    private ComboBox locationInput, paymentMethodInput;
    @FXML
    private BorderPane basicScene;
    @FXML
    private TableView<TransactionData> transactionTable;
    @FXML
    private TableColumn<TransactionData, String> col_tid, col_cid, col_Location, col_TranDT, col_TranAmt, col_DisAmt, col_NetAmt,col_Payment, col_sel;
    private ObservableList<TransactionData> data, items, others;
    private static TransactionController instance = null;

    public TransactionController(){
        instance = this;
    }

    public static TransactionController getInstance(){
        return instance;
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/resources/stafficon/"+staff+".png");
        Image image = new Image(file.toURI().toString());
        staffIcon.setImage(image);

        if(transactionModel.isDatabaseConnected()){
            dbStatus.setText("Connected");
            dbStatus.setTextFill(Color.GREEN);
        }else{
            dbStatus.setText("Not Connected");
            dbStatus.setTextFill(Color.RED);
        }

        transactionButton.setDisable(true);

        try {
            setStaff(staff, staffName, staffID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loopCombo();
        loopPaymentCombo();
        new AutoCompleteBox(locationInput);
        new AutoCompleteBox(paymentMethodInput);
    }

    public void deleteRecord(ActionEvent event) throws Exception {
        conn = null;
        Statement stmt = null;

        items = transactionTable.getItems();
        try {
            for (TransactionData item : items) {
                if (item.getRemark().isSelected()) {
                    String sqlD = "Delete from transactions Where TransactionID = " + item.getTid();
                    conn = dbConnection.getConntection();
                    stmt = conn.createStatement();
                    stmt.executeUpdate(sqlD);
                    conn.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loadData();
    }

    //Import Data
    public void importData(ActionEvent event) throws Exception {
        Stage editStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader. load(getClass().getResource("/transaction/transactionImport/importPage.fxml").openStream());
        transaction.transactionImport.ImportController importController = (transaction.transactionImport.ImportController)loader.getController();
        Scene scene = new Scene(root);
        editStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        editStage.setScene(scene);
        editStage.setTitle("Inventory System");
        editStage.setResizable(false);
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.show();
    }

    public void exportToExcel(ActionEvent event) throws SQLException, IOException {
        File selectedDirectory1 = selectPath("Transaction");
        if (selectedDirectory1 == null) {
            //No Directory selected
        } else {
            String fileTypeName = selectedDirectory1.getName();
            String[] parts = fileTypeName.split(Pattern.quote("."));
            String part2 = parts[1];
            conn = dbConnection.getConntection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            String query1 = "Select * from Transactions";
            String query2 = "Select * from TransactionDetail";
            if (part2.toLowerCase().equals("xlsx")) {
                try {
                    stmt = conn.prepareStatement(query1);
                    rs = stmt.executeQuery();

                    XSSFWorkbook wb = new XSSFWorkbook();//for earlier version use HSSF
                    XSSFSheet sheet = wb.createSheet("Transactions");
                    XSSFRow header = sheet.createRow(0);
                    header.createCell(0).setCellValue("TransactionID");
                    header.createCell(1).setCellValue("CustomerID");
                    header.createCell(2).setCellValue("Location");
                    header.createCell(3).setCellValue("Transaction DateTime");
                    header.createCell(4).setCellValue("Transaction Amount");
                    header.createCell(5).setCellValue("Discount Amount");
                    header.createCell(6).setCellValue("Net Amount");


                    sheet.autoSizeColumn(1);
                    sheet.autoSizeColumn(2);
                    sheet.autoSizeColumn(3);
                    sheet.autoSizeColumn(4);
                    sheet.autoSizeColumn(5);
                    sheet.autoSizeColumn(6);
                    sheet.autoSizeColumn(7);

                    sheet.setZoom(150);

                    int index = 1;
                    while (rs.next()) {
                        XSSFRow row = sheet.createRow(index);
                        row.createCell(0).setCellValue(rs.getString("TransactionID"));
                        row.createCell(1).setCellValue(rs.getString("CustomerID"));
                        row.createCell(2).setCellValue(rs.getString("Location"));
                        row.createCell(3).setCellValue(rs.getString("TransactionDateTime"));
                        row.createCell(4).setCellValue(rs.getString("TransactionAmount"));
                        row.createCell(5).setCellValue(rs.getString("DiscountAmount"));
                        row.createCell(6).setCellValue(rs.getString("NetAmount"));
                        index++;
                    }

                    stmt = conn.prepareStatement(query2);
                    rs = stmt.executeQuery();
                    XSSFSheet sheet2 = wb.createSheet("Transaction Details");
                    XSSFRow header2 = sheet2.createRow(0);
                    header2.createCell(0).setCellValue("TransactionID");
                    header2.createCell(1).setCellValue("ItemID");
                    header2.createCell(2).setCellValue("Number Of Unit");
                    header2.createCell(3).setCellValue("Discount");
                    header2.createCell(4).setCellValue("Items Total");
                    header2.createCell(5).setCellValue("Unit Price");

                    sheet2.autoSizeColumn(1);
                    sheet2.autoSizeColumn(2);
                    sheet2.autoSizeColumn(3);
                    sheet2.autoSizeColumn(4);
                    sheet2.autoSizeColumn(5);
                    sheet2.autoSizeColumn(6);

                    sheet2.setZoom(150);

                    int index2 = 1;
                    while (rs.next()) {
                        XSSFRow row = sheet2.createRow(index2);
                        row.createCell(0).setCellValue(rs.getString("TransactionID"));
                        row.createCell(1).setCellValue(rs.getString("ItemID"));
                        row.createCell(2).setCellValue(rs.getString("NumberOfUnit"));
                        row.createCell(3).setCellValue(rs.getString("Discount"));
                        row.createCell(4).setCellValue(rs.getString("ItemsTotal"));
                        row.createCell(5).setCellValue(rs.getString("UnitPrice"));
                        index2++;
                    }

                    FileOutputStream fileOut = new FileOutputStream(selectedDirectory1.getAbsolutePath());//before 2007 version xls
                    wb.write(fileOut);
                    fileOut.close();

                    stmt.close();
                    rs.close();

                    AlertBox.informationBox("Finish the export");

                } catch (SQLException | FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (part2.toLowerCase().equals("csv")) {
                    Writer writer1 = null;
                    Writer writer2 = null;
                    try {
                        File file1 = new File(selectedDirectory1.getAbsolutePath());
                        writer1 = new BufferedWriter(new FileWriter(file1));
                        stmt = conn.prepareStatement(query1);
                        rs = stmt.executeQuery();
                        /*String text = "TransactionID" + "," + "CustomerID" + "," + "Location" + ","
                                + "Transaction DateTime" + "," + "Transaction Amount" + "," + "Discount Amount" + "," + "Net Amount" + "\n";*/
                        while (rs.next()) {
                            String text = rs.getString("TransactionID") + "," + rs.getString("CustomerID") + "," +
                                    rs.getString("Location") + "," + rs.getString("TransactionDateTime") + ","
                                    + rs.getString("TransactionAmount") + "," + rs.getString("DiscountAmount") + "," + rs.getString("NetAmount") + "\n";
                            writer1.write(text);
                        }

                        File selectedDirectory2 = selectPathForCSV("Transaction_Details");

                        File file2 = new File(selectedDirectory2.getAbsolutePath());
                        writer2 = new BufferedWriter(new FileWriter(file2));
                        stmt = conn.prepareStatement(query2);
                        rs = stmt.executeQuery();

                        /*text = "TransactionID" + "," + "ItemID" + "," + "Number Of Unit" + "," + "Discount" + "," + "Items Total" + "," + "Unit Price" + "\n";
                        writer2.write(text);*/
                        while (rs.next()) {
                            String text = rs.getString("TransactionID") + "," + rs.getString("ItemID") + "," +
                                    rs.getString("NumberOfUnit") + "," + rs.getString("Discount") + "," +
                                    rs.getString("ItemsTotal") + "," +  rs.getString("UnitPrice") +"\n";
                            writer2.write(text);
                        }
                        AlertBox.informationBox("Finish the export");

                    } catch (FileNotFoundException | NullPointerException ex) {
                        AlertBox.informationBox("Cannot cover the opened file!");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        writer1.flush();
                        writer2.flush();
                        writer1.close();
                        writer2.close();
                    }
                }
            }
        }
        conn.close();
    }

    public void dataToExcel(ActionEvent event) throws SQLException{

        items = transactionTable.getItems();

        try {
            int i = 0;
            boolean[] check = new boolean[items.size()];
            for (int q = 0; q<items.size();q++) {
                if (items.get(q).getRemark().isSelected()) {
                    check[q] = true;
                    i++;
                }
            }

            if(i !=0){
                File selectedDirectory1 = selectPath("Transaction");
                if(selectedDirectory1 == null){
                    //No Directory selected
                }else {
                    String fileTypeName = selectedDirectory1.getName();
                    String[] parts = fileTypeName.split(Pattern.quote("."));
                    String part2 = parts[1];
                    if(part2.toLowerCase().equals("xlsx")) {
                        try {
                            XSSFWorkbook wb = new XSSFWorkbook();//for earlier version use HSSF
                            XSSFSheet sheet = wb.createSheet("Transaction");
                            XSSFRow header = sheet.createRow(0);
                            header.createCell(0).setCellValue("TransactionID");
                            header.createCell(1).setCellValue("CustomerID");
                            header.createCell(2).setCellValue("Location");
                            header.createCell(3).setCellValue("Transaction DateTime");
                            header.createCell(4).setCellValue("Transaction Amount");
                            header.createCell(5).setCellValue("Discount Amount");
                            header.createCell(6).setCellValue("Net Amount");

                            sheet.autoSizeColumn(1);
                            sheet.autoSizeColumn(2);
                            sheet.autoSizeColumn(3);

                            sheet.setZoom(150);

                            index = 1;
                            for (int j = 0; j < items.size(); j++) {
                                if (check[j])
                                    getTransactionsData(j, sheet);
                            }

                            XSSFSheet sheet2 = wb.createSheet("Transaction Details");
                            XSSFRow header2 = sheet2.createRow(0);
                            header2.createCell(0).setCellValue("TransactionID");
                            header2.createCell(1).setCellValue("ItemID");
                            header2.createCell(2).setCellValue("Number Of Unit");
                            header2.createCell(3).setCellValue("Discount");
                            header2.createCell(4).setCellValue("Items Total");
                            header2.createCell(5).setCellValue("Unit Price");

                            sheet2.autoSizeColumn(1);
                            sheet2.autoSizeColumn(2);
                            sheet2.autoSizeColumn(3);
                            sheet2.autoSizeColumn(4);
                            sheet2.autoSizeColumn(5);
                            sheet2.autoSizeColumn(6);

                            sheet2.setZoom(150);

                            index = 1;
                            for (int j = 0; j < items.size(); j++) {
                                if (check[j])
                                    getTransactionDetailData(j, sheet2);
                            }

                            FileOutputStream fileOut = new FileOutputStream(selectedDirectory1.getAbsolutePath());//before 2007 version xls
                            wb.write(fileOut);
                            fileOut.close();

                            AlertBox.informationBox("Finish the export");
                            loadData();

                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        if(part2.toLowerCase().equals("csv")) {

                            Writer writer1 = null;
                            Writer writer2 = null;
                            try {
                                File file1 = new File(selectedDirectory1.getAbsolutePath());
                                writer1 = new BufferedWriter(new FileWriter(file1));

                                /*String text = "TransactionID" + "," + "CustomerID" + "," + "Location" + ","
                                        + "Transaction DateTime" + "," + "Transaction Amount" + "," + "Discount Amount" + "," + "Net Amount" + "\n";
                                writer1.write(text);*/

                                index = 1;
                                for (int j = 0; j < items.size(); j++) {
                                    if (check[j])
                                        getsqlTransactionforCSV(j,writer1);
                                }

                                File selectedDirectory2 = selectPathForCSV("Transaction_Details");
                                File file2 = new File(selectedDirectory2.getAbsolutePath());
                                writer2 = new BufferedWriter(new FileWriter(file2));

/*                                text = "TransactionID" + "," + "ItemID" + "," + "Number Of Unit" + "," + "Discount" + "," + "Items Total" + "," + "Unit Price" + "\n";
                                writer2.write(text);*/

                                index = 1;
                                for (int j = 0; j < items.size(); j++) {
                                    if (check[j])
                                        getsqlTransactionDetailsforCSV(j,writer2);
                                }
                                AlertBox.informationBox("Finish the export");

                            } catch (FileNotFoundException | NullPointerException ex){
                                AlertBox.informationBox("Cannot cover the opened file!");
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            finally {
                                writer1.flush();
                                writer2.flush();
                                writer1.close();
                                writer2.close();
                            }


                        }
                    }
                }

            }else{
                AlertBox.informationBox("You doesn't select the record");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getTransactionsData(int y, XSSFSheet sheet) throws SQLException {
        items = transactionTable.getItems();
        String query = "Select * from TRANSACTIONS ";
        boolean andA = false;
        if (!items.get(y).getTid().equals("")) {
            if (!andA) {
                query += " where Transactions.TransactionID = " + items.get(y).getTid();
                andA = true;
            } else {
                query += " and Transactions.TransactionID = " +items.get(y).getTid();
            }
        }
        if (!items.get(y).getCid().equals("")) {
            if (!andA) {
                query += " where Transactions.CustomerID = " + items.get(y).getCid();
                andA = true;
            } else {
                query += " and Transactions.CustomerID = " + items.get(y).getCid();
            }
        }
        if (!items.get(y).getTranAmt().equals("")) {
            if (!andA) {
                query += " where Transactions.TransactionAmount = " + items.get(y).getTranAmt();
                andA = true;
            } else {
                query += " and Transactions.TransactionAmount = " + items.get(y).getTranAmt();
            }
        }
        if (!items.get(y).getDisAmt().equals("")) {
            if (!andA) {
                query += " where Transactions.DiscountAmount = " + items.get(y).getDisAmt();
                andA = true;
            } else {
                query += " and Transactions.DiscountAmount = " + items.get(y).getDisAmt();
            }
        }
        if (!items.get(y).getNetAmt().equals("")) {
            if (!andA) {
                query += " where Transactions.NetAmount = " + items.get(y).getNetAmt();
                andA = true;
            } else {
                query += " and Transactions.NetAmount = " + items.get(y).getNetAmt();
            }
        }
        if (items.get(y).getLocation() != null && items.get(y).getLocation() != "") {
            if (!andA) {
                query += " where Transactions.Location = '" + items.get(y).getLocation() + "'";
                andA = true;
            } else {
                query += " and Transactions.Location = '" + items.get(y).getLocation() + "'";
            }
        }
        if (!(items.get(y).getTranDT() == null)) {
            if (!andA) {
                query += " where Transactions.TransactionDateTime LIKE '%" + items.get(y).getTranDT() + "%'";
                andA = true;
            } else {
                query += " and Transactions.TransactionDateTime LIKE '%" + items.get(y).getTranDT() + "%'";
            }
        }

        conn =  dbConnection.getConntection();
        PreparedStatement stmt = null;
        ResultSet rs = null;


        conn = dbConnection.getConntection();
        stmt = conn.prepareStatement(query);
        rs = stmt.executeQuery();
        while(rs.next()){
            XSSFRow row = sheet.createRow(index);
            row.createCell(0).setCellValue(rs.getString("TransactionID"));
            row.createCell(1).setCellValue(rs.getString("CustomerID"));
            row.createCell(2).setCellValue(rs.getString("Location"));
            row.createCell(3).setCellValue(rs.getString("TransactionDateTime"));
            row.createCell(4).setCellValue(rs.getString("TransactionAmount"));
            row.createCell(5).setCellValue(rs.getString("DiscountAmount"));
            row.createCell(6).setCellValue(rs.getString("NetAmount"));
            index++;
        }

        stmt.close();
        rs.close();
        conn.close();
    }

    public void getTransactionDetailData(int y, XSSFSheet sheet) throws SQLException {
        items = transactionTable.getItems();
        String query = "Select * from TransactionDetail";
        boolean andA = false;
        if (!items.get(y).getTid().equals("")) {
            if (!andA) {
                query += " where TransactionDetail.TransactionID = " + items.get(y).getTid();
                andA = true;
            } else {
                query += " and TransactionDetail.TransactionID = " +items.get(y).getTid();
            }
        }

        conn =  dbConnection.getConntection();
        PreparedStatement stmt = null;
        ResultSet rs = null;


        conn = dbConnection.getConntection();
        stmt = conn.prepareStatement(query);
        rs = stmt.executeQuery();
        while(rs.next()){
            XSSFRow row = sheet.createRow(index);
            row.createCell(0).setCellValue(rs.getString("TransactionID"));
            row.createCell(1).setCellValue(rs.getString("ItemID"));
            row.createCell(2).setCellValue(rs.getString("NumberOfUnit"));
            row.createCell(3).setCellValue(rs.getString("Discount"));
            row.createCell(4).setCellValue(rs.getString("ItemsTotal"));
            row.createCell(5).setCellValue(rs.getString("UnitPrice"));
            index++;
        }

        stmt.close();
        rs.close();
        conn.close();
    }


    public void getsqlTransactionforCSV(int y, Writer writer) throws SQLException, IOException {
        items = transactionTable.getItems();
        String query = "Select * from TRANSACTIONS ";
        boolean andA = false;
        if (!items.get(y).getTid().equals("")) {
            if (!andA) {
                query += " where Transactions.TransactionID = " + items.get(y).getTid();
                andA = true;
            } else {
                query += " and Transactions.TransactionID = " +items.get(y).getTid();
            }
        }
        if (!items.get(y).getCid().equals("")) {
            if (!andA) {
                query += " where Transactions.CustomerID = " + items.get(y).getCid();
                andA = true;
            } else {
                query += " and Transactions.CustomerID = " + items.get(y).getCid();
            }
        }
        if (!items.get(y).getTranAmt().equals("")) {
            if (!andA) {
                query += " where Transactions.TransactionAmount = " + items.get(y).getTranAmt();
                andA = true;
            } else {
                query += " and Transactions.TransactionAmount = " + items.get(y).getTranAmt();
            }
        }
        if (!items.get(y).getDisAmt().equals("")) {
            if (!andA) {
                query += " where Transactions.DiscountAmount = " + items.get(y).getDisAmt();
                andA = true;
            } else {
                query += " and Transactions.DiscountAmount = " + items.get(y).getDisAmt();
            }
        }
        if (!items.get(y).getNetAmt().equals("")) {
            if (!andA) {
                query += " where Transactions.NetAmount = " + items.get(y).getNetAmt();
                andA = true;
            } else {
                query += " and Transactions.NetAmount = " + items.get(y).getNetAmt();
            }
        }
        if (items.get(y).getLocation() != null && items.get(y).getLocation() != "") {
            if (!andA) {
                query += " where Transactions.Location = '" + items.get(y).getLocation() + "'";
                andA = true;
            } else {
                query += " and Transactions.Location = '" + items.get(y).getLocation() + "'";
            }
        }
        if (!(items.get(y).getTranDT() == null)) {
            if (!andA) {
                query += " where Transactions.TransactionDateTime LIKE '%" + items.get(y).getTranDT() + "%'";
                andA = true;
            } else {
                query += " and Transactions.TransactionDateTime LIKE '%" + items.get(y).getTranDT() + "%'";
            }
        }

        conn =  dbConnection.getConntection();
        PreparedStatement stmt = null;
        ResultSet rs = null;


        conn = dbConnection.getConntection();
        stmt = conn.prepareStatement(query);
        rs = stmt.executeQuery();
        while(rs.next()){
            String text = rs.getString("TransactionID") + "," + rs.getString("CustomerID") + "," +
                    rs.getString("Location")+ "," + rs.getString("TransactionDateTime")+ ","
                    + rs.getString("TransactionAmount")+ "," + rs.getString("DiscountAmount")+ "," +rs.getString("NetAmount") + "\n";
            writer.write(text);
        }
        stmt.close();
        rs.close();
        conn.close();
    }

    public void getsqlTransactionDetailsforCSV(int y, Writer writer) throws SQLException, IOException {
        items = transactionTable.getItems();
        String query = "Select * from TransactionDetail ";
        boolean andA = false;
        if (!items.get(y).getTid().equals("")) {
            if (!andA) {
                query += " where TransactionDetail.TransactionID = " + items.get(y).getTid();
                andA = true;
            } else {
                query += " and TransactionDetail.TransactionID = " +items.get(y).getTid();
            }
        }

        conn =  dbConnection.getConntection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        conn = dbConnection.getConntection();
        stmt = conn.prepareStatement(query);
        rs = stmt.executeQuery();
        while(rs.next()){
            String text = rs.getString("TransactionID") + "," + rs.getString("ItemID") + "," +
                    rs.getString("NumberOfUnit")+ "," + rs.getString("Discount")+ "," +
                    rs.getString("ItemsTotal") + "," +rs.getString("UnitPrice")+ "\n";
            writer.write(text);
        }
        stmt.close();
        rs.close();
        conn.close();
    }

    public void clearField() {
        tidInput.clear();
        cidInput.clear();
        tranAmtInput.clear();
        disAmtInput.clear();
        netAmtInput.clear();
        tranDtInput.setValue(null);
        locationInput.setValue(null);
        paymentMethodInput.setValue(null);
    }

    public void loadData() throws SQLException {
        conn = dbConnection.getConntection();
        this.data = FXCollections.observableArrayList();
        String sql = "SELECT Transactions.* FROM Transactions";
        ResultSet rs = conn.createStatement().executeQuery(sql);

        while (rs.next()) {
            this.data.add(new TransactionData(rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)
            ));
        }
        conn.close();
        rs.close();

        conn = dbConnection.getConntection();
        Iterator<TransactionData> itr = data.iterator();
        TransactionData ref = itr.hasNext() ? itr.next() : null;
        while(ref!=null){
            String sql1 = "SELECT Method FROM Payment Where TransactionID = " + ref.getTid();
            ResultSet rs1 = conn.createStatement().executeQuery(sql1);
            while (rs1.next()) {
                ref.setPayment(rs1.getString(1));
            }
            rs1.close();
            ref = itr.hasNext() ? itr.next() : null;
        }
        conn.close();
        setTableData();
    }

    public void logOut(ActionEvent event) throws Exception {
        super.logOut(exitButton);
        transactionModel.exitDatabase();
    }

    //go to modify data window
    //right click to the edit
    @FXML
    public void handlEditOption(ActionEvent event) throws IOException {
        TransactionData selectedForEdit = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            return;
        }
        thisTid = Integer.parseInt(selectedForEdit.getTid());
        switchEdit(selectedForEdit);
    }

    public void switchEdit(TransactionData selectedForEdit) throws IOException{
        Stage editStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/transaction/transactionEdit/edit.fxml").openStream());
        transaction.transactionEdit.EditController editController = (transaction.transactionEdit.EditController)loader.getController();
        editController.inflateUI(selectedForEdit);
        Scene scene = new Scene(root);
        editStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        editStage.setScene(scene);
        editStage.setTitle("Inventory System");
        editStage.setResizable(false);
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.show();
    }

    public void switchAdd(ActionEvent event) throws IOException {
        Stage addStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane) loader.load(getClass().getResource("/transaction/transactionAdd/add.fxml").openStream());
        transaction.transactionAdd.AddController addController = (transaction.transactionAdd.AddController) loader.getController();
        Scene scene = new Scene(root);
        addStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        addStage.setScene(scene);
        addStage.setTitle("Inventory System");
        addStage.setResizable(false);
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.show();
    }

    public void filterData(ActionEvent event)throws SQLException {
        boolean filterPayment = false;
        try {
            if (!tidInput.getText().equals("")) {
                if (!andT) {
                    sql += " where Transactions.TransactionID = " + tidInput.getText();
                    andT = true;
                } else {
                    sql += " and Transactions.TransactionID = " + tidInput.getText();
                }
            }
            if (!cidInput.getText().equals("")) {
                if (!andT) {
                    sql += " where Transactions.CustomerID = " + cidInput.getText();
                    andT = true;
                } else {
                    sql += " and Transactions.CustomerID = " + cidInput.getText();
                }
            }
            if (!tranAmtInput.getText().equals("")) {
                if (!andT) {
                    sql += " where Transactions.TransactionAmount = " + tranAmtInput.getText();
                    andT = true;
                } else {
                    sql += " and Transactions.TransactionAmount = " + tranAmtInput.getText();
                }
            }
            if (!disAmtInput.getText().equals("")) {
                if (!andT) {
                    sql += " where Transactions.DiscountAmount = " + disAmtInput.getText();
                    andT = true;
                } else {
                    sql += " and Transactions.DiscountAmount = " + disAmtInput.getText();
                }
            }
            if (!netAmtInput.getText().equals("")) {
                if (!andT) {
                    sql += " where Transactions.NetAmount = " + netAmtInput.getText();
                    andT = true;
                } else {
                    sql += " and Transactions.NetAmount = " + netAmtInput.getText();
                }
            }
            if (locationInput.getSelectionModel().getSelectedItem() != null && locationInput.getSelectionModel().getSelectedItem() != "") {
                if (!andT) {
                    sql += " where Transactions.Location = '" + locationInput.getSelectionModel().getSelectedItem() + "'";
                    andT = true;
                } else {
                    sql += " and Transactions.Location = '" + locationInput.getSelectionModel().getSelectedItem() + "'";
                }
            }

            if (!(tranDtInput.getValue() == null)) {
                if (!andT) {
                    sql += " where Transactions.TransactionDateTime LIKE '%" + tranDtInput.getValue() + "%'";
                    andT = true;
                } else {
                    sql += " and Transactions.TransactionDateTime LIKE '%" + tranDtInput.getValue() + "%'";
                }
            }

            conn = dbConnection.getConntection();
            this.data = FXCollections.observableArrayList();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                this.data.add(new TransactionData(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)
                ));
            }
            conn.close();

            conn = dbConnection.getConntection();
            Iterator<TransactionData> itr = data.iterator();
            TransactionData ref = itr.hasNext() ? itr.next() : null;
            while(ref!=null){
                String sql1 = "SELECT Method FROM Payment Where TransactionID = " + ref.getTid();
                ResultSet rs1 = conn.createStatement().executeQuery(sql1);
                while (rs1.next()) {
                    ref.setPayment(rs1.getString(1));
                }
                rs1.close();
                ref = itr.hasNext() ? itr.next() : null;
            }
            conn.close();
            this.others = FXCollections.observableArrayList();
            if(!(paymentMethodInput.getSelectionModel().getSelectedItem()==null||paymentMethodInput.getSelectionModel().getSelectedItem().equals(""))) {
                for(TransactionData i : data){
                    if (paymentMethodInput.getSelectionModel().getSelectedItem().equals(i.getPayment()))
                        this.others.add(i);
                }
                setTableData(others);
                filterPayment =true;
            }

            andT = false;
            conn.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            sql = "SELECT TRANSACTIONS.* FROM Transactions";
            andT = false;
        }
        if(!filterPayment)
            setTableData();
    }

    public File selectPath(String name) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        String userDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userDir +"/Desktop"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel(*.xlsx)", "*.xlsx"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV(*.csv)", "*.csv"));
        fileChooser.setInitialFileName(name+LocalDateTime.now());
        File selectedDirectory = fileChooser.showSaveDialog(stage);
        return selectedDirectory;
    }

    public File selectPathForCSV(String name) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        String userDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userDir +"/Desktop"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV(*.csv)", "*.csv"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        fileChooser.setInitialFileName(name+ LocalDateTime.now().format(format));
        File selectedDirectory = fileChooser.showSaveDialog(stage);
        return selectedDirectory;
    }

    //loop the combobox of location
    public void loopCombo(){
        locationInput.getItems().add("");
        try{
            String sql = "SELECT Location.name FROM Location";
            conn = dbConnection.getConntection();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String locationName = rs.getString(1);
                locationInput.getItems().add(locationName);
            }
            conn.close();
            rs.close();
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }

    }

    //loop the combobox of payment
    public void loopPaymentCombo(){
        paymentMethodInput.getItems().add("");
        try{
            String sql = "SELECT PaymentMethod.name FROM PaymentMethod";
            conn = dbConnection.getConntection();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String paymentName = rs.getString(1);
                paymentMethodInput.getItems().add(paymentName);
            }
            conn.close();
            rs.close();
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }

    }

    public void setTableData(){
        this.col_tid.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("tid"));
        this.col_cid.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("cid"));
        this.col_Location.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("location"));
        this.col_TranDT.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("tranDT"));
        this.col_TranAmt.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("tranAmt"));
        this.col_DisAmt.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("disAmt"));
        this.col_NetAmt.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("netAmt"));
        this.col_Payment.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("payment"));
        this.col_sel.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("remark"));

        this.transactionTable.setItems(null);
        this.transactionTable.setItems(this.data);
    }

    public void setTableData(ObservableList<TransactionData> a){
        this.col_tid.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("tid"));
        this.col_cid.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("cid"));
        this.col_Location.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("location"));
        this.col_TranDT.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("tranDT"));
        this.col_TranAmt.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("tranAmt"));
        this.col_DisAmt.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("disAmt"));
        this.col_NetAmt.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("netAmt"));
        this.col_Payment.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("payment"));
        this.col_sel.setCellValueFactory(new PropertyValueFactory<TransactionData, String>("remark"));

        this.transactionTable.setItems(null);
        this.transactionTable.setItems(a);
    }
}
