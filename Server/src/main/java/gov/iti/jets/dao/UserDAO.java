package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.RowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.Predicate;
import javax.sql.rowset.RowSetProvider;

import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class UserDAO implements DAO<UserDTO>{
    DatabaseConnectionManager meh;
    Connection con;
    public UserDAO(){
        meh = DatabaseConnectionManager.getInstance();
        con = meh.getConnection();        
    }
    @Override
    public UserDTO create(UserDTO user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public UserDTO read(UserDTO user) { // u can make it take String and String (phone and password)
        // return null; // change the logic to return user and make condition in the controller
                    // or u can create default method in DAO or create it in the userDAO 
                    // this is a very simple structure
        
        String sql2 = "Select * From User where phone = ? And password = ?";
        ResultSet re;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql2);
                preparedStatement.setString(1, user.getPhone());
                preparedStatement.setString(2, user.getPassword());
                re =preparedStatement.executeQuery();

                return convert(re);
            } catch (SQLException e) {
                
                e.printStackTrace();
            }
            return null;

    }

    @Override
    public UserDTO update(UserDTO user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(UserDTO user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<UserDTO> findAll() {
        return null;
    }

    

    public ObservableList<PieChart.Data> getUserStatistics(String columnName) {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        String query = String.format("SELECT %s, COUNT(*) AS count FROM user GROUP BY %s", columnName, columnName);
    
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
    
            while (rs.next()) {
                String label = rs.getString(columnName); 
                int count = rs.getInt("count"); 
                data.add(new PieChart.Data(label, count));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return data;
    }
    

    private UserDTO convert(ResultSet re){
        UserDTO user = new UserDTO();
        try {
            if(!re.next())return null;
            // re.next();
            user.setPhone(re.getString("phone"));
            user.setName(re.getString("name"));
            user.setCountry(re.getString("country"));
            user.setGender(Gender.valueOf(re.getString("gender")));
            user.setEmail(re.getString("email"));
            user.setBirthdate(re.getDate("birthdate"));
            user.setPassword(re.getString("password"));
            user.setFirstLogin(re.getBoolean("firstLogin"));
            user.setUserStatus(UserStatus.valueOf(re.getString("userStatus")));
            try {
                user.setUserMode(UserMode.valueOf(re.getString("userMode")));
            } catch (IllegalArgumentException | NullPointerException e) {
                user.setUserMode(null); 
            }
            try {
                user.setBio(re.getString("bio"));
            } catch (IllegalArgumentException | NullPointerException e) {
                user.setBio(null); 
            }
            try {
                user.setUserPicture(re.getBytes("userPicture"));
            } catch (IllegalArgumentException | NullPointerException e) {
                user.setUserPicture(null); 
            }
            
            user.setUserPicture(re.getBytes("userPicture"));
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}


class test{
    public static void main(String[] args) {
        UserDAO user = new UserDAO();
        ObservableList<PieChart.Data> pieChartData = user.getUserStatistics("country");
        System.out.println(pieChartData.size());
    }
}