package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;

public class UserDAO implements DAO<UserDTO> {
    DatabaseConnectionManager meh;
    Connection con;

    public UserDAO() {
        meh = DatabaseConnectionManager.getInstance();
        con = meh.getConnection();

    }

    @Override
    public UserDTO create(UserDTO user) {
                String phone =user.getPhone(); String name = user.getName(); String country = user.getCountry();
                Gender gender = user.getGender(); String email = user.getEmail(); Date birthdate = user.getBirthdate();
                String password =user.getPassword(); Boolean firstLogin = true; UserStatus userStatus = UserStatus.ONLINE;
                

           if(phone.length() !=11 || name.length() ==0 || password.length() ==0 || birthdate ==null )return null;
   String sql2 = "INSERT INTO `User` (`phone`, `name` , `country`, `gender`, `email`, `birthdate`,`password`, `firstLogin`, `userStatus`) VALUES(?,?,?,?,?,?,?,?,?)";
   try {
   PreparedStatement preparedStatement = con.prepareStatement(sql2);
       java.sql.Date birthdate2= java.sql.Date.valueOf(birthdate.toString());
       preparedStatement.setString(1, phone);
       preparedStatement.setString(2, name);
       preparedStatement.setString(3, country);
       preparedStatement.setString(4, gender.toString());
       preparedStatement.setString(5, email);
       preparedStatement.setDate(6, birthdate2);
       preparedStatement.setString(7, password);
       preparedStatement.setBoolean(8, firstLogin);
       preparedStatement.setString(9, userStatus.toString());
       preparedStatement.executeUpdate();
       System.out.println("Record inserted successfully.");
       return user;
   } catch (SQLException e) {
       // TODO Auto-generated catch block
       e.printStackTrace();
    }
    return null;

        // throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public UserDTO read(UserDTO user) { // u can make it take String and String (phone and password)
        // return null; // change the logic to return user and make condition in the
        // controller
        // or u can create default method in DAO or create it in the userDAO
        // this is a very simple structure

        String sql2 = "Select * From User where phone = ? And password = ?";
        ResultSet re;
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql2);
            preparedStatement.setString(1, user.getPhone());
            preparedStatement.setString(2, user.getPassword());
            re = preparedStatement.executeQuery();

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    private UserDTO convert(ResultSet re) {
        UserDTO user = new UserDTO();
        try {
            if (!re.next())
                return null;
            // re.next();
            user.setUserID(re.getInt("userID"));
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

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
