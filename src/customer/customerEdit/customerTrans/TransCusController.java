package customer.customerEdit.customerTrans;

import customer.customerEdit.EditController;
import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import transaction.TransactionData;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TransCusController implements Initializable{

    private dbConnection dc;
    @FXML
    private Label cNameLabel, cidLabel;
    @FXML
    private TableView <TransCusData>transTable;
    @FXML
    private TableColumn<TransCusData, String> col_tid, col_Location,col_teamCat, col_itemDesc, col_unitPrice, col_NetAmt;
    private ObservableList<TransCusData> data;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.dc = new dbConnection();
        cidLabel.setText(EditController.getcidT);
        cNameLabel.setText(EditController.getcNameT);
        try {
            loadItem();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadItem() throws SQLException {
        Connection conn = dbConnection.getConntection();
        this.data = FXCollections.observableArrayList();
        String sql = "SELECT TransactionID, Location, TransactionDateTime, TransactionAmount, DiscountAmount, NetAmount FROM Transactions WHERE CustomerID = "+EditController.getcidT;

        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            this.data.add(new TransCusData(rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)
            ));
        }

        this.col_tid.setCellValueFactory(new PropertyValueFactory<TransCusData, String>("tid"));
        this.col_Location.setCellValueFactory(new PropertyValueFactory<TransCusData, String>("location"));
        this.col_teamCat.setCellValueFactory(new PropertyValueFactory<TransCusData, String>("tranDT"));
        this.col_itemDesc.setCellValueFactory(new PropertyValueFactory<TransCusData, String>("tranAmount"));
        this.col_unitPrice.setCellValueFactory(new PropertyValueFactory<TransCusData, String>("discount"));
        this.col_NetAmt.setCellValueFactory(new PropertyValueFactory<TransCusData, String>("netAmount"));

        this.transTable.setItems(null);
        this.transTable.setItems(this.data);


    }


}
