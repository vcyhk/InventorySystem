package customer.customerImport;

import common.AlertBox;
import customer.CustomerController;
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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ImportController implements Initializable {
    @FXML
    private TableView<ImportData> importTable;
    @FXML
    private TableColumn<ImportData, String> col_cid, col_Lname,col_Fname,col_Gender,col_AgeGp,col_Country,col_Address1,col_Address2,col_Address3,col_District,col_Email,col_Phone,col_sel;
    @FXML
    private Label filename;
    @FXML
    private Button selectBtn;
    @FXML
    private VBox importScene;
    private ObservableList<ImportData> data, itemInsert, itemUpdate, itemCreate;
    private List<String> cidExist = new ArrayList();

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
            String[] excelData = new String[13];
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
            if(colNum==12) {
                this.data.add(new ImportData(excelData[0],excelData[1],excelData[2],excelData[3],excelData[4]
                        ,excelData[5],excelData[6],excelData[7],excelData[8],excelData[9],excelData[10],excelData[11]));
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
            if(csvData.length==12)
                this.data.add(new ImportData(csvData[0],csvData[1],csvData[2],csvData[3],csvData[4]
                        ,csvData[5],csvData[6],csvData[7],csvData[8],csvData[9],csvData[10],csvData[11]));

        }
    }

    public void tableData(){
        this.col_cid.setCellValueFactory(new PropertyValueFactory<ImportData, String>("cid"));
        this.col_Lname.setCellValueFactory(new PropertyValueFactory<ImportData, String>("lname"));
        this.col_Fname.setCellValueFactory(new PropertyValueFactory<ImportData, String>("fname"));
        this.col_Gender.setCellValueFactory(new PropertyValueFactory<ImportData, String>("gender"));
        this.col_Country.setCellValueFactory(new PropertyValueFactory<ImportData, String>("country"));
        this.col_AgeGp.setCellValueFactory(new PropertyValueFactory<ImportData, String>("agegroup"));
        this.col_Phone.setCellValueFactory(new PropertyValueFactory<ImportData, String>("phone"));
        this.col_Email.setCellValueFactory(new PropertyValueFactory<ImportData, String>("email"));
        this.col_Address1.setCellValueFactory(new PropertyValueFactory<ImportData, String>("address1"));
        this.col_Address2.setCellValueFactory(new PropertyValueFactory<ImportData, String>("address2"));
        this.col_Address3.setCellValueFactory(new PropertyValueFactory<ImportData, String>("address3"));
        this.col_District.setCellValueFactory(new PropertyValueFactory<ImportData, String>("district"));
        this.importTable.setItems(null);
        this.importTable.setItems(this.data);
    }

    public void importCustomer(ActionEvent event) throws Exception{
        this.itemInsert = FXCollections.observableArrayList();
        this.itemUpdate = FXCollections.observableArrayList();
        this.itemCreate = FXCollections.observableArrayList();
        int noOfExist = noofCidExist();
        boolean dontChange = false;
        for (int i = 0; i < data.size(); i++) {
            if (isCIDExist(data.get(i).getCid())) {
                openWindow(data.get(i).getCid(), noOfExist);
                String a = WindowController.getData();
                WindowController.setData(null);
                try{
                    if (a.equals("overWrite")) {
                        this.itemUpdate.add(data.get(i));
                    } else if (a.equals("overWriteAll")) {
                        for (int k = i; k < data.size(); k++) {
                            this.itemUpdate.add(data.get(k));
                        }
                        break;
                    } else if (a.equals("skipAll")) {
                        break;
                    } else if (a.equals("keepNew")) {
                        this.itemCreate.add(data.get(i));
                    }
                }catch (Exception e){
                    dontChange = true;
                    break;
                }
            } else {
                this.itemInsert.add(data.get(i));
            }
        }
        if(!dontChange){
            insertCustomer();
            updateCustomer();
            closeWindow();
        }
    }

    public void closeWindow() throws Exception{
        CustomerController customerController = CustomerController.getInstance();
        customerController.loadData();
        Stage stage = (Stage) selectBtn.getScene().getWindow();
        stage.close();
    }

    private void insertCustomer() throws SQLException {
        //Insert the new data into database
        for (ImportData d: itemInsert) {
            String sqlInsert = "INSERT INTO Customer(CID, FName, LName, Gender, AgeGroup, Country, Address1, Address2, Address3, District, Email, Phone) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                Connection conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, d.getCid());
                ps.setString(2, d.getFname());
                ps.setString(3, d.getLname());
                ps.setString(4, d.getGender());
                ps.setString(5, d.getAgegroup());
                ps.setString(6, d.getCountry());
                ps.setString(7, d.getAddress1());
                ps.setString(8, d.getAddress2());
                ps.setString(9, d.getAddress3());
                ps.setString(10, d.getDistrict());
                ps.setString(11, d.getEmail());
                ps.setString(12, d.getPhone());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //Insert the new data into database, while the customer selected Keep "Both Records But Generate A New CID"
        for(ImportData d: itemCreate){
            long cidMax = Long.parseLong(maxCID());
            String sqlInsert = "INSERT INTO Customer(CID, FName, LName, Gender, AgeGroup, Country, Address1, Address2, Address3, District, Email, Phone) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                Connection conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, String.valueOf(++cidMax));
                ps.setString(2, d.getFname());
                ps.setString(3, d.getLname());
                ps.setString(4, d.getGender());
                ps.setString(5, d.getAgegroup());
                ps.setString(6, d.getCountry());
                ps.setString(7, d.getAddress1());
                ps.setString(8, d.getAddress2());
                ps.setString(9, d.getAddress3());
                ps.setString(10, d.getDistrict());
                ps.setString(11, d.getEmail());
                ps.setString(12, d.getPhone());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateCustomer() throws SQLException {
        //Update the data of the customer, which the cid exist.
        for (ImportData d: itemUpdate) {
            if(isCIDExist(d.getCid())) {
                String sqlInsert = "UPDATE Customer SET FName = ?, LName = ?, Gender = ?, AgeGroup = ?, Country = ?, Address1 = ?, Address2 = ?, Address3 = ?, District = ?, Email = ?, Phone = ? WHERE CID = ?";
                try {
                    Connection conn = dbConnection.getConntection();
                    PreparedStatement ps = conn.prepareStatement(sqlInsert);

                    ps.setString(1, d.getFname());
                    ps.setString(2, d.getLname());
                    ps.setString(3, d.getGender());
                    ps.setString(4, d.getAgegroup());
                    ps.setString(5, d.getCountry());
                    ps.setString(6, d.getAddress1());
                    ps.setString(7, d.getAddress2());
                    ps.setString(8, d.getAddress3());
                    ps.setString(9, d.getDistrict());
                    ps.setString(10, d.getEmail());
                    ps.setString(11, d.getPhone());
                    ps.setString(12, d.getCid());

                    ps.execute();
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                String sqlInsert = "INSERT INTO Customer(CID, FName, LName, Gender, AgeGroup, Country, Address1, Address2, Address3, District, Email, Phone) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try {
                    Connection conn = dbConnection.getConntection();
                    PreparedStatement ps = conn.prepareStatement(sqlInsert);

                    ps.setString(1, d.getCid());
                    ps.setString(2, d.getFname());
                    ps.setString(3, d.getLname());
                    ps.setString(4, d.getGender());
                    ps.setString(5, d.getAgegroup());
                    ps.setString(6, d.getCountry());
                    ps.setString(7, d.getAddress1());
                    ps.setString(8, d.getAddress2());
                    ps.setString(9, d.getAddress3());
                    ps.setString(10, d.getDistrict());
                    ps.setString(11, d.getEmail());
                    ps.setString(12, d.getPhone());

                    ps.execute();
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //check the # of the cid is exist
    public int noofCidExist() throws SQLException {
        int result  = 0;
        for(ImportData c : data){
            cidExist.add(c.getCid());
        }
        for(String cid : cidExist){
            Connection conn = dbConnection.getConntection();
            String sql = "SELECT COUNT(*) AS RESULT FROM CUSTOMER WHERE CID = " +  cid + ";";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if (Integer.parseInt(rs.getString(1)) > 0) {
                result++;
            }
            conn.close();
            rs.close();
        }
        return result;
    }

    //check the cid whether exist
    public boolean isCIDExist(String cid) throws SQLException {
        Connection conn = dbConnection.getConntection();
        String sql = "SELECT COUNT(*) AS RESULT FROM CUSTOMER WHERE CID = " +  cid + ";";
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

    //check the max. # of cid
    public String maxCID() throws SQLException {
        Connection conn = dbConnection.getConntection();
        String sql = "SELECT MAX(CID) FROM CUSTOMER;";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        String a =rs.getString(1);
        conn.close();
        rs.close();
        return a;
    }

    //the select window to user, decide how do deal with the exist cid data
    public void openWindow(String cid, int total) throws IOException {
        Stage windowStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/customer/customerImport/importSelection.fxml").openStream());
        customer.customerImport.WindowController importController = (customer.customerImport.WindowController)loader.getController();
        importController.setCid(cid);
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


