package promotion.promotionImport;

import common.AlertBox;
import customer.CustomerController;
import customer.CustomerData;
import dbUtil.dbConnection;
import javafx.beans.property.StringProperty;
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
import promotion.PromotionController;
import promotion.PromotionData;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ImportController implements Initializable {
    Connection conn =null;
    @FXML
    private TableView<ImportData> importTable;
    @FXML
    private TableColumn<ImportData, String> col_itemNum, col_Discount,col_EffectFrom,col_EffectTo;
    @FXML
    private Label filename;
    @FXML
    private Button selectBtn;
    @FXML
    private VBox importScene;

    private ObservableList<ImportData> data, itemInsert, itemUpdate, itemCreate, promotionExist;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.data = FXCollections.observableArrayList();
        this.promotionExist = FXCollections.observableArrayList();
        this.itemCreate = FXCollections.observableArrayList();
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
        this.col_itemNum.setCellValueFactory(new PropertyValueFactory<ImportData, String>("itemID"));
        this.col_Discount.setCellValueFactory(new PropertyValueFactory<ImportData, String>("discount"));
        this.col_EffectFrom.setCellValueFactory(new PropertyValueFactory<ImportData, String>("effectiveFrom"));
        this.col_EffectTo.setCellValueFactory(new PropertyValueFactory<ImportData, String>("effectiveTo"));
        this.importTable.setItems(null);
        this.importTable.setItems(this.data);
    }

    public void importPromotion(ActionEvent event) throws Exception{
        this.itemInsert = FXCollections.observableArrayList();
        this.itemUpdate = FXCollections.observableArrayList();

        int noOfExist = noofPromotionExist();
        boolean dontChange = false;
        for (int i = 0; i < data.size(); i++) {
            if (isPromotionExist(data.get(i).getItemID(), data.get(i).getDiscount(), data.get(i).getEffectiveFrom(), data.get(i).getEffectiveTo())) {
                String message = "ItemID : " + data.get(i).getItemID() + ", Discount : " + data.get(i).getDiscount() + ", Effective From : " + data.get(i).getEffectiveFrom() + ",  Effective To : " + data.get(i).getEffectiveTo();
                openWindow(message, noOfExist);
                String a = promotion.promotionImport.WindowController.getData();
                customer.customerImport.WindowController.setData(null);
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
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    dontChange = true;
                    break;
                }
            } else {
                this.itemInsert.add(data.get(i));
            }
        }
        if(!dontChange){
            insertPromotion();
            updatePromotion();
            closeWindow();
        }
    }

    public void closeWindow() throws Exception{
        PromotionController promotionController = PromotionController.getInstance();
        promotionController.loadData();
        Stage stage = (Stage) selectBtn.getScene().getWindow();
        stage.close();
    }

    private void insertPromotion() throws SQLException {
        //Insert the new data into database
        for (ImportData d: itemInsert) {
            String sqlInsert = "INSERT INTO Promotion(ItemID, Discount, EffectiveFrom, EffectiveTo) VALUES ( ?, ?, ?, ?)";
            try {
                conn = dbConnection.getConntection();
                PreparedStatement ps = conn.prepareStatement(sqlInsert);

                ps.setString(1, d.getItemID());
                ps.setString(2, d.getDiscount());
                ps.setString(3, d.getEffectiveFrom());
                ps.setString(4, d.getEffectiveTo());

                ps.execute();
                ps.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private void updatePromotion() throws SQLException {
        //Update the data of the customer, which the cid exist.
        this.itemCreate = FXCollections.observableArrayList();
        this.promotionExist = FXCollections.observableArrayList();
        for (ImportData d: itemUpdate) {
            if (isPromotionExist(d)) {
                updateIS(promotionExist.size()-1);
            }else{
                String sqlInsert = "INSERT INTO Promotion(ItemID, Discount, EffectiveFrom, EffectiveTo) VALUES ( ?, ?, ?, ?)";
                try {
                    conn = dbConnection.getConntection();
                    PreparedStatement ps = conn.prepareStatement(sqlInsert);

                    ps.setString(1, d.getItemID());
                    ps.setString(2, d.getDiscount());
                    ps.setString(3, d.getEffectiveFrom());
                    ps.setString(4, d.getEffectiveTo());

                    ps.execute();
                    ps.close();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

/*        for(int i = 0; i< promotionExist.size();i++){

            Statement stmt1 = null;
            try{
                String sqlInsert2 = "UPDATE PROMOTION SET ItemID = "+ itemCreate.get(i).getItemID()+" , Discount = "+itemCreate.get(i).getDiscount()+" , EffectiveFrom = '" + itemCreate.get(i).getEffectiveFrom() +
                        "', EffectiveTo = '" + itemCreate.get(i).getEffectiveTo() + "' WHERE ItemID = "+ promotionExist.get(i).getItemID() + " AND Discount = " + promotionExist.get(i).getDiscount() +
                        " AND EffectiveFrom = '" + promotionExist.get(i).getEffectiveFrom() + "' AND EffectiveTo = '" + promotionExist.get(i).getEffectiveTo() + "'";
                System.out.println(sqlInsert2);
                conn = dbConnection.getConntection();
                stmt1 = conn.createStatement();
                stmt1.executeUpdate(sqlInsert2);
                conn.close();
                stmt1.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }*/

    }

    public void updateIS(int i){
        Statement stmt1 = null;
        try{
            String sqlInsert2 = "UPDATE PROMOTION SET ItemID = "+ itemCreate.get(i).getItemID()+" , Discount = "+itemCreate.get(i).getDiscount()+" , EffectiveFrom = '" + itemCreate.get(i).getEffectiveFrom() +
                    "', EffectiveTo = '" + itemCreate.get(i).getEffectiveTo() + "' WHERE ItemID = "+ promotionExist.get(i).getItemID() + " AND Discount = " + promotionExist.get(i).getDiscount() +
                    " AND EffectiveFrom = '" + promotionExist.get(i).getEffectiveFrom() + "' AND EffectiveTo = '" + promotionExist.get(i).getEffectiveTo() + "'";
            conn = dbConnection.getConntection();
            stmt1 = conn.createStatement();
            stmt1.executeUpdate(sqlInsert2);
            conn.close();
            stmt1.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //check the # of the Promotion is exist
    public int noofPromotionExist() throws SQLException {
        int result  = 0;
        for(ImportData c : data){
            conn = dbConnection.getConntection();
            String sql = "SELECT * FROM PROMOTION WHERE ItemID = "+ c.getItemID() + " AND Discount = " + c.getDiscount() + " AND EffectiveFrom <= '" + c.getEffectiveFrom() + "' AND EffectiveTo >= '" + c.getEffectiveTo() + "'";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                result++;
            }
            conn.close();
            rs.close();
        }
        return result;
    }

    //check the promotion whether exist
    public boolean isPromotionExist(String itemID, String discount, String effectiveFrom, String effectiveTo) throws SQLException {
        conn = dbConnection.getConntection();
        //case 1: exist the promotion within the import period
        String sql = "SELECT * FROM PROMOTION WHERE ItemID = "+ itemID + " AND Discount = " + discount + " AND EffectiveFrom <= '" + effectiveFrom + "' AND EffectiveTo >= '" + effectiveTo + "'";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()){
            conn.close();
            return true;
        }
        rs.close();
        //case 2 : exist the promotion when EffectiveFrom <= input effectivefrom && EffectiveTo <= input effectiveTo
        sql = "SELECT * FROM PROMOTION WHERE ItemID = "+ itemID + " AND Discount = " + discount + " AND EffectiveFrom <= '" + effectiveFrom + "' AND EffectiveTo <= '" + effectiveTo + "'";
        ResultSet rs1 = conn.createStatement().executeQuery(sql);
        while (rs1.next()){
            conn.close();
            return true;
        }
        rs1.close();
        //case 3 : exist the promotion when EffectiveFrom >= input effectivefrom && EffectiveTo >= input effectiveTo
        sql = "SELECT * FROM PROMOTION WHERE ItemID = "+ itemID + " AND Discount = " + discount + " AND EffectiveFrom >= '" + effectiveFrom + "' AND EffectiveTo >= '" + effectiveTo + "'";
        ResultSet rs2 = conn.createStatement().executeQuery(sql);
        while (rs2.next()){
            conn.close();
            return true;
        }
        rs2.close();
        //case 4 : exist the promotion when EffectiveFrom <= input effectivefrom && EffectiveTo <= input effectiveTo
        sql = "SELECT * FROM PROMOTION WHERE ItemID = "+ itemID + " AND Discount = " + discount + " AND EffectiveFrom <= '" + effectiveFrom + "' AND EffectiveTo <>>= '" + effectiveTo + "'";
        ResultSet rs3 = conn.createStatement().executeQuery(sql);
        while (rs3.next()){
            conn.close();
            return true;
        }
        conn.close();
        rs3.close();
        return false;
    }

    //check the promotion whether exist
    public boolean isPromotionExist(ImportData dataP) throws SQLException {
        conn = dbConnection.getConntection();
        //case 1: exist the promotion within the import period
        String sql = "SELECT * FROM PROMOTION WHERE ItemID = "+ dataP.getItemID() + " AND Discount = " + dataP.getDiscount()+ " AND EffectiveFrom <= '" + dataP.getEffectiveFrom() + "' AND EffectiveTo >= '" + dataP.getEffectiveTo() + "'";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()){
            itemCreate.add(dataP);
            promotionExist.add(new ImportData(rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)));
            rs.close();
            conn.close();
            return true;
        }
        rs.close();
        //case 2 : exist the promotion when EffectiveFrom <= input effectivefrom && EffectiveTo <= input effectiveTo
        sql = "SELECT * FROM PROMOTION WHERE ItemID = "+ dataP.getItemID() + " AND Discount = " + dataP.getDiscount() + " AND EffectiveFrom <= '" + dataP.getEffectiveFrom() + "' AND EffectiveTo <= '" + dataP.getEffectiveTo() + "'";
        ResultSet rs1 = conn.createStatement().executeQuery(sql);
        while (rs1.next()){
            itemCreate.add(dataP);
            promotionExist.add(new ImportData(rs1.getString(1),rs1.getString(2),rs1.getString(3),rs1.getString(4)));
            rs1.close();
            conn.close();
            return true;
        }
        rs1.close();
        //case 3 : exist the promotion when EffectiveFrom >= input effectivefrom && EffectiveTo >= input effectiveTo
        sql = "SELECT * FROM PROMOTION WHERE ItemID = "+ dataP.getItemID() + " AND Discount = " + dataP.getDiscount() + " AND EffectiveFrom >= '" + dataP.getEffectiveFrom() + "' AND EffectiveTo >= '" + dataP.getEffectiveTo() + "'";
        ResultSet rs2 = conn.createStatement().executeQuery(sql);
        while (rs2.next()){
            itemCreate.add(dataP);
            promotionExist.add(new ImportData(rs2.getString(1),rs2.getString(2),rs2.getString(3),rs2.getString(4)));
            rs2.close();
            conn.close();
            return true;
        }
        rs2.close();
        //case 4 : exist the promotion when EffectiveFrom <= input effectivefrom && EffectiveTo <= input effectiveTo
        sql = "SELECT * FROM PROMOTION WHERE ItemID = "+ dataP.getItemID() + " AND Discount = " + dataP.getDiscount() + " AND EffectiveFrom <= '" + dataP.getEffectiveFrom() + "' AND EffectiveTo <>>= '" + dataP.getEffectiveTo() + "'";
        ResultSet rs3 = conn.createStatement().executeQuery(sql);
        while (rs3.next()){
            itemCreate.add(dataP);
            promotionExist.add(new ImportData(rs3.getString(1),rs3.getString(2),rs3.getString(3),rs3.getString(4)));
            rs3.close();
            conn.close();
            return true;
        }
        rs3.close();
        conn.close();
        return false;
    }

    //the select window to user, decide how do deal with the exist cid data
    public void openWindow(String message, int total) throws IOException {
        Stage windowStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/promotion/promotionImport/importSelection.fxml").openStream());
        WindowController importController = (WindowController)loader.getController();
        importController.setCid(message);
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
