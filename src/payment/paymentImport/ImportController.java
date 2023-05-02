package payment.paymentImport;

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
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import payment.PaymentController;
import payment.PaymentData;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ImportController implements Initializable {
    Connection conn;
    @FXML
    private TableView<ImportData> importTable;
    @FXML
    private TableColumn<ImportData, String> col_tid, col_PayMethod,col_PayAmount, col_TransactionDate;
    @FXML
    private Label filename;
    @FXML
    private Button importBtn;

    private ObservableList<ImportData> data;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.data = FXCollections.observableArrayList();
    }

    public void selectFile(ActionEvent Event) throws Exception {
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
            if (part2.toLowerCase().equals("xlsx")) {
                filename.setText(fileTypeName);
                openExcel(selectedDirectory);
                tableData();
            }else{
                if(part2.toLowerCase().equals("csv")){
                    filename.setText(fileTypeName);
                    openCSV(selectedDirectory);
                    tableData();
                }
                else{
                    AlertBox.informationBox("Wrong file format. Allowed: XLSX, CSV.");
                }
            }
            }
    }

    public void openExcel(File selectedDirectory) throws IOException {
        FileInputStream fis = new FileInputStream(selectedDirectory);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        XSSFSheet sheet = wb.getSheetAt(0);     //creating a Sheet object to retrieve object
        Iterator<Row> itr = sheet.iterator();    //iterating over excel file
        this.data = FXCollections.observableArrayList();
        while (itr.hasNext())
        {
            Row row = itr.next();
            Iterator<Cell> cellIterator = row.cellIterator();   //iterating over each column
            int colNum =0;
            String[] excelData = new String[11];
            while(cellIterator.hasNext()){
                Cell cell = cellIterator.next();
                if(row.getRowNum()==0)
                    continue;
                if(cell.equals("")){
                    excelData[cell.getColumnIndex()] = null;
                }else
                    excelData[cell.getColumnIndex()] = String.valueOf(cell);

                colNum++;
            }
            if(colNum==4) {
                this.data.add(new ImportData(excelData[0],excelData[1],excelData[2],excelData[3]));
            }
        }
        fis.close();
    }

    public void openCSV(File selectedDirectory) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(selectedDirectory));
        String line = "";
        String cvsSplitBy = ",";
        while ((line = br.readLine()) != null) {
            // use comma as separator
            String[] csvData = line.split(cvsSplitBy);
            if(csvData.length==4)
            this.data.add(new ImportData(csvData[0],csvData[1],csvData[2],csvData[3]));

        }
    }

    public void tableData(){
        this.col_tid.setCellValueFactory(new PropertyValueFactory<ImportData, String>("tid"));
        this.col_PayMethod.setCellValueFactory(new PropertyValueFactory<ImportData, String>("paymentMethod"));
        this.col_PayAmount.setCellValueFactory(new PropertyValueFactory<ImportData, String>("paymentAmount"));
        this.col_TransactionDate.setCellValueFactory(new PropertyValueFactory<ImportData, String>("transactionDate"));

        this.importTable.setItems(null);
        this.importTable.setItems(this.data);
    }

    public void importPayment(ActionEvent event) throws Exception{
        boolean dontChange = false;
        if(isTransactionIDExist()){
            openWindow();
            String a = payment.paymentImport.WindowController.getData();
            customer.customerImport.WindowController.setData(null);
            try{
               if (a.equals("overWriteAll")) {
                    dontChange = false;
                } else if (a.equals("skipAll")) {
                    dontChange = true;
                }
            }catch (Exception e){
                e.printStackTrace();
                dontChange = true;
            }
        }

        if(!dontChange){
            deleteAll();
            insertPayment();
            closeWindow();
        }
    }

    public void closeWindow() throws Exception{
        PaymentController paymentController = PaymentController.getInstance();
        paymentController.loadData();
        Stage stage = (Stage) importBtn.getScene().getWindow();
        stage.close();
    }

    private void insertPayment() throws SQLException {
        for(ImportData d : data){
            try {
                String sqlInsert = "INSERT INTO Payment(TransactionID, Method, Amount, TransactionDate) VALUES ( ?, ?, ?, ?)";
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, d.getTid());
                ps.setString(2, d.getPaymentMethod());
                ps.setString(3, d.getPaymentAmount());
                ps.setString(4, d.getTransactionDate());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isTransactionIDExist() throws SQLException {
        conn = dbConnection.getConntection();
        int result  = 0;
        for(ImportData c : data){
            conn = dbConnection.getConntection();
            String sql = "SELECT COUNT(*) AS RESULT FROM Payment WHERE TransactionID = " +  c.getTid() + ";";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (Integer.parseInt(rs.getString(1)) > 0) {
                result++;
            }
            conn.close();
            rs.close();
        }

        if(result > 0)
            return true;
        else
            return false;
    }

    //the select window to user, decide how do deal with the exist cid data
    public void openWindow() throws IOException {
        Stage windowStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/payment/paymentImport/importSelection.fxml").openStream());
        payment.paymentImport.WindowController importController = (WindowController)loader.getController();
        Scene scene = new Scene(root);
        windowStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        windowStage.setScene(scene);
        windowStage.setTitle("Inventory System");
        windowStage.setResizable(false);
        windowStage.initModality(Modality.APPLICATION_MODAL);
        windowStage.showAndWait(); //When a window is blocked by a modal stage its Z-order relative to its ancestors is preserved, and it receives no input events and no window activation events, but continues to animate and render normally.
    }

    public void deleteAll() throws SQLException {
        conn = null;
        Statement stmt = null;
        String sql = "DELETE FROM Payment";
        conn = dbConnection.getConntection();
        stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        conn.close();

    }

}


