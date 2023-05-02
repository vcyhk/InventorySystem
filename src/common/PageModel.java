package common;

import dbUtil.dbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import dbUtil.dbConnection;

public class PageModel {

    Connection connection;
    PreparedStatement pr = null;
    ResultSet rs =null;

    public PageModel(){
        try{
            this.connection = dbConnection.getConntection();
        }catch(SQLException ex){
            ex.printStackTrace();
        }if (this.connection == null){
            System.exit(1);
        }
    }

    public boolean isDatabaseConnected(){
        return this.connection != null;
    }

    public void exitDatabase() throws SQLException {
        connection.close();
    }

}
