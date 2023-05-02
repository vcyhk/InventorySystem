package dbUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbConnection {
    //  Database credentials
    private static final String Account = "root";
    private static final String Password = "root";


    // JDBC driver name and database URL
    private static final String SQCONN = "jdbc:sqlite:sql/db.sqlite";

    public static Connection getConntection() throws SQLException {

        try{
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(SQCONN);
        }catch(ClassNotFoundException ex){
            ex.printStackTrace();
        }
        return null;
    }
}
