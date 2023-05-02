package product.productImport;

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
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import product.ProductController;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ImportController implements Initializable {
    Connection conn;
    @FXML
    private TableView<ImportData> importTable;
    @FXML
    private TableColumn<ImportData, String> col_itemNum, col_ItemCat, col_ItemDesc, col_UnitPrice, col_EffectFrom, col_EffectTo;
    @FXML
    private Label filename;
    @FXML
    private Button selectBtn;
    @FXML
    private VBox importScene;

    private ObservableList<ImportData> data, itemInsert, itemUpdate, itemCreate;
    private List<String> pidExist = new ArrayList();

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
            if(colNum==6) {
                this.data.add(new ImportData(excelData[0],excelData[1],excelData[2],excelData[3],excelData[4],excelData[5]));
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
            if(csvData.length==6)
                this.data.add(new ImportData(csvData[0],csvData[1],csvData[2],csvData[3],csvData[4],csvData[5]));

        }
    }

    public void tableData(){
        this.col_itemNum.setCellValueFactory(new PropertyValueFactory<ImportData, String>("itemID"));
        this.col_ItemCat.setCellValueFactory(new PropertyValueFactory<ImportData, String>("categroy"));
        this.col_ItemDesc.setCellValueFactory(new PropertyValueFactory<ImportData, String>("desc"));
        this.col_UnitPrice.setCellValueFactory(new PropertyValueFactory<ImportData, String>("unitPrice"));
        this.col_EffectFrom.setCellValueFactory(new PropertyValueFactory<ImportData, String>("effectiveFrom"));
        this.col_EffectTo.setCellValueFactory(new PropertyValueFactory<ImportData, String>("effectiveTo"));
        this.importTable.setItems(null);
        this.importTable.setItems(this.data);
    }

    public void importProduct(ActionEvent event) throws Exception{
        this.itemInsert = FXCollections.observableArrayList();
        this.itemUpdate = FXCollections.observableArrayList();
        this.itemCreate = FXCollections.observableArrayList();
        int noOfExist = noofCidExist();
        boolean dontChange = false;
        if(noOfExist>0) {
            for (int i = 0; i < data.size(); i++) {
                if (isCIDExist(data.get(i).getItemID())) {
                    openWindow(data.get(i).getItemID(), noOfExist);
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
        }
        if(!dontChange){
            insertProduct();
            updateProduct();
            closeWindow();
        }
    }

    public void closeWindow() throws Exception{
        ProductController productController = ProductController.getInstance();
        productController.loadData();
        Stage stage = (Stage) selectBtn.getScene().getWindow();
        stage.close();
    }

    private void insertProduct() throws SQLException {
        //Insert the new data into database
        for (ImportData d: itemInsert) {
            String sqlInsert = "INSERT INTO Product(ItemID, Category, Desc, UnitPrice, EffectiveFrom, EffectiveTo) VALUES ( ?, ?, ?, ?, ?, ?)";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, d.getItemID());
                ps.setString(2, d.getCategroy());
                ps.setString(3, d.getDesc());
                ps.setString(4, d.getUnitPrice());
                ps.setString(5, d.getEffectiveFrom());
                ps.setString(6, d.getEffectiveTo());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //Insert the new data into database, while the Product selected Keep "Both Records But Generate A New CID"
        for(ImportData d: itemCreate){
            long pidMax = Long.parseLong(maxPID());
            String sqlInsert = "INSERT INTO Product(ItemID, Category, Desc, UnitPrice, EffectiveFrom, EffectiveTo) VALUES ( ?, ?, ?, ?, ?, ?)";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, String.valueOf(++pidMax));
                ps.setString(2, d.getCategroy());
                ps.setString(3, d.getDesc());
                ps.setString(4, d.getUnitPrice());
                ps.setString(5, d.getEffectiveFrom());
                ps.setString(6, d.getEffectiveTo());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateProduct() throws SQLException {
        //Update the data of the Product, which the cid exist.
        for (ImportData d: itemUpdate) {
            String sqlInsert = "UPDATE Product SET Category = ?, Desc = ?, UnitPrice = ?, EffectiveFrom = ?, EffectiveTo = ? WHERE ItemID = ?";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, d.getCategroy());
                ps.setString(2, d.getDesc());
                ps.setString(3, d.getUnitPrice());
                ps.setString(4, d.getEffectiveFrom());
                ps.setString(5, d.getEffectiveTo());
                ps.setString(6, d.getItemID());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //check the # of the cid is exist
    public int noofCidExist() throws SQLException {
        int result  = 0;
        for(ImportData c : data){
            pidExist.add(c.getItemID());
        }
        for(String pid : pidExist){
            conn = dbConnection.getConntection();
            String sql = "SELECT COUNT(*) AS RESULT FROM PRODUCT WHERE ItemID = " +  pid + ";";
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
    public boolean isCIDExist(String pid) throws SQLException {
        conn = dbConnection.getConntection();
        String sql = "SELECT COUNT(*) AS RESULT FROM PRODUCT WHERE ItemID = " +  pid + ";";
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
    public String maxPID() throws SQLException {
        conn = dbConnection.getConntection();
        String sql = "SELECT MAX(ItemID) FROM PRODUCT;";
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
        Pane root = (Pane)loader.load(getClass().getResource("/product/productImport/importSelection.fxml").openStream());
        WindowController importController = (WindowController)loader.getController();
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


