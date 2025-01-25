package gov.iti.jets.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConnectionManager {
    private static DatabaseConnectionManager inst = null;
    private DataSource ds = null;

    private DatabaseConnectionManager() {
        ds = getHikariDataSource();
    }

    public Connection getConnection() {
        
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static DataSource getHikariDataSource() {
        Properties props = new Properties();
        HikariConfig config = new HikariConfig();

        try (InputStream is = DatabaseConnectionManager.class.getResourceAsStream("/db.properties")) {
            if (is == null) {
                throw new FileNotFoundException("Properties file '/db.properties' not found in the classpath.");
            }
            props.load(is);

            config.setJdbcUrl(props.getProperty("MYSQL_DB_URL"));
            config.setUsername(props.getProperty("MYSQL_DB_USERNAME"));
            config.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new HikariDataSource(config);
    }

    public static synchronized DatabaseConnectionManager getInstance() {
        if (inst == null) {
            inst = new DatabaseConnectionManager();
        }
        return inst;
    }
}