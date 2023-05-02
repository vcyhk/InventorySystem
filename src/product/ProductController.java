package product;

import common.AlertBox;
import common.PageController;
import common.PageGuide;
import common.PageModel;
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

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ProductController extends PageController implements Initializable, PageGuide {
    PageModel productModel = new PageModel();
    Connection conn;
    public boolean andT = false;
    private String sql = "SELECT Product.* FROM Product";
    @FXML
    private Label staffName, staffID,dbStatus;
    @FXML
    private ImageView staffIcon;
    @FXML
    private Button productButton, exitButton;
    @FXML
    private TextField itemInput, unitpriceInput, itemCatInput,itemDescInput;
    @FXML
    private DatePicker effectFromInput, effectToInput;
    @FXML
    private BorderPane basicScene;
    @FXML
    private TableView<ProductData> productTable;
    @FXML
    private TableColumn<ProductData, String> col_itemNum, col_ItemCat,col_ItemDesc,col_UnitPrice,col_EffectFrom,col_EffectTo,col_sel;

    private ObservableList<ProductData> data, items;

    private static ProductController instance = null;

    public ProductController(){
        instance = this;
    }

    public static ProductController getInstance(){
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/resources/stafficon/"+staff+".png");
        Image image = new Image(file.toURI().toString());
        staffIcon.setImage(image);

        if(productModel.isDatabaseConnected()){
            dbStatus.setText("Connected");
            dbStatus.setTextFill(Color.GREEN);
        }else{
            dbStatus.setText("Not Connected");
            dbStatus.setTextFill(Color.RED);
        }

        productButton.setDisable(true);

        try {
            setStaff(staff, staffName, staffID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRecord(ActionEvent event) throws Exception {
        conn = null;
        Statement stmt = null;

        items = productTable.getItems();
        try {
            for (ProductData item : items) {
                if (item.getRemark().isSelected()) {
                    String sqlD = "Delete from product Where ItemID = " + item.getItemID();
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
        Pane root = (Pane)loader.load(getClass().getResource("/product/productImport/importPage.fxml").openStream());
        product.productImport.ImportController importController = (product.productImport.ImportController)loader.getController();
        Scene scene = new Scene(root);
        editStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        editStage.setScene(scene);
        editStage.setTitle("Inventory System");
        editStage.setResizable(false);
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.show();
    }

    public void exportToExcel(ActionEvent event) throws SQLException, IOException {
        File selectedDirectory = selectPath("Product");
        if (selectedDirectory == null) {
            //No Directory selected
        } else {
            conn = dbConnection.getConntection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            String query = "Select * from product";
            String fileTypeName = selectedDirectory.getName();
            String[] parts = fileTypeName.split(Pattern.quote("."));
            String part2 = parts[1];
            if (part2.toLowerCase().equals("xlsx")) {
                try {
                    stmt = conn.prepareStatement(query);
                    rs = stmt.executeQuery();

                    XSSFWorkbook wb = new XSSFWorkbook();//for earlier version use HSSF
                    XSSFSheet sheet = wb.createSheet("Product Details");
                    XSSFRow header = sheet.createRow(0);
                    header.createCell(0).setCellValue("ItemID");
                    header.createCell(1).setCellValue("Category");
                    header.createCell(2).setCellValue("Description");
                    header.createCell(3).setCellValue("Unit");
                    header.createCell(4).setCellValue("Effective From");
                    header.createCell(5).setCellValue("Effective To");

                    sheet.autoSizeColumn(1);
                    sheet.autoSizeColumn(2);
                    sheet.autoSizeColumn(3);
                    sheet.autoSizeColumn(4);
                    sheet.autoSizeColumn(5);
                    sheet.autoSizeColumn(6);

                    sheet.setZoom(150);

                    int index = 1;
                    while (rs.next()) {
                        XSSFRow row = sheet.createRow(index);
                        row.createCell(0).setCellValue(rs.getString("ItemID"));
                        row.createCell(1).setCellValue(rs.getString("Category"));
                        row.createCell(2).setCellValue(rs.getString("Desc"));
                        row.createCell(3).setCellValue(rs.getString("UnitPrice"));
                        row.createCell(4).setCellValue(rs.getString("EffectiveFrom"));
                        row.createCell(5).setCellValue(rs.getString("EffectiveTo"));
                        index++;
                    }
                    FileOutputStream fileOut = new FileOutputStream(selectedDirectory.getAbsolutePath());//before 2007 version xls
                    wb.write(fileOut);
                    fileOut.close();

                    stmt.close();
                    rs.close();
                    conn.close();
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
                            String text = rs.getString("ItemID") + "," + rs.getString("Category") + "," +
                                    rs.getString("Desc") + "," + rs.getString("UnitPrice") + ","
                                    + rs.getString("EffectiveFrom") + "," + rs.getString("EffectiveTo") + "\n";
                            writer.write(text);
                        }
                        conn.close();
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

        items = productTable.getItems();
        String query = "Select * from Product Where itemId IN ";
        try {
            int i = 0;
            for (ProductData item : items) {
                if (item.getRemark().isSelected()) {
                    if (i == 0) {
                        query += "('" + item.getItemID() + "'";
                    }else
                        query += "," + "'" + item.getItemID() + "'";
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
                            header.createCell(1).setCellValue("Category");
                            header.createCell(2).setCellValue("Description");
                            header.createCell(3).setCellValue("Unit Price");
                            header.createCell(4).setCellValue("Effective From");
                            header.createCell(5).setCellValue("Effective To");
                            sheet.autoSizeColumn(1);
                            sheet.autoSizeColumn(2);
                            sheet.autoSizeColumn(3);
                            sheet.autoSizeColumn(4);
                            sheet.autoSizeColumn(5);
                            sheet.autoSizeColumn(6);

                            sheet.setZoom(150);

                            int index = 1;
                            while (rs.next()) {
                                XSSFRow row = sheet.createRow(index);
                                row.createCell(0).setCellValue(rs.getString("ItemID"));
                                row.createCell(1).setCellValue(rs.getString("Category"));
                                row.createCell(2).setCellValue(rs.getString("Desc"));
                                row.createCell(3).setCellValue(rs.getString("UnitPrice"));
                                row.createCell(4).setCellValue(rs.getString("EffectiveFrom"));
                                row.createCell(5).setCellValue(rs.getString("EffectiveTo"));
                                index++;
                            }

                            FileOutputStream fileOut = new FileOutputStream(selectedDirectory.getAbsolutePath());//before 2007 version xls
                            wb.write(fileOut);
                            fileOut.close();

                            stmt.close();
                            rs.close();
                            conn.close();
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
                                    String text = rs.getString("ItemID") + "," + rs.getString("Category") + "," +
                                            rs.getString("Desc") + "," + rs.getString("UnitPrice") + ","
                                            + rs.getString("EffectiveFrom") + "," + rs.getString("EffectiveTo") + "\n";
                                    writer.write(text);
                                }
                                conn.close();
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

    public void clearField() {
        itemInput.clear();
        unitpriceInput.clear();
        itemCatInput.clear();
        itemDescInput.clear();
        effectFromInput.setValue(null);
        effectToInput.setValue(null);
    }

    public void loadData() throws SQLException {
        conn = dbConnection.getConntection();
        this.data = FXCollections.observableArrayList();
        String sql = "SELECT product.* FROM product";

        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            this.data.add(new ProductData(rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)
            ));
        }
        conn.close();
        setTableData();

    }

    public void logOut(ActionEvent event) throws Exception {
        super.logOut(exitButton);
        productModel.exitDatabase();
    }

    public void handlEditOption(ActionEvent event) throws IOException {
        ProductData selectedForEdit = productTable.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            return;
        }
        switchEdit(selectedForEdit);
    }

    public void switchEdit(ProductData selectedForEdit) throws IOException{
        Stage editStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/product/productEdit/edit.fxml").openStream());
        product.productEdit.EditController editController = (product.productEdit.EditController)loader.getController();
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
        Pane root = (Pane) loader.load(getClass().getResource("/product/productAdd/add.fxml").openStream());
        product.productAdd.AddController addController = (product.productAdd.AddController) loader.getController();
        Scene scene = new Scene(root);
        addStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        addStage.setScene(scene);
        addStage.setTitle("Inventory System");
        addStage.setResizable(false);
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.show();
    }

    public void filterData(ActionEvent event)throws SQLException {
        try {
            if (!itemInput.getText().equals("")) {
                if (!andT) {
                    sql += " where Product.ItemID = " + itemInput.getText();
                    andT = true;
                } else {
                    sql += " and Product.ItemID = " + itemInput.getText();
                }
            }
            if (!itemCatInput.getText().equals("")) {
                if (!andT) {
                    sql += " where Product.Category LIKE '%" + itemCatInput.getText() + "%'";
                    andT = true;
                } else {
                    sql += " and Product.Category LIKE '%" + itemCatInput.getText() + "%'";
                }
            }

            if (!itemDescInput.getText().equals("")) {
                if (!andT) {
                    sql += " where Product.Desc LIKE '%" + itemDescInput.getText() + "%'";
                    andT = true;
                } else {
                    sql += " and Product.Desc LIKE '%" + itemDescInput.getText() + "%'";
                }
            }
            if (!unitpriceInput.getText().equals("")) {
                if (!andT) {
                    sql += " where Product.UnitPrice LIKE '%" + unitpriceInput.getText() + "%'";
                    andT = true;
                } else {
                    sql += " and Product.UnitPrice LIKE '%" + unitpriceInput.getText() + "%'";
                }
            }
            if (!(effectFromInput.getValue() == null)) {
                if (!andT) {
                    sql += " where Product.EffectiveFrom LIKE '%" + String.valueOf(effectFromInput.getValue()) + "%'";
                    andT = true;
                } else {
                    sql += " and Product.EffectiveFrom LIKE '%" + String.valueOf(effectFromInput.getValue()) + "%'";
                }
            }
            if (!(effectToInput.getValue() == null)) {
                if (!andT) {
                    sql += " where Product.EffectiveTo LIKE '%" + String.valueOf(effectToInput.getValue()) + "%'";
                    andT = true;
                } else {
                    sql += " and Product.EffectiveTo LIKE '%" + String.valueOf(effectToInput.getValue()) + "%'";
                }
            }


            conn = dbConnection.getConntection();
            this.data = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                this.data.add(new ProductData(rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), String.valueOf(rs.getString(5)), String.valueOf(rs.getString(6)))
                );
            }
            sql = "SELECT Product.* FROM Product";
            andT = false;

        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        } finally {
            sql = "SELECT Product.* FROM Product";
            andT = false;

        }
        conn.close();
        setTableData();
    }

    public File selectPath(String name) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        String userDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userDir +"/Desktop"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel(*.xlsx)", "*.xlsx"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV(*.csv)", "*.csv"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd_MM_yyyy HH_mm_ss");
        fileChooser.setInitialFileName("Product"+ LocalDateTime.now().format(format));
        File selectedDirectory = fileChooser.showSaveDialog(stage);
        return selectedDirectory;
    }

    public void setTableData(){
        this.col_itemNum.setCellValueFactory(new PropertyValueFactory<ProductData, String>("itemID"));
        this.col_ItemCat.setCellValueFactory(new PropertyValueFactory<ProductData, String>("categroy"));
        this.col_ItemDesc.setCellValueFactory(new PropertyValueFactory<ProductData, String>("desc"));
        this.col_UnitPrice.setCellValueFactory(new PropertyValueFactory<ProductData, String>("unitPrice"));
        this.col_EffectFrom.setCellValueFactory(new PropertyValueFactory<ProductData, String>("effectiveFrom"));
        this.col_EffectTo.setCellValueFactory(new PropertyValueFactory<ProductData, String>("effectiveTo"));
        this.col_sel.setCellValueFactory(new PropertyValueFactory<ProductData, String>("remark"));

        this.productTable.setItems(null);
        this.productTable.setItems(this.data);

    }
}
