package payment;

import common.*;
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
import promotion.PromotionController;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class PaymentController extends PageController implements Initializable, PageGuide {
    PageModel paymentModel = new PageModel();
    Connection conn;

    int index=0;
    public boolean andT = false;
    private String sql = "SELECT Payment.* FROM Payment";
    @FXML
    private Label staffName, staffID,dbStatus;
    @FXML
    private ImageView staffIcon;
    @FXML
    private Button paymentButton, exitButton;
    @FXML
    private TextField tidInput, payAmtInput;
    @FXML
    private ComboBox payMethodInput;
    @FXML
    private DatePicker transactionDateInput;
    @FXML
    private BorderPane basicScene;
    @FXML
    private TableView<PaymentData> paymentTable;
    @FXML
    private TableColumn<PaymentData, String> col_tid, col_PayMethod,col_PayAmount, col_TransactionDate, col_sel;

    private ObservableList<PaymentData> data, items;


    private static PaymentController instance = null;

    public PaymentController(){
        instance = this;
    }

    public static PaymentController getInstance(){
        return instance;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/resources/stafficon/"+staff+".png");
        Image image = new Image(file.toURI().toString());
        staffIcon.setImage(image);

        if(paymentModel.isDatabaseConnected()){
            dbStatus.setText("Connected");
            dbStatus.setTextFill(Color.GREEN);
        }else{
            dbStatus.setText("Not Connected");
            dbStatus.setTextFill(Color.RED);
        }

        paymentButton.setDisable(true);

        try {
            setStaff(staff, staffName, staffID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        paymentMethodLoopCombo();
        new AutoCompleteBox(payMethodInput);
    }

    public void deleteRecord(ActionEvent event) throws Exception {
        conn = null;
        Statement stmt = null;

        items = paymentTable.getItems();
        try {
            for (PaymentData item : items) {
                if (item.getRemark().isSelected()) {
                    String sqlD = "Delete from payment Where Payment.TransactionID = " + item.getTid() + " and Payment.Method = '"
                            + item.getPaymentMethod() + "' and Payment.Amount = " + item.getPaymentAmount()+ " and Payment.TransactionDate = '" + item.getTransactionDate() + "'";
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
        Pane root = (Pane)loader.load(getClass().getResource("/payment/paymentImport/importPage.fxml").openStream());
        payment.paymentImport.ImportController importController = (payment.paymentImport.ImportController)loader.getController();
        Scene scene = new Scene(root);
        editStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        editStage.setScene(scene);
        editStage.setTitle("Inventory System");
        editStage.setResizable(false);
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.show();
    }

    public void exportToExcel(ActionEvent event) throws SQLException, IOException {
        File selectedDirectory = selectPath("PaymentMethod");
        if (selectedDirectory == null) {
            //No Directory selected
        } else {
            String fileTypeName = selectedDirectory.getName();
            String[] parts = fileTypeName.split(Pattern.quote("."));
            String part2 = parts[1];
            conn = dbConnection.getConntection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            String query = "Select * from payment";
            if (part2.toLowerCase().equals("xlsx")) {
                try {
                    stmt = conn.prepareStatement(query);
                    rs = stmt.executeQuery();

                    XSSFWorkbook wb = new XSSFWorkbook();//for earlier version use HSSF
                    XSSFSheet sheet = wb.createSheet("Payment Details");
                    XSSFRow header = sheet.createRow(0);
                    header.createCell(0).setCellValue("TransactionID");
                    header.createCell(1).setCellValue("Payment Method");
                    header.createCell(2).setCellValue("Payment Amount");
                    header.createCell(3).setCellValue("Transaction Date");

                    sheet.autoSizeColumn(1);
                    sheet.autoSizeColumn(2);
                    sheet.autoSizeColumn(3);
                    sheet.autoSizeColumn(4);
                    sheet.autoSizeColumn(5);


                    sheet.setZoom(150);

                    int index = 1;
                    while (rs.next()) {
                        XSSFRow row = sheet.createRow(index);
                        row.createCell(0).setCellValue(rs.getString("TransactionID"));
                        row.createCell(1).setCellValue(rs.getString("Method"));
                        row.createCell(2).setCellValue(rs.getString("Amount"));
                        row.createCell(3).setCellValue(rs.getString("TransactionDate"));

                        index++;
                    }
                    FileOutputStream fileOut = new FileOutputStream(selectedDirectory.getAbsolutePath());//before 2007 version xls
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
                    Writer writer = null;
                    try {
                        File file = new File(selectedDirectory.getAbsolutePath());
                        writer = new BufferedWriter(new FileWriter(file));
                        stmt = conn.prepareStatement(query);
                        rs = stmt.executeQuery();
                        while (rs.next()) {
                            String text = rs.getString("TransactionID") + "," + rs.getString("Method")
                                    + "," + rs.getString("Amount") + "," + rs.getString("TransactionDate") + "\n";
                            writer.write(text);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        writer.flush();
                        writer.close();
                    }
                }
            }
        }
        conn.close();
    }

    public void dataToExcel(ActionEvent event) throws SQLException{

        items = paymentTable.getItems();

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
                File selectedDirectory = selectPath("PaymentMethod");
                if(selectedDirectory == null){
                    //No Directory selected
                }else {
                    String fileTypeName = selectedDirectory.getName();
                    String[] parts = fileTypeName.split(Pattern.quote("."));
                    String part2 = parts[1];
                    if(part2.toLowerCase().equals("xlsx")) {
                        try {
                            XSSFWorkbook wb = new XSSFWorkbook();//for earlier version use HSSF
                            XSSFSheet sheet = wb.createSheet("Payment Details");
                            XSSFRow header = sheet.createRow(0);
                            header.createCell(0).setCellValue("TransactionID");
                            header.createCell(1).setCellValue("Payment Method");
                            header.createCell(2).setCellValue("Payment Amount");
                            header.createCell(3).setCellValue("Transaction Date");

                            sheet.autoSizeColumn(1);
                            sheet.autoSizeColumn(2);
                            sheet.autoSizeColumn(3);

                            sheet.setZoom(150);

                            index = 1;
                            for (int j = 0; j < items.size(); j++) {
                                if (check[j])
                                    getsqlData(j, sheet);
                            }

                            FileOutputStream fileOut = new FileOutputStream(selectedDirectory.getAbsolutePath());//before 2007 version xls
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
                            Writer writer = null;
                            File file = new File(selectedDirectory.getAbsolutePath());
                            writer = new BufferedWriter(new FileWriter(file));
                            try {
                                for (int j = 0; j < items.size(); j++) {
                                    if (check[j])
                                        getsqlDataforCSV(j, writer);
                                }
                            } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            writer.flush();
                            writer.close();
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

    public void getsqlData(int y, XSSFSheet sheet) throws SQLException {
        items = paymentTable.getItems();
        String query = "Select * from payment Where ";
        query +="Payment.TransactionID = " + items.get(y).getTid() +" and Payment.Method = '"
                +items.get(y).getPaymentMethod()+"' and Payment.Amount = "+ items.get(y).getPaymentAmount()
                +" and Payment.TransactionDate = '"+ items.get(y).getTransactionDate()+"' ";
        System.out.println(query);
        conn =  dbConnection.getConntection();
        PreparedStatement stmt = null;
        ResultSet rs = null;


        conn = dbConnection.getConntection();
        stmt = conn.prepareStatement(query);
        rs = stmt.executeQuery();
        while(rs.next()){
            XSSFRow row = sheet.createRow(index);
            row.createCell(0).setCellValue(rs.getString("TransactionID"));
            row.createCell(1).setCellValue(rs.getString("Method"));
            row.createCell(2).setCellValue(rs.getString("Amount"));
            row.createCell(3).setCellValue(rs.getString("TransactionDate"));
            index++;
        }
        stmt.close();
        rs.close();
        conn.close();
    }

    public void getsqlDataforCSV(int y, Writer writer) throws SQLException, IOException {
        items = paymentTable.getItems();
        String query = "Select * from payment Where ";
        query +="Payment.TransactionID = " + items.get(y).getTid() +" and Payment.Method = '"
                +items.get(y).getPaymentMethod()+"' and Payment.Amount = "+ items.get(y).getPaymentAmount()
                +" and Payment.TransactionDate = '"+ items.get(y).getTransactionDate()+"'" ;

        conn =  dbConnection.getConntection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        stmt = conn.prepareStatement(query);
        rs = stmt.executeQuery();
        while(rs.next()) {
            String text = rs.getString("TransactionID") + "," + rs.getString("Method") + "," + rs.getString("Amount") + "," + rs.getString("TransactionDate") +"\n";
            writer.write(text);
        }

        stmt.close();
        rs.close();
        conn.close();
    }

    public void clearField() {
        tidInput.clear();
        payAmtInput.clear();
        payMethodInput.setValue(null);
        transactionDateInput.setValue(null);
    }

    public void loadData() throws SQLException{
        try{
            conn = dbConnection.getConntection();
            this.data = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                this.data.add(new PaymentData(rs.getString(1),rs.getString(2),rs.getString(3), rs.getString(4)));
            }
            sql = "SELECT Payment.* FROM Payment";
            andT = false;
            conn.close();
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }

        setTableData();
    }

    public void logOut(ActionEvent event) throws Exception {
        super.logOut(exitButton);
        paymentModel.exitDatabase();
    }

    public void handlEditOption(ActionEvent event) throws IOException {
        PaymentData selectedForEdit = paymentTable.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            return;
        }
        switchEdit(selectedForEdit);
    }

    public void switchEdit(PaymentData selectedForEdit) throws IOException{
        Stage editStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/payment/paymentEdit/edit.fxml").openStream());
        payment.paymentEdit.EditController editController = (payment.paymentEdit.EditController)loader.getController();
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
        Pane root = (Pane) loader.load(getClass().getResource("/payment/paymentAdd/add.fxml").openStream());
        payment.paymentAdd.AddController addController = (payment.paymentAdd.AddController) loader.getController();
        Scene scene = new Scene(root);
        addStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        addStage.setScene(scene);
        addStage.setTitle("Inventory System");
        addStage.setResizable(false);
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.show();
    }

    public void filterData(ActionEvent event)throws SQLException{
        try{
            if (!tidInput.getText().equals("")){
                if(!andT){
                    sql+= " where Payment.TransactionID = " + tidInput.getText();
                    andT = true;
                }else {
                    sql += " and Payment.TransactionID = " + tidInput.getText();
                }
            }

            if (payMethodInput.getSelectionModel().getSelectedItem() != null && payMethodInput.getSelectionModel().getSelectedItem() != ""){
                if(!andT){
                    sql+= " where Payment.Method LIKE '%" + payMethodInput.getValue() +"%'";
                    andT = true;
                }else {
                    sql += " and Payment.Method LIKE '%" + payMethodInput.getValue() +"%'";
                }
            }
            if (!payAmtInput.getText().equals("")){
                if(!andT){
                    sql+= " where Payment.Amount LIKE '%" + payAmtInput.getText() +"%'";
                    andT = true;
                }else {
                    sql += " and Payment.Amount LIKE '%" + payAmtInput.getText() + "%'";
                }
            }
            if (!(transactionDateInput.getValue() == null)) {
                if (!andT) {
                    sql += " where Payment.TransactionDate LIKE '%" + transactionDateInput.getValue() + "%'";
                    andT = true;
                } else {
                    sql += " and Payment.TransactionDate LIKE '%" + transactionDateInput.getValue() + "%'";
                }
            }


            conn = dbConnection.getConntection();
            this.data = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                this.data.add(new PaymentData(rs.getString(1),rs.getString(2),rs.getString(3), rs.getString(4)));
            }
            sql = "SELECT Payment.* FROM Payment";
            andT = false;

        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }finally {
            sql = "SELECT Payment.* FROM Payment";
            andT = false;
            conn.close();
        }
        setTableData();
    }

    public File selectPath(String name) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        String userDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userDir +"/Desktop"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel(*.xlsx)", "*.xlsx"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV(*.csv)", "*.csv"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        fileChooser.setInitialFileName("Payment"+ LocalDateTime.now().format(format));
        stage.centerOnScreen();
        File selectedDirectory = fileChooser.showSaveDialog(stage);
        return selectedDirectory;
    }

    public void setTableData(){
        this.col_tid.setCellValueFactory(new PropertyValueFactory<PaymentData, String>("tid"));
        this.col_PayMethod.setCellValueFactory(new PropertyValueFactory<PaymentData, String>("paymentMethod"));
        this.col_PayAmount.setCellValueFactory(new PropertyValueFactory<PaymentData, String>("paymentAmount"));
        this.col_TransactionDate.setCellValueFactory(new PropertyValueFactory<PaymentData, String>("transactionDate"));

        this.col_sel.setCellValueFactory(new PropertyValueFactory<PaymentData, String>("remark"));

        this.paymentTable.setItems(null);
        this.paymentTable.setItems(this.data);
    }

    public void paymentMethodLoopCombo(){
        try{
            payMethodInput.getItems().add("");
            conn = dbConnection.getConntection();
            String sql = "SELECT PaymentMethod.name FROM PaymentMethod";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String countryName = rs.getString(1);
                payMethodInput.getItems().add(countryName);
            }
            conn.close();
            rs.close();

        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }

    }
}