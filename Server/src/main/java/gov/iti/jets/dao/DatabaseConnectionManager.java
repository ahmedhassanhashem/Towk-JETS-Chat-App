package gov.iti.jets.dao;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import gov.iti.jets.config.DBConfig;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

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
        HikariConfig config = new HikariConfig();


        try{

        File XMLfile = new File(DatabaseConnectionManager.class.getResource("/db.xml").toURI()); 
        JAXBContext context = JAXBContext.newInstance(DBConfig.class);
        Unmarshaller unmarshaller = context.createUnmarshaller(); 
        DBConfig p = (DBConfig) unmarshaller.unmarshal(XMLfile);
            config.setJdbcUrl(p.getURL());
            config.setUsername(p.getUsername());
            config.setPassword(p.getPassword());
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);
        } catch (URISyntaxException ex) {
            ex.printStackTrace();

        } catch (JAXBException ex) {
            ex.printStackTrace();

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