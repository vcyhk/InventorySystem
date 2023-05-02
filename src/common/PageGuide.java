package common;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;

public interface PageGuide {
    public void deleteRecord(ActionEvent event) throws Exception;
    public void exportToExcel(ActionEvent event) throws SQLException, IOException;
    public void dataToExcel(ActionEvent event) throws SQLException;
    public void clearField();
    public void loadData() throws SQLException;
    public void logOut(ActionEvent event) throws Exception;
    public void handlEditOption(ActionEvent event) throws IOException;
    //public void switchEdit(CardData selectedForEdit) throws IOException;
    public void switchAdd(ActionEvent event) throws IOException;
    public void filterData(ActionEvent event)throws SQLException;
    public void setTableData();
}
