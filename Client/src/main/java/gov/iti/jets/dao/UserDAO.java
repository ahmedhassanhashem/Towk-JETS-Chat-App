package gov.iti.jets.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import gov.iti.jets.client.Images;
import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;

public class UserDAO {
    DatabaseConnectionManager meh;
    Images images = new Images();
    public UserDAO() {
        meh = DatabaseConnectionManager.getInstance();
    }

    public UserDTO create(UserDTO user) {
                String phone =user.getPhone(); String name = user.getName(); String country = user.getCountry();
                Gender gender = user.getGender(); String email = user.getEmail(); Date birthdate = user.getBirthdate();
                String password =user.getPassword(); Boolean firstLogin = true; UserStatus userStatus = UserStatus.ONLINE;
                

           if(phone.length() !=11 || name.length() ==0 || password.length() ==0 || birthdate ==null )return null;
   String sql2 = "INSERT INTO `User` (`phone`, `name` , `country`, `gender`, `email`, `birthdate`,`password`, `firstLogin`, `userStatus`) VALUES(?,?,?,?,?,?,?,?,?)";
   try (Connection con = meh.getConnection();
        PreparedStatement preparedStatement = con.prepareStatement(sql2);){
   
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

    public UserDTO read(UserDTO user) { // u can make it take String and String (phone and password)
        // return null; // change the logic to return user and make condition in the
        // controller
        // or u can create default method in DAO or create it in the userDAO
        // this is a very simple structure

        String sql2 = "Select * From User where phone = ? And password = ?";
        ResultSet re;
        try (Connection con = meh.getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(sql2);){
            
            preparedStatement.setString(1, user.getPhone());
            preparedStatement.setString(2, user.getPassword());
            re = preparedStatement.executeQuery();

            return convert(re);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;

    }

    public String read(int id) { 

        String sql2 = "Select name From User where userId = ?";
        ResultSet re;
        try (Connection con = meh.getConnection();
            PreparedStatement preparedStatement = con.prepareStatement(sql2);){
            
            preparedStatement.setInt(1, id);
            re = preparedStatement.executeQuery();
            re.next();
            return re.getString(1);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;

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
            // try {
            //     user.setUserPicture(images.downloadPP(re.getString(("userPicture"))));
            // } catch (IllegalArgumentException | NullPointerException e) {
            //     user.setUserPicture(null);
            // }
            if(re.getString("userPicture") != null && re.getString("userPicture").length()>0) {
                user.setUserPicture(images.downloadPP(re.getString("userPicture")));
            } else  {
                // System.out.println("why");
                user.setUserPicture(null);
            }

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int updatePicture(int userID , String fileName, byte[] userPicture) {
        String query = "Update User \r\n" +
                        "SET userPicture = ?\r\n" +
                        "WHERE userID = ? \r\n";
        try (Connection con = meh.getConnection();
            PreparedStatement stmnt = con.prepareStatement(query);){

                if (userPicture != null) {
                    images.uploadPP(fileName, userPicture);
                    stmnt.setString(1, fileName); 
                }
                else
                stmnt.setNull(1, Types.CHAR);


                stmnt.setInt(2, userID); 

                int rowsUpdated = stmnt.executeUpdate();
                if(rowsUpdated > 0)
                    return rowsUpdated;

            }catch(SQLException e){e.printStackTrace();}
        return 0; 
    }


    public int update(int userID, String name, String bio, UserMode userMode) {
        String query = "Update User \r\n" +
                        "SET name = ?, bio = ?, userMode = ?\r\n" +
                        "WHERE userID = ? \r\n";
        try (Connection con = meh.getConnection();
            PreparedStatement stmnt = con.prepareStatement(query);){
                stmnt.setString(1, name);
                stmnt.setString(2, bio); 

                if (userMode != null) 
                    stmnt.setString(3, userMode.name()); 

                stmnt.setInt(4, userID); 

                int rowsUpdated = stmnt.executeUpdate();
                if(rowsUpdated > 0)
                    return rowsUpdated;

            }catch(SQLException e){e.printStackTrace();}
        return 0; 
    }

    public int updatePassword(int userID ,String password) {
        String query = "Update User \r\n" +
                        "SET password = ?\r\n" +
                        "WHERE userID = ? \r\n";
        try (Connection con = meh.getConnection();
            PreparedStatement stmnt = con.prepareStatement(query);){
                stmnt.setString(1, password);
                stmnt.setInt(2, userID); 

            int rowsUpdated = stmnt.executeUpdate();
            if (rowsUpdated > 0)
                return rowsUpdated;
            
            }catch(SQLException e){e.printStackTrace();}
            return 0;
    }


    public void delete(int userID) {
        String query = "DELETE FROM User \r\n" +
                        "WHERE userID = ? \r\n";
        try (Connection con = meh.getConnection();
            PreparedStatement stmnt = con.prepareStatement(query);){
                stmnt.setInt(1, userID); 
                int rowsDeleted = stmnt.executeUpdate();

                if (rowsDeleted > 0) 
                    System.out.println("User deleted successfully!");
                else 
                    System.out.println("User not found!");
                
        
            }catch(SQLException e){e.printStackTrace();}
    }


}
