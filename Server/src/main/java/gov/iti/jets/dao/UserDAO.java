package gov.iti.jets.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import gov.iti.jets.dto.UserDTO;

public class UserDAO implements DAO<UserDTO>{

    @Override
    public UserDTO create(UserDTO user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public UserDTO read(UserDTO user) { // u can make it take String and String (phone and password)
        return null; // change the logic to return user and make condition in the controller
                    // or u can create default method in DAO or create it in the userDAO 
                    // this is a very simple structure


        // String sql2 = "Select * From User where phone = ? And password = ?";
        // ResultSet re;
        // try {
            // PreparedStatement preparedStatement = con.prepareStatement(sql2);
                // preparedStatement.setString(1, phone);
                // preparedStatement.setString(2, password);
                // re =preparedStatement.executeQuery();
            //     if(re.next()){
            //         return true;
            //     }else return false;
            // } catch (SQLException e) {
            //     // TODO Auto-generated catch block
            //     e.printStackTrace();
            //     return false;
            // }
            
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }
    

}
