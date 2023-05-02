package customer;

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
import javafx.stage.*;
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

public class CustomerController extends PageController implements Initializable, PageGuide {
    PageModel customerModel = new PageModel();
    Connection conn;
    public boolean andT = false;
    private String sql = "SELECT Customer.* FROM Customer";
    @FXML
    private Label staffName, staffID,dbStatus;
    @FXML
    private ImageView staffIcon;
    @FXML
    private Button exitButton,customerButton;
    @FXML
    private TextField cidInput, lnameInput, fnameInput, addressInput, emailInput, phoneInput, districtInput;
    @FXML
    private ComboBox genderInput, agegpInput, countryInput;

    @FXML
    private BorderPane basicScene;
    @FXML
    private TableView<CustomerData> customerTable;
    @FXML
    private TableColumn<CustomerData, String> col_cid, col_Lname,col_Fname,col_Gender,col_AgeGp,col_Country,col_Address,col_District,col_Email,col_Phone,col_sel;

    private ObservableList<CustomerData> data, items;

    private static CustomerController instance = null;

    public CustomerController(){
        instance = this;
    }

    public static CustomerController getInstance(){
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("src/resources/stafficon/"+staff+".png");
        Image image = new Image(file.toURI().toString());
        staffIcon.setImage(image);

        if(customerModel.isDatabaseConnected()){
            dbStatus.setText("Connected");
            dbStatus.setTextFill(Color.GREEN);
        }else{
            dbStatus.setText("Not Connected");
            dbStatus.setTextFill(Color.RED);
        }

        customerButton.setDisable(true);

        try {
            setStaff(staff, staffName, staffID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        loopCombo();
        new AutoCompleteBox(genderInput);
        new AutoCompleteBox(agegpInput);
        new AutoCompleteBox(countryInput);
    }

    //delete data
    public void deleteRecord(ActionEvent event) throws Exception {
        conn = null;
        Statement stmt = null;

        items = customerTable.getItems();

        try {
            for (CustomerData item : items) {
                if (item.getRemark().isSelected()) {
                    String sqlD = "Delete from customer Where cid = "+item.getCid();
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
        Pane root = (Pane)loader.load(getClass().getResource("/customer/customerImport/importPage.fxml").openStream());
        customer.customerImport.ImportController importController = (customer.customerImport.ImportController)loader.getController();
        Scene scene = new Scene(root);
        editStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        editStage.setScene(scene);
        editStage.setTitle("Inventory System");
        editStage.setResizable(false);
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.show();
    }

    //export database to Excel
    public void exportToExcel(ActionEvent event) throws SQLException, IOException {
        File selectedDirectory = selectPath("Customer");
        if(selectedDirectory == null){
            //No Directory selected
        }else {
            String fileTypeName = selectedDirectory.getName();
            String[] parts = fileTypeName.split(Pattern.quote("."));
            String part2 = parts[1];
            conn = dbConnection.getConntection();
            PreparedStatement stmt = null;
            ResultSet rs = null;
            String query = "Select * from customer";

            if (part2.toLowerCase().equals("xlsx")) {
                try {
                    stmt = conn.prepareStatement(query);
                    rs = stmt.executeQuery();

                    XSSFWorkbook wb = new XSSFWorkbook();//for earlier version use HSSF
                    XSSFSheet sheet = wb.createSheet("Customer Details");
                    XSSFRow header = sheet.createRow(0);
                    header.createCell(0).setCellValue("CID");
                    header.createCell(1).setCellValue("FName");
                    header.createCell(2).setCellValue("LName");
                    header.createCell(3).setCellValue("Gender");
                    header.createCell(4).setCellValue("Age Group");
                    header.createCell(5).setCellValue("Country");
                    header.createCell(6).setCellValue("Address1");
                    header.createCell(7).setCellValue("Address2");
                    header.createCell(8).setCellValue("Address3");
                    header.createCell(9).setCellValue("District");
                    header.createCell(10).setCellValue("Email");
                    header.createCell(11).setCellValue("Phone");

                    sheet.autoSizeColumn(1);
                    sheet.autoSizeColumn(2);
                    sheet.autoSizeColumn(3);
                    sheet.autoSizeColumn(4);
                    sheet.autoSizeColumn(5);
                    sheet.autoSizeColumn(6);
                    sheet.autoSizeColumn(7);
                    sheet.autoSizeColumn(8);
                    sheet.autoSizeColumn(9);
                    sheet.autoSizeColumn(10);
                    sheet.autoSizeColumn(11);

                    sheet.setZoom(150);

                    int index = 1;
                    while (rs.next()) {
                        XSSFRow row = sheet.createRow(index);
                        row.createCell(0).setCellValue(rs.getString("CID"));
                        row.createCell(1).setCellValue(rs.getString("FName"));
                        row.createCell(2).setCellValue(rs.getString("LName"));
                        row.createCell(3).setCellValue(rs.getString("Gender"));
                        row.createCell(4).setCellValue(rs.getString("AgeGroup"));
                        row.createCell(5).setCellValue(rs.getString("Country"));
                        row.createCell(6).setCellValue(rs.getString("Address1"));
                        row.createCell(7).setCellValue(rs.getString("Address2"));
                        row.createCell(8).setCellValue(rs.getString("Address3"));
                        row.createCell(9).setCellValue(rs.getString("District"));
                        row.createCell(10).setCellValue(rs.getString("Email"));
                        row.createCell(11).setCellValue(rs.getString("Phone"));
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
            }else{
                if(part2.toLowerCase().equals("csv")) {
                    Writer writer = null;
                    try {
                        File file = new File(selectedDirectory.getAbsolutePath());
                        writer = new BufferedWriter(new FileWriter(file));
                        stmt = conn.prepareStatement(query);
                        rs = stmt.executeQuery();
                        while (rs.next()) {
                            String text = rs.getString("CID") + "," + rs.getString("FName") + "," +
                                    rs.getString("LName")+ "," + rs.getString("Gender")+ ","
                                    + rs.getString("AgeGroup")+ "," + rs.getString("Country") +","+
                                    rs.getString("Address1") + "," + rs.getString("Address2") + "," + rs.getString("Address3")+ "," + rs.getString("District")+","+rs.getString("Email")+","+ rs.getString("Phone")+ "\n";
                            writer.write(text);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    finally {
                        writer.flush();
                        writer.close();
                    }
                }
            }
        }
        conn.close();
    }

    //export selected data to excel
    public void dataToExcel(ActionEvent event) throws SQLException{
        conn =  dbConnection.getConntection();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        items = customerTable.getItems();
        String query = "Select * from customer Where cid IN ";

        try {
            int i = 0;
            for (CustomerData item : items) {
                if (item.getRemark().isSelected()) {
                    if (i == 0) {
                        query += "('" + item.getCid() + "'";
                    }else
                        query += "," + "'" + item.getCid() + "'";
                    i += 1;
                }
            }

            if(i !=0){
                try{
                    File selectedDirectory = selectPath("Customer");
                    if(selectedDirectory == null){
                        //No Directory selected
                    }else {
                        String fileTypeName = selectedDirectory.getName();
                        String[] parts = fileTypeName.split(Pattern.quote("."));
                        String part2 = parts[1];
                        query += ")";
                        if (part2.toLowerCase().equals("xlsx")) {
                            conn = dbConnection.getConntection();
                            stmt = conn.prepareStatement(query);
                            rs = stmt.executeQuery();
                            XSSFWorkbook wb = new XSSFWorkbook();//for earlier version use HSSF
                            XSSFSheet sheet = wb.createSheet("Customer Details");
                            XSSFRow header = sheet.createRow(0);
                            header.createCell(0).setCellValue("CID");
                            header.createCell(1).setCellValue("FName");
                            header.createCell(2).setCellValue("LName");
                            header.createCell(3).setCellValue("Gender");
                            header.createCell(4).setCellValue("Age Group");
                            header.createCell(5).setCellValue("Country");
                            header.createCell(6).setCellValue("Address1");
                            header.createCell(7).setCellValue("Address2");
                            header.createCell(8).setCellValue("Address3");
                            header.createCell(9).setCellValue("District");
                            header.createCell(10).setCellValue("Email");
                            header.createCell(11).setCellValue("Phone");
                            sheet.autoSizeColumn(1);
                            sheet.autoSizeColumn(2);
                            sheet.autoSizeColumn(3);
                            sheet.autoSizeColumn(4);
                            sheet.autoSizeColumn(5);
                            sheet.autoSizeColumn(6);
                            sheet.autoSizeColumn(7);
                            sheet.autoSizeColumn(8);
                            sheet.autoSizeColumn(9);
                            sheet.autoSizeColumn(10);
                            sheet.autoSizeColumn(11);
                            sheet.setZoom(150);

                            int index = 1;
                            while (rs.next()) {
                                XSSFRow row = sheet.createRow(index);
                                row.createCell(0).setCellValue(rs.getString("CID"));
                                row.createCell(1).setCellValue(rs.getString("FName"));
                                row.createCell(2).setCellValue(rs.getString("LName"));
                                row.createCell(3).setCellValue(rs.getString("Gender"));
                                row.createCell(4).setCellValue(rs.getString("AgeGroup"));
                                row.createCell(5).setCellValue(rs.getString("Country"));
                                row.createCell(6).setCellValue(rs.getString("Address1"));
                                row.createCell(7).setCellValue(rs.getString("Address2"));
                                row.createCell(8).setCellValue(rs.getString("Address3"));
                                row.createCell(9).setCellValue(rs.getString("District"));
                                row.createCell(10).setCellValue(rs.getString("Email"));
                                row.createCell(11).setCellValue(rs.getString("Phone"));
                                index++;
                            }

                            FileOutputStream fileOut = new FileOutputStream(selectedDirectory.getAbsolutePath());//before 2007 version xls
                            wb.write(fileOut);
                            fileOut.close();

                            stmt.close();
                            rs.close();
                            AlertBox.informationBox("Finish the export");
                        } else {
                            if (part2.toLowerCase().equals("csv")) {
                                Writer writer = null;
                                try {
                                    File file = new File(selectedDirectory.getAbsolutePath());
                                    writer = new BufferedWriter(new FileWriter(file));
                                    stmt = conn.prepareStatement(query);
                                    rs = stmt.executeQuery();
                                    while (rs.next()) {
                                        String text = rs.getString("CID") + "," + rs.getString("FName") + "," +
                                                rs.getString("LName")+ "," + rs.getString("Gender")+ ","
                                                + rs.getString("AgeGroup")+ "," + rs.getString("Country") +","+
                                                rs.getString("Address1") + "," + rs.getString("Address2") + "," + rs.getString("Address3")+ "," + rs.getString("District")+","+rs.getString("Email")+","+ rs.getString("Phone")+ "\n";
                                        writer.write(text);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                finally {
                                    writer.flush();
                                    writer.close();
                                }
                            }
                        }
                    }
                }catch (SQLException | FileNotFoundException ex){
                    ex.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                AlertBox.informationBox("You doesn't select the record");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.close();
    }

    //clear fields
    public void clearField(){
        cidInput.clear();
        lnameInput.clear();
        fnameInput.clear();
        phoneInput.clear();
        emailInput.clear();
        addressInput.clear();
        districtInput.clear();
        agegpInput.setValue(null);
        genderInput.setValue(null);
        countryInput.setValue(null);
    }

    public void loadData() throws SQLException {
        conn = dbConnection.getConntection();
        this.data = FXCollections.observableArrayList();
        String sql = "SELECT * FROM customer";

        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()){
            this.data.add(new CustomerData(rs.getString(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getString(6),
                    rs.getString(7)+"  "+rs.getString(8)+"  "+rs.getString(9),
                    rs.getString(10),rs.getString(11),rs.getString(12)));
        }
        rs.close();
        conn.close();

        setTableData();
    }

    //log out
    @FXML
    public void logOut(ActionEvent event) throws Exception{
        super.logOut(exitButton);
        customerModel.exitDatabase();
    }

    //check whether has the data selected
    @FXML
    public void handlEditOption(ActionEvent event) throws IOException {
        CustomerData selectedForEdit = customerTable.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null) {
            return;
        }
        switchEdit(selectedForEdit);
    }

    //open the edit page
    public void switchEdit(CustomerData selectedForEdit) throws IOException{
        Stage editStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/customer/customerEdit/edit.fxml").openStream());
        customer.customerEdit.EditController editController = (customer.customerEdit.EditController)loader.getController();
        editController.inflateUI(selectedForEdit);
        Scene scene = new Scene(root);
        editStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        editStage.setScene(scene);
        editStage.setTitle("Inventory System");
        editStage.setResizable(false);
        editStage.initModality(Modality.APPLICATION_MODAL);
        editStage.show();
    }

    //go to add data window
    public void switchAdd(ActionEvent event) throws IOException{
        Stage addStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        Pane root = (Pane)loader.load(getClass().getResource("/customer/customerAdd/add.fxml").openStream());
        customer.customerAdd.AddController addController = (customer.customerAdd.AddController)loader.getController();
        Scene scene = new Scene(root);
        addStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        addStage.setScene(scene);
        addStage.setTitle("Inventory System");
        addStage.setResizable(false);
        addStage.initModality(Modality.APPLICATION_MODAL);
        addStage.show();
    }

    //filter
    @FXML
    public void filterData (ActionEvent event) throws SQLException{
        try{
            if (!cidInput.getText().equals("")){
                if(!andT){
                    sql += " where Customer.CID = " + cidInput.getText();
                    andT = true;
                }else {
                    sql += " and Customer.CID = " + cidInput.getText();
                }
            }
            if (!fnameInput.getText().equals("")){
                if(!andT){
                    sql+= " where Customer.FName LIKE '%" + fnameInput.getText() +"%'";
                    andT = true;
                }else {
                    sql += " and Customer.FName LIKE '%" + fnameInput.getText() + "%'";
                }
            }
            if (!lnameInput.getText().equals("")){
                if(!andT){
                    sql+= " where Customer.LName LIKE '%" + lnameInput.getText() +"%'";
                    andT = true;
                }else {
                    sql += " and Customer.LName LIKE '%" + lnameInput.getText() +"%'";
                }
            }
            if ((agegpInput.getSelectionModel().getSelectedItem() != null) && agegpInput.getSelectionModel().getSelectedItem() != ""){
                if(!andT){
                    sql+= " where Customer.AgeGroup LIKE '%" + agegpInput.getValue() + "%'";
                    andT = true;
                }else {
                    sql += " and Customer.AgeGroup LIKE '%" + agegpInput.getValue() + "%'";
                }
            }
            if (genderInput.getSelectionModel().getSelectedItem() != null && genderInput.getSelectionModel().getSelectedItem() != ""){
                if(!andT){
                    sql+= " where Customer.Gender = '" + genderInput.getSelectionModel().getSelectedItem() + "'";
                    andT = true;
                }else {
                    sql += " and Customer.Gender = '" + genderInput.getSelectionModel().getSelectedItem() + "'";
                }
            }
            if (countryInput.getSelectionModel().getSelectedItem() != null && countryInput.getSelectionModel().getSelectedItem() != ""){
                if(!andT){
                    sql+= " where Customer.Country = '" + countryInput.getSelectionModel().getSelectedItem() + "'";
                    andT = true;
                }else {
                    sql += " and Customer.Country = '" + countryInput.getSelectionModel().getSelectedItem() + "'";
                }
            }
            if (!phoneInput.getText().equals("")){
                if(!andT){
                    sql+= " where Customer.Phone LIKE '%" + phoneInput.getText() + "%'";
                    andT = true;
                }else {
                    sql += " and Customer.Phone LIKE '%" + phoneInput.getText() + "%'";
                }
            }
            if (!emailInput.getText().equals("")){
                if(!andT){
                    sql+= " where Customer.Email LIKE '%" + emailInput.getText() + "%'";
                    andT = true;
                }else {
                    sql += " and Customer.Email LIKE '%" + emailInput.getText() + "%'";
                }
            }
            if (!addressInput.getText().equals("")){
                if(!andT){
                    sql+= " where Customer.Address LIKE '%" + addressInput.getText() + "%'";
                    andT = true;
                }else {
                    sql += " and Customer.Address LIKE '%" + addressInput.getText() + "%'";
                }
            }


            conn = dbConnection.getConntection();
            this.data = FXCollections.observableArrayList();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                this.data.add(new CustomerData(rs.getString(1),rs.getString(2),rs.getString(3),
                        rs.getString(4),rs.getString(5),rs.getString(6),
                        rs.getString(7)+"  "+rs.getString(8)+"  "+rs.getString(9),
                        rs.getString(10),rs.getString(11),rs.getString(12)));
            }
            sql = "SELECT Customer.* FROM Customer";
            andT = false;
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }finally {
            sql = "SELECT Customer.* FROM Customer";
            andT = false;
        }
        setTableData();
    }
    //loop the combobox of country
    public void loopCombo(){
        try{
            String sql = "SELECT Country.name FROM Country";
            conn = dbConnection.getConntection();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String countryName = rs.getString(1);
                countryInput.getItems().add(countryName);
            }
            conn.close();
            rs.close();

        } catch (SQLException ex) {
            System.err.println("Error " + ex);
        }

    }

    public File selectPath(String name) {
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        String userDir = System.getProperty("user.home");
        fileChooser.setInitialDirectory(new File(userDir +"/Desktop"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel(*.xlsx)", "*.xlsx"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV(*.csv)", "*.csv"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        fileChooser.setInitialFileName("Customer"+ LocalDateTime.now().format(format));
        File selectedDirectory = fileChooser.showSaveDialog(stage);
        return selectedDirectory;
    }

    public void setTableData(){
        this.col_cid.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("cid"));
        this.col_Lname.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("lname"));
        this.col_Fname.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("fname"));
        this.col_Gender.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("gender"));
        this.col_Country.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("country"));
        this.col_AgeGp.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("agegroup"));
        this.col_Phone.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("phone"));
        this.col_Email.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("email"));
        this.col_Address.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("address"));
        this.col_District.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("district"));
        this.col_sel.setCellValueFactory(new PropertyValueFactory<CustomerData, String>("remark"));

        this.customerTable.setItems(null);
        this.customerTable.setItems(this.data);
    }
}


