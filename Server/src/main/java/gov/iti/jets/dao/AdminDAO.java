package gov.iti.jets.dao;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gov.iti.jets.client.ClientInt;
import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;
import gov.iti.jets.server.Images;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class AdminDAO  implements AdminDAOInterface {



    DatabaseConnectionManager meh;
    Images images = new Images();

    // Connection con;
    public AdminDAO() {
        meh = DatabaseConnectionManager.getInstance();
        // con = meh.getConnection();
    }


    @Override
    public UserDTO read(UserDTO user) { 

        String sql2 = "Select * From Admin where phone = ? And password = ?";
        ResultSet re;
        try (Connection con = meh.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql2);) {

            preparedStatement.setString(1, user.getPhone());
            preparedStatement.setString(2, user.getPassword());
            re = preparedStatement.executeQuery();
            UserDTO ret = convert(re);

            return ret;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;

    }






    private UserDTO convert(ResultSet re) {
        UserDTO user = new UserDTO();
        try {
            if (!re.next()) {
                return null;
            }
            // re.next();
            user.setUserID(re.getInt("userID"));
            user.setPhone(re.getString("phone"));
           
            user.setPassword(re.getString("password"));
           

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


 
    @Override
    public int updatePassword(int userID, String password) {
        String query = "Update Admin \r\n"
                + "SET password = ?\r\n"
                + "WHERE userID = ? \r\n";
        try (Connection con = meh.getConnection(); PreparedStatement stmnt = con.prepareStatement(query);) {
            stmnt.setString(1, password);
            stmnt.setInt(2, userID);

            int rowsUpdated = stmnt.executeUpdate();
            if (rowsUpdated > 0) {
                return rowsUpdated;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int updateFirstLogin(int userID) {
        String sql = "update Admin set firstLogin = 0 where  userID = ?;";
        String sql2 = "select * from Admin where userID = ? and firstLogin = 1;";
        int ret = 0;
        try (Connection con = meh.getConnection();
                PreparedStatement stmnt = con.prepareStatement(sql);
                PreparedStatement preparedStatement = con.prepareStatement(sql2);) {
            stmnt.setInt(1, userID);
            preparedStatement.setInt(1, userID);
            ResultSet re = preparedStatement.executeQuery();
            ret = re.isBeforeFirst() ? 1 : 0;
            stmnt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public void delete(int userID) {
        String query = "DELETE FROM User \r\n"
                + "WHERE userID = ? \r\n";
        try (Connection con = meh.getConnection(); PreparedStatement stmnt = con.prepareStatement(query);) {
            stmnt.setInt(1, userID);
            int rowsDeleted = stmnt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("User deleted successfully!");
            } else {
                System.out.println("User not found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 
  
}
