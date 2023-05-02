package loginapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dbUtil.dbConnection;

public class LoginModel {

    Connection connection;

    public LoginModel(){
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

    public boolean isLogin(String user, String pass) throws SQLException {
        PreparedStatement pr = null;
        ResultSet rs =null;

        String sql ="Select * From staff where Account = ? and password = ?";
        try{
            pr = this.connection.prepareStatement(sql);
            pr.setString(1, user);
            pr.setString(2, pass);

            rs = pr.executeQuery();

            boolean boll1;

            if(rs.next()) {
                return true;
            }
            return false;
        }catch(SQLException ex){
            ex.printStackTrace();
            return false;
        }finally {
            pr.close();
            rs.close();
        }

    }

    public void exitDatabase() throws SQLException {
        connection.close();
    }

}
