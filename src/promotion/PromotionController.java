package promotion;

import common.AlertBox;
import common.PageController;
import common.PageGuide;
import common.PageModel;
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
import product.ProductData;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class PromotionController extends PageController implements Initializable, PageGuide {
    PageModel promotionModel = new PageModel();
    public boolean andT = false;
    private String sql = "SELECT Promotion.* FROM Promotion";
    Connection conn;
    @FXML
    private Label staffName, staffID,dbStatus;
    @FXML
    private ImageView staffIcon;
    @FXML
    private Button promotionButton, exitButton;
    @FXML
    private TextField itemInput, discountInput;
    @FXML
    private DatePicker effectFromInput, effectToInput;
    @FXML
    private BorderPane basicScene;
    @FXML
    private TableView<PromotionData> promotionTable;
    @FXML
    private TableColumn<PromotionData, String> col_itemNum, col_Discount, col_EffectFrom, col_EffectTo, col_sel;

    private ObservableList<PromotionData> data, items;

    private static PromotionController instance = null;

    public PromotionController(){
        instance = this;
    }

    public static PromotionController getInstance(){
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/resources/stafficon/"+staff+".png");
        Image image = new Image(file.toURI().toString());
        staffIcon.setImage(image);

        if(promotionModel.isDatabaseConnected()){
            dbStatus.setText("Connected");
            dbStatus.setTextFill(Color.GREEN);
        }else{
            dbStatus.setText("Not Connected");
            dbStatus.setTextFill(Color.RED);
        }

        promotionButton.setDisable(true);

        try {
            setStaff(staff, staffName, staffID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRecord(ActionEvent event) throws Exception{
        conn = null;
        Statement stmt = null;

        items = promotionTable.getItems();
        try {
            for (PromotionData item : items) {
                if (item.getRemark().isSelected()) {
                    String sqlD = "Delete from Promotion Where Promotion.ItemID = " + item.getItemID() +
                            " AND Promotion.EffectiveFrom = '"+ item.getEffectiveFrom() +"' AND Promotion.EffectiveTo = '" + item.getEffectiveTo()
                            +"' AND Promotion.Discount = " + item.getDiscount();
                    conn = dbConnection.getConntection();
                    stmt = conn.createStatement();
                    stmt.executeUpdate(sqlD);
                    conn.close();
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        loadData();
    }


    //Import Data
    public void importData(ActionEvent event) throws Exception {
        Stage editStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/promotion/promotionImport/importPage.fxml").openStream());
        promotion.promotionImport.ImportController importController = loader.getController();
        Scene scene = new Scene(root);
        editStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        editStage.setScene(scene);
        editStage.setTitle("Inventory System");
        editStage.setResizable(false);
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.show();
    }

    public void exportToExcel(ActionEvent event) throws SQLException, IOException {
        File selectedDirectory = selectPath("Promotion");
        if (selectedDirectory == null) {
            //No Directory selected
        } else {
            conn = dbConnection.getConntection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            String query = "Select * from Promotion";
            String fileTypeName = selectedDirectory.getName();
            String[] parts = fileTypeName.split(Pattern.quote("."));
            String part2 = parts[1];
            if (part2.toLowerCase().equals("xlsx")) {
                try {
                    stmt = conn.prepareStatement(query);
                    rs = stmt.executeQuery();

                    XSSFWorkbook wb = new XSSFWorkbook();//for earlier version use HSSF
                    XSSFSheet sheet = wb.createSheet("Promotion Details");
                    XSSFRow header = sheet.createRow(0);
                    header.createCell(0).setCellValue("ItemID");
                    header.createCell(1).setCellValue("Discount");
                    header.createCell(2).setCellValue("Effective From");
                    header.createCell(3).setCellValue("Effective To");

                    sheet.autoSizeColumn(1);
                    sheet.autoSizeColumn(2);
                    sheet.autoSizeColumn(3);

                    sheet.setZoom(150);

                    int index = 1;
                    while (rs.next()) {
                        XSSFRow row = sheet.createRow(index);
                        row.createCell(0).setCellValue(rs.getString("ItemID"));
                        row.createCell(1).setCellValue(rs.getString("Discount"));
                        row.createCell(2).setCellValue(rs.getString("EffectiveFrom"));
                        row.createCell(3).setCellValue(rs.getString("EffectiveTo"));
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
                            String text = rs.getString("ItemID") + "," + rs.getString("Discount") + ","
                                    + rs.getString("EffectiveFrom") + "," + rs.getString("EffectiveTo") + "\n";
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

    public void dataToExcel(ActionEvent event) throws SQLException {
        conn = dbConnection.getConntection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        items = promotionTable.getItems();
        String query = "Select * from Promotion Where itemId IN ";
        try {
            int i = 0;
            for (PromotionData item : items) {
                if (item.getRemark().isSelected()) {
                    if (i == 0) {
                        query += "('" + item.getItemID() + "'";
                    }else {
                        query += "," + "'" + item.getItemID() + "'";
                    }
                    i += 1;
                }
            }
            if(i>0){
                query += ") and EffectiveFrom IN ";
                i=0;
            }
            for(PromotionData item : items){
                if (item.getRemark().isSelected()) {
                    if (i == 0) {
                        query += "('" + item.getEffectiveFrom() + "'";
                    }else
                        query += "," + "'" + item.getEffectiveFrom() + "'";
                    i += 1;
                }
            }
            if(i>0){
                query += ") and EffectiveTo IN ";
                i=0;
            }
            for(PromotionData item : items){
                if (item.getRemark().isSelected()) {
                    if (i == 0) {
                        query += "('" + item.getEffectiveTo() + "'";
                    }else
                        query += "," + "'" + item.getEffectiveTo() + "'";
                    i += 1;
                }
            }
            if(i>0){
                query += ") and Discount IN ";
                i=0;
            }
            for(PromotionData item : items){
                if (item.getRemark().isSelected()) {
                    if (i == 0) {
                        query += "('" + item.getDiscount() + "'";
                    }else
                        query += "," + "'" + item.getDiscount() + "'";
                    i += 1;
                }
            }

            if (i != 0) {
                File selectedDirectory = selectPath("Product");
                if (selectedDirectory == null) {
                    //No Directory selected
                } else {
                    String fileTypeName = selectedDirectory.getName();
                    String[] parts = fileTypeName.split(Pattern.quote("."));
                    String part2 = parts[1];
                    query += ")";
                    if (part2.toLowerCase().equals("xlsx")) {
                        try {
                            conn = dbConnection.getConntection();
                            stmt = conn.prepareStatement(query);
                            rs = stmt.executeQuery();
                            XSSFWorkbook wb = new XSSFWorkbook();//for earlier version use HSSF
                            XSSFSheet sheet = wb.createSheet("Product Details");
                            XSSFRow header = sheet.createRow(0);
                            header.createCell(0).setCellValue("ItemID");
                            header.createCell(1).setCellValue("Discount");
                            header.createCell(2).setCellValue("Effective From");
                            header.createCell(3).setCellValue("Effective To");
                            sheet.autoSizeColumn(1);
                            sheet.autoSizeColumn(2);
                            sheet.autoSizeColumn(3);

                            sheet.setZoom(150);

                            int index = 1;
                            while (rs.next()) {
                                XSSFRow row = sheet.createRow(index);
                                row.createCell(0).setCellValue(rs.getString("ItemID"));
                                row.createCell(1).setCellValue(rs.getString("Discount"));
                                row.createCell(2).setCellValue(rs.getString("EffectiveFrom"));
                                row.createCell(3).setCellValue(rs.getString("EffectiveTo"));
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
                            conn = dbConnection.getConntection();
                            try {
                                File file = new File(selectedDirectory.getAbsolutePath());
                                writer = new BufferedWriter(new FileWriter(file));
                                stmt = conn.prepareStatement(query);
                                rs = stmt.executeQuery();
                                while (rs.next()) {
                                    String text = rs.getString("ItemID") + "," + rs.getString("Discount") + ","
                                            + rs.getString("EffectiveFrom") + "," + rs.getString("EffectiveTo") + "\n";
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
            } else {
                AlertBox.informationBox("You doesn't select the record");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }

    public void clearField(){
        itemInput.clear();
        discountInput.clear();
        effectFromInput.setValue(null);
        effectToInput.setValue(null);
    }

    public void loadData() throws SQLException{
        conn = dbConnection.getConntection();
        this.data = FXCollections.observableArrayList();
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            this.data.add(new PromotionData(rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4) ));
        }
        conn.close();
        setTableData();
    }

    public void logOut(ActionEvent event) throws Exception{
        super.logOut(exitButton);
        promotionModel.exitDatabase();
    }

    public void handlEditOption(ActionEvent event) throws IOException {
        PromotionData selectedForEdit = promotionTable.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            return;
        }
        switchEdit(selectedForEdit);
    }

    public void switchEdit(PromotionData selectedForEdit) throws IOException{
        Stage editStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/promotion/promotionEdit/edit.fxml").openStream());
        promotion.promotionEdit.EditController editController = (promotion.promotionEdit.EditController)loader.getController();
        editController.inflateUI(selectedForEdit);
        Scene scene = new Scene(root);
        editStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        editStage.setScene(scene);
        editStage.setTitle("Inventory System");
        editStage.setResizable(false);
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.show();
    }

    public void switchAdd(ActionEvent event) throws IOException{
        Stage addStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/promotion/promotionAdd/add.fxml").openStream());
        promotion.promotionAdd.AddController addController = loader.getController();
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
            if (!itemInput.getText().equals("")){
                if(!andT){
                    sql+= " where Promotion.ItemID = " + itemInput.getText();
                    andT = true;
                }else {
                    sql += " and Promotion.ItemID = " + itemInput.getText();
                }
            }
            if (!discountInput.getText().equals("")){
                if(!andT){
                    sql+= " where Promotion.Discount LIKE '%" + discountInput.getText() + "%'";
                    andT = true;
                }else {
                    sql += " and Promotion.Discount LIKE '%" + discountInput.getText() + "%'";
                }
            }
            if (!(effectFromInput.getValue() == null)) {
                if (!andT) {
                    sql += " where Promotion.EffectiveFrom LIKE '%" + String.valueOf(effectFromInput.getValue()) + "%'";
                    andT = true;
                }else {
                    sql += " and Promotion.EffectiveFrom LIKE '%" + String.valueOf(effectFromInput.getValue())+"%'";
                }
            }
            if (!(effectToInput.getValue() == null)) {
                if (!andT) {
                    sql += " where Promotion.EffectiveTo LIKE '%" + String.valueOf(effectToInput.getValue()) + "%'";
                    andT = true;
                }else {
                    sql += " and Promotion.EffectiveTo LIKE '%" + String.valueOf(effectToInput.getValue())+"%'";
                }
            }


            conn = dbConnection.getConntection();
            this.data = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                this.data.add(new PromotionData(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4))
                );
            }
            sql = "SELECT Promotion.* FROM Promotion";
            andT = false;
            conn.close();

        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }finally {
            sql = "SELECT Promotion.* FROM Promotion";
            andT = false;
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
        fileChooser.setInitialFileName("Promotion"+ LocalDateTime.now().format(format));
        File selectedDirectory = fileChooser.showSaveDialog(stage);
        return selectedDirectory;
    }


    public void setTableData(){
        this.col_itemNum.setCellValueFactory(new PropertyValueFactory<PromotionData, String>("itemID"));
        this.col_Discount.setCellValueFactory(new PropertyValueFactory<PromotionData, String>("discount"));
        this.col_EffectFrom.setCellValueFactory(new PropertyValueFactory<PromotionData, String>("effectiveFrom"));
        this.col_EffectTo.setCellValueFactory(new PropertyValueFactory<PromotionData, String>("effectiveTo"));
        this.col_sel.setCellValueFactory(new PropertyValueFactory<PromotionData, String>("remark"));

        this.promotionTable.setItems(null);
        this.promotionTable.setItems(this.data);
    }
}
