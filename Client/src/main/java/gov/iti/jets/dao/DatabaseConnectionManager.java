package gov.iti.jets.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.cj.jdbc.MysqlDataSource;


public class DatabaseConnectionManager {
    private static DatabaseConnectionManager inst = null;
    DataSource ds = null;
    Connection con = null;

    private DatabaseConnectionManager() {
        ds = getMySQLDataSource();
        try {
            con = ds.getConnection();
            // stmt =
            // con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
    public Connection getConnection(){
        return con;
    }

    private static DataSource getMySQLDataSource() {
        Properties props = new Properties();
        MysqlDataSource mysqlDS = null;

        try (InputStream is = DatabaseConnectionManager.class.getResourceAsStream("/db.properties")) {
            if (is == null) {
                throw new FileNotFoundException("Properties file '/db.properties' not found in the classpath.");
            }
            props.load(is);

            mysqlDS = new MysqlDataSource();
            mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
            mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
            mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mysqlDS;
    }

    public static synchronized DatabaseConnectionManager getInstance() {
        if (inst == null) {
            inst = new DatabaseConnectionManager();
        }
        return inst;
    }
}
