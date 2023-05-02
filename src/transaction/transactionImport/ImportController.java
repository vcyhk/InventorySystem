package transaction.transactionImport;

import common.AlertBox;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import transaction.*;


public class ImportController implements Initializable {
    Connection conn;
    @FXML
    private TableView<ImportData> importTable;
    @FXML
    private TableColumn<ImportData, String> col_TID, col_CID, col_Location, col_TransactionDate, col_TransactionAmount, col_DiscountAmount, col_NetAmount, col_Payment;
    @FXML
    private Label filename;
    @FXML
    private Label label_transHeader, filename_detail;
    @FXML
    private Button selectBtn, selectDetailBtn;
    @FXML
    private HBox selectFileArea1;
    @FXML
    private HBox selectFileArea2;
    @FXML
    private VBox importScene;
    public boolean useXlsx = false;


    private ObservableList<ImportData> data, itemInsert, itemUpdate;
    private ObservableList<ImportDetailData> dataDetail, itemDetailInsert, itemDetailUpdate;
    private List<String> tidExist = new ArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.data = FXCollections.observableArrayList();
        selectFileArea1.setVisible(false);
        selectFileArea2.setVisible(false);
    }

    public void selectFile(ActionEvent Event) throws Exception {
        if(useXlsx) {
            Stage stage = new Stage();
            FileChooser fileChooser = new FileChooser();
            String userDir = System.getProperty("user.home");
            fileChooser.setInitialDirectory(new File(userDir + "/Desktop"));
            File selectedDirectory = fileChooser.showOpenDialog(stage);
            if (selectedDirectory == null) {
                //No Directory selected
            } else {
                String fileTypeName = selectedDirectory.getName();
                String[] parts = fileTypeName.split(Pattern.quote("."));
                String part2 = parts[1];
                if (part2.toLowerCase().equals("xlsx")) {
                    filename.setText(fileTypeName);
                    openExcel(selectedDirectory);
                    saveTransactionDetail(selectedDirectory);
                    tableData();
                } else {

                    AlertBox.informationBox("Wrong file format. Allowed: XLSX.");

                }
            }
        }else
            selectCsvFile();
    }

    public void selectCsvFile() throws Exception {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        String userDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userDir +"/Desktop"));
        File selectedDirectory = fileChooser.showOpenDialog(stage);
        if(selectedDirectory == null){
            //No Directory selected
        }else {
            String fileTypeName = selectedDirectory.getName();
            String[] parts = fileTypeName.split(Pattern.quote("."));
            String part2 = parts[1];
            if(part2.toLowerCase().equals("csv")){
                filename.setText(fileTypeName);
                openCSV(selectedDirectory);
                tableData();
            }else {
                AlertBox.informationBox("Wrong file format. Allowed: CSV.");
            }
        }
    }

    public void selectCsvDetailFile(ActionEvent Event) throws Exception {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        String userDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userDir +"/Desktop"));
        File selectedDirectory = fileChooser.showOpenDialog(stage);
        if(selectedDirectory == null){
            //No Directory selected
        }else {
            String fileTypeName = selectedDirectory.getName();
            String[] parts = fileTypeName.split(Pattern.quote("."));
            String part2 = parts[1];

            if(part2.toLowerCase().equals("csv")){
                filename_detail.setText(fileTypeName);
                openDetailCSV(selectedDirectory);
            }else {
                AlertBox.informationBox("Wrong file format. Allowed: CSV.");
            }
        }
    }

    public void openExcel(File selectedDirectory) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(selectedDirectory);
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
            Iterator<Row> itr = sheet.iterator();    //iterating over excel file
            this.data = FXCollections.observableArrayList();
            while (itr.hasNext()) {
                Row row = itr.next();
                Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
                int colNum = 0;
                String[] excelData = new String[13];
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (row.getRowNum() == 0)
                        continue;
                    if (cell.equals("")) {
                        excelData[cell.getColumnIndex()] = null;
                    } else{
                        excelData[cell.getColumnIndex()] = String.valueOf(cell);
                    }

                    colNum++;
                }
                if (colNum == 7) {
                    this.data.add(new ImportData(excelData[0], excelData[1], excelData[2], excelData[3], excelData[4], excelData[5], excelData[6]));
                    System.out.println(data.get(data.size()-1).toString());
                }
            }
            fis.close();

        }catch (Exception e){System.err.println(e);}
    }

    public void saveTransactionDetail(File selectedDirectory) throws IOException {
        FileInputStream fis = new FileInputStream(selectedDirectory);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        //System.out.println(wb.getSheetIndex("Transaction Details"));
        XSSFSheet sheet = wb.getSheetAt(1);     //creating a Sheet object to retrieve object
        //System.out.println(sheet.getSheetName());
        Iterator<Row> itr = sheet.iterator();    //iterating over excel file
        this.dataDetail = FXCollections.observableArrayList();
        while (itr.hasNext()) {
            Row row = itr.next();
            Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
            int colNum = 0;
            String[] excelData = new String[13];
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                if (row.getRowNum() == 0)
                    continue;
                if (cell.equals("")) {
                    excelData[cell.getColumnIndex()] = null;
                } else {
                    excelData[cell.getColumnIndex()] = String.valueOf(cell);
                }
                colNum++;
            }
            if (colNum == 6) {
                this.dataDetail.add(new ImportDetailData(excelData[0], excelData[1], excelData[2], excelData[3], excelData[4], excelData[5]));
                System.out.println(dataDetail.get(dataDetail.size()-1).toString());
            }
        }
        fis.close();
    }

    public void openCSV(File selectedDirectory) throws IOException {
        this.data = FXCollections.observableArrayList();
        BufferedReader br = new BufferedReader(new FileReader(selectedDirectory));
        String line = "";
        String cvsSplitBy = ",";
        while ((line = br.readLine()) != null) {
            // use comma as separator
            String[] csvData = line.split(cvsSplitBy);
            if(csvData.length==7){
                this.data.add(new ImportData(csvData[0],csvData[1],csvData[2],csvData[3],csvData[4],csvData[5],csvData[6]));
                System.out.println(data.get(data.size()-1).toString());
            }

        }
    }

    public void openDetailCSV(File selectedDirectory) throws IOException {
        this.dataDetail = FXCollections.observableArrayList();
        BufferedReader br = new BufferedReader(new FileReader(selectedDirectory));
        String line = "";
        String cvsSplitBy = ",";
        while ((line = br.readLine()) != null) {
            // use comma as separator
            String[] csvData = line.split(cvsSplitBy);
            if(csvData.length==6){
                this.dataDetail.add(new ImportDetailData(csvData[0], csvData[1], csvData[2], csvData[3], csvData[4], csvData[5]));
                System.out.println(dataDetail.get(dataDetail.size()-1).toString());
            }
        }
    }

    public void xlsxSelected(ActionEvent Event) throws Exception {
        useXlsx = true;
        label_transHeader.setText("File Name :");
        selectFileArea1.setVisible(true);
        selectFileArea2.setVisible(false);
    }

    public void csvSelected(ActionEvent Event) throws Exception {
        useXlsx = false;
        label_transHeader.setText("File Name (Header):");
        selectFileArea1.setVisible(true);
        selectFileArea2.setVisible(true);
    }

    public void tableData(){
        this.col_TID.setCellValueFactory(new PropertyValueFactory<ImportData, String>("TID"));
        this.col_CID.setCellValueFactory(new PropertyValueFactory<ImportData, String>("CID"));
        this.col_Location.setCellValueFactory(new PropertyValueFactory<ImportData, String>("location"));
        this.col_TransactionDate.setCellValueFactory(new PropertyValueFactory<ImportData, String>("transactionDate"));
        this.col_TransactionAmount.setCellValueFactory(new PropertyValueFactory<ImportData, String>("transactionAmount"));
        this.col_DiscountAmount.setCellValueFactory(new PropertyValueFactory<ImportData, String>("discountAmount"));
        this.col_NetAmount.setCellValueFactory(new PropertyValueFactory<ImportData, String>("netAmount"));
        this.importTable.setItems(null);
        this.importTable.setItems(this.data);
    }

    public void importTransaction(ActionEvent event) throws Exception{
        this.itemInsert = FXCollections.observableArrayList();
        this.itemUpdate = FXCollections.observableArrayList();
        this.itemDetailInsert = FXCollections.observableArrayList();
        this.itemDetailUpdate = FXCollections.observableArrayList();
        int noOfExist = noofTidExist();
        boolean dontChange = false;
        if(noOfExist>0) {
            for (int i = 0; i < data.size(); i++) {
                if (isTIDExist(data.get(i).getTID())) {
                    openWindow(data.get(i).getTID(), noOfExist);
                    String a = WindowController.getData();
                    WindowController.setData(null);
                    try{
                        if (a.equals("overWrite")) {
                            this.itemUpdate.add(data.get(i));
                            for(ImportDetailData b : dataDetail){
                                if(b.getTransactionID().equals(data.get(i).getTID())){
                                    itemDetailUpdate.add(b);
                                }
                            }
                        } else if (a.equals("overWriteAll")) {
                            for (int k = i; k < data.size(); k++) {
                                this.itemUpdate.add(data.get(k));
                                for(ImportDetailData x : dataDetail){
                                    if(x.getTransactionID().equals(data.get(k).getTID())){
                                        itemDetailUpdate.add(x);
                                    }
                                }
                            }
                            break;
                        } else if (a.equals("skipAll")) {
                            break;
                        }
                    }catch (Exception e){
                        dontChange = true;
                        break;
                    }
                } else {
                    this.itemInsert.add(data.get(i));
                    for(ImportDetailData a : dataDetail){
                        if(a.getTransactionID().equals(data.get(i).getTID())){
                            itemDetailInsert.add(a);
                        }
                    }
                }
            }
        }
        if(!dontChange){
            insertTransaction();
            updateTransaction();
            closeWindow();
        }
    }



    private void insertTransaction() throws SQLException {
        //Insert the new data into database
        for (ImportData d: itemInsert) {
            String sqlInsert = "INSERT INTO Transactions(TransactionID, CustomerID, Location, TransactionDateTime, TransactionAmount, DiscountAmount, NetAmount) VALUES ( ?, ?, ?, ?, ?, ?, ?)";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, d.getTID());
                ps.setString(2, d.getCID());
                ps.setString(3, d.getLocation());
                ps.setString(4, d.getTransactionDate());
                ps.setString(5, d.getTransactionAmount());
                ps.setString(6, d.getDiscountAmount());
                ps.setString(7, d.getNetAmount());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (ImportDetailData m: itemDetailInsert) {
            String sqlInsert = "INSERT INTO TransactionDetail(TransactionID, ItemID, NumberOfUnit, Discount, ItemsTotal, UnitPrice) VALUES ( ?, ?, ?, ?, ?, ?)";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, m.getTransactionID());
                ps.setString(2, m.getItemID());
                ps.setString(3, m.getNumberOfUnit());
                ps.setString(4, m.getDiscount());
                ps.setString(5, m.getItemsTotal());
                ps.setString(6, m.getUnitPrice());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //Insert the new data into database, while the Product selected Keep "Both Records But Generate A New CID"
/*        for(ImportData d: itemCreate){
            long tidMax = Long.parseLong(maxTID());
            String sqlInsert = "INSERT INTO Product(ItemID, Category, Desc, UnitPrice, EffectiveFrom, EffectiveTo) VALUES ( ?, ?, ?, ?, ?, ?)";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, String.valueOf(++tidMax));
                ps.setString(2, d.getCID());
                ps.setString(3, d.getLocation());
                ps.setString(4, d.getTransactionDate());
                ps.setString(5, d.getDiscountAmount());
                ps.setString(6, d.getDiscountAmount());


                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }*/

    }

    private void updateTransaction() throws SQLException {
        //Update the data of the Transaction, which the tid exist.
        for (ImportData d: itemUpdate) {
            String sqlInsert = "UPDATE Transactions SET TransactionID = ?, CustomerID = ?, Location = ?, TransactionDateTime = ?, TransactionAmount = ?, DiscountAmount = ?, NetAmount = ? WHERE TransactionID = ?";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, d.getTID());
                ps.setString(2, d.getCID());
                ps.setString(3, d.getLocation());
                ps.setString(4, d.getTransactionDate());
                ps.setString(5, d.getTransactionAmount());
                ps.setString(6, d.getDiscountAmount());
                ps.setString(7, d.getNetAmount());
                ps.setString(8, d.getTID());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        for (ImportData y : itemUpdate){
            Statement stmt = null;
            String sqlInsert = "DELETE FROM TransactionDetail Where TransactionID = "+ y.getTID();
            conn = dbConnection.getConntection();
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlInsert);
            conn.close();
        }

        for (ImportDetailData m: itemDetailUpdate) {
            String sqlInsert = "INSERT INTO TransactionDetail(TransactionID, ItemID, NumberOfUnit, Discount, ItemsTotal, UnitPrice) VALUES ( ?, ?, ?, ?, ?, ?)";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, m.getTransactionID());
                ps.setString(2, m.getItemID());
                ps.setString(3, m.getNumberOfUnit());
                ps.setString(4, m.getDiscount());
                ps.setString(5, m.getItemsTotal());
                ps.setString(6, m.getUnitPrice());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    //check the # of the tid is exist
    public int noofTidExist() throws SQLException {
        int result  = 0;
        for(ImportData c : data){
            tidExist.add(c.getTID());
        }
        for(String tid : tidExist){
            conn = dbConnection.getConntection();
            String sql = "SELECT COUNT(*) AS RESULT FROM TRANSACTIONS WHERE TransactionID = " +  tid + ";";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (Integer.parseInt(rs.getString(1)) > 0) {
                result++;
            }
            conn.close();
            rs.close();
        }
        return result;
    }

    //check the tid whether exist
    public boolean isTIDExist(String tid) throws SQLException {
        conn = dbConnection.getConntection();
        String sql = "SELECT COUNT(*) AS RESULT FROM TRANSACTIONS WHERE TransactionID = " +  tid + ";";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        if (Integer.parseInt(rs.getString(1)) > 0) {
            conn.close();
            rs.close();
            return true;
        }
        conn.close();
        rs.close();
        return false;
    }

/*    //check the max. # of tid
    public String maxTID() throws SQLException {
        conn = dbConnection.getConntection();
        String sql = "SELECT MAX(TID) FROM TRANSACTION;";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        String a =rs.getString(1);
        conn.close();
        rs.close();
        return a;
    }*/

    public void closeWindow() throws Exception{
        TransactionController transactionController = TransactionController.getInstance();
        transactionController.loadData();
        Stage stage = (Stage) selectBtn.getScene().getWindow();
        stage.close();
    }

    //the select window to user, decide how do deal with the exist cid data
    public void openWindow(String tid, int total) throws IOException {
        Stage windowStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/transaction/transactionImport/importSelection.fxml").openStream());
        WindowController importController = (transaction.transactionImport.WindowController)loader.getController();
        importController.setTid(tid);
        importController.totalExist(String.valueOf(total));
        Scene scene = new Scene(root);
        windowStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        windowStage.setScene(scene);
        windowStage.setTitle("Inventory System");
        windowStage.setResizable(false);
        windowStage.initModality(Modality.APPLICATION_MODAL);
        windowStage.showAndWait(); //When a window is blocked by a modal stage its Z-order relative to its ancestors is preserved, and it receives no input events and no window activation events, but continues to animate and render normally.
    }

}


