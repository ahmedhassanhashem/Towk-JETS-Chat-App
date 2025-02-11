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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import gov.iti.jets.client.ClientInt;
import gov.iti.jets.dto.Gender;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import gov.iti.jets.dto.UserStatus;
import gov.iti.jets.server.Images;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class UserDAO extends UnicastRemoteObject implements UserDAOInterface {

    ConcurrentHashMap<Integer,CopyOnWriteArrayList<ClientInt>> online;
    ConcurrentHashMap<Integer,CopyOnWriteArrayList<ClientInt>> online2;

    DatabaseConnectionManager meh;
    Images images = new Images();

    // Connection con;
    public UserDAO() throws RemoteException {
        super();
        meh = DatabaseConnectionManager.getInstance();
        online = new ConcurrentHashMap<>();
        online2 = new ConcurrentHashMap<>();
        // con = meh.getConnection();
    }

    @Override
    public void register(int userID,ClientInt clientRef) throws RemoteException {

        online.computeIfAbsent(userID, k -> new CopyOnWriteArrayList<>()).add(clientRef);


    }

    // Unregister a client
    @Override
    public void unRegister(int userID,ClientInt clientRef) throws RemoteException {

        if (online.containsKey(userID)) {
            List<ClientInt> clientList = online.get(userID);
            
            clientList.remove(clientRef);
            
            if (clientList.isEmpty()) {
                online.remove(userID);
            }
        }
    }

    @Override
    public void registerwww(int userID,ClientInt clientRef) throws RemoteException {

        online2.computeIfAbsent(userID, k -> new CopyOnWriteArrayList<>()).add(clientRef);
// System.out.println("hi --> "+online2);

    }

    // Unregister a client
    @Override
    public void unRegisterwww(int userID,ClientInt clientRef) throws RemoteException {

        if (online2.containsKey(userID)) {
            List<ClientInt> clientList = online2.get(userID);
            
            clientList.remove(clientRef);
            
            if (clientList.isEmpty()) {
                online2.remove(userID);
            }
        }
        // System.out.println("bye --> "+online2);
    }


    @Override
    public UserDTO create(UserDTO user) throws RemoteException {
        String phone = user.getPhone();
        String name = user.getName();
        String country = user.getCountry();
        Gender gender = user.getGender();
        String email = user.getEmail();
        Date birthdate = user.getBirthdate();
        String password = user.getPassword();
        Boolean firstLogin = true;
        UserStatus userStatus = UserStatus.ONLINE;

        if (phone.length() != 11 || name.length() == 0 || password.length() == 0 || birthdate == null) {
            return null;
        }
        String sql2 = "INSERT INTO `User` (`phone`, `name` , `country`, `gender`, `email`, `birthdate`,`password`, `firstLogin`, `userStatus`) VALUES(?,?,?,?,?,?,?,?,?)";
        String sql = "select * from User order by userID desc limit 1;";
        try (Connection con = meh.getConnection();
                PreparedStatement preparedStatement = con.prepareStatement(sql2);
                PreparedStatement preparedStatement2 = con.prepareStatement(sql)) {

            java.sql.Date birthdate2 = java.sql.Date.valueOf(birthdate.toString());
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
            ResultSet re = preparedStatement2.executeQuery();
            return convert(re);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

        // throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public UserDTO read(UserDTO user) throws RemoteException { // u can make it take String and String (phone and
                                                               // password)
        // return null; // change the logic to return user and make condition in the
        // controller
        // or u can create default method in DAO or create it in the userDAO
        // this is a very simple structure

        String sql2 = "Select * From User where phone = ? And password = ?";
        ResultSet re;
        try (Connection con = meh.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql2);) {

            preparedStatement.setString(1, user.getPhone());
            preparedStatement.setString(2, user.getPassword());
            re = preparedStatement.executeQuery();
            UserDTO ret = convert(re);
            if(ret!=null){
                ret.setUserStatus(UserStatus.ONLINE);
                for(int i : online.keySet()){
                    if(checkFriend(i,ret.getUserID())){
                        List<ClientInt> clients = online.get(i);
                        List<ClientInt> toRemove = new ArrayList<>();

                        for (ClientInt client : clients) {
                            try {
                                client.sendMessage(ret);
                            } catch (RemoteException e1) {
                                toRemove.add(client);
                                e1.printStackTrace();
                            }
                        }
                
                        clients.removeAll(toRemove);
                    }
                }
            }
            return ret;
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;

    }

    @Override
    public void propagateOffline(UserDTO id) throws RemoteException{
        for(int i : online.keySet()){
            if(checkFriend(i,id.getUserID())){
                List<ClientInt> clients = online.get(i);
                List<ClientInt> toRemove = new ArrayList<>();
                for (ClientInt client : clients) {
                    try {
                        client.sendMessage(id);
                    } catch (RemoteException e1) {
                        toRemove.add(client);
                        // e1.printStackTrace();
                    }
                }
                
                clients.removeAll(toRemove);
            }
        }
    }
    @Override
    public String read(int id) throws RemoteException {

        String sql2 = "Select name From User where userId = ?";
        ResultSet re;
        try (Connection con = meh.getConnection(); PreparedStatement preparedStatement = con.prepareStatement(sql2);) {

            preparedStatement.setInt(1, id);
            re = preparedStatement.executeQuery();
            re.next();
            return re.getString(1);
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return null;

    }

    @Override
    public ObservableList<PieChart.Data> getUserStatistics(String columnName) throws RemoteException {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        String query = String.format("SELECT %s, COUNT(*) AS count FROM User GROUP BY %s", columnName, columnName);

        try (Connection con = meh.getConnection();
                PreparedStatement ps = con.prepareStatement(query);
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

    private UserDTO convert(ResultSet re) {
        UserDTO user = new UserDTO();
        try {
            if (!re.next()) {
                return null;
            }
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
            // user.setUserPicture(images.downloadPP(re.getString(("userPicture"))));
            // } catch (IllegalArgumentException | NullPointerException e) {
            // user.setUserPicture(null);
            // }
            if (re.getString("userPicture") != null && re.getString("userPicture").length() > 0) {
                user.setUserPicture(images.downloadPP(re.getString("userPicture")));
                System.out.println(user.getUserPicture().length);

            } else {
                // System.out.println("why");
                user.setUserPicture(null);
            }

            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int updatePicture(int userID, String fileName, byte[] userPicture) throws RemoteException {
        String query = "Update User \r\n"
                + "SET userPicture = ?\r\n"
                + "WHERE userID = ? \r\n";
        try (Connection con = meh.getConnection(); PreparedStatement stmnt = con.prepareStatement(query);) {

            if (userPicture != null) {
                images.uploadPP(fileName, userPicture);
                stmnt.setString(1, fileName);
            } else {
                stmnt.setNull(1, Types.CHAR);
            }

            stmnt.setInt(2, userID);

            int rowsUpdated = stmnt.executeUpdate();
            if (rowsUpdated > 0) {
                UserDTO ret = new UserDTO();
                ret.setUserPicture(userPicture);
                    ret.setUserStatus(UserStatus.ONLINE);
                    ret.setUserID(userID);
                    for(int i : online.keySet()){
                        if(checkFriend(i,userID)){
                            List<ClientInt> clients = online.get(i);
                            List<ClientInt> toRemove = new ArrayList<>();

                            for (ClientInt client : clients) {
                                try {
                                    client.sendMessage(ret);
                                } catch (RemoteException e1) {
                                    toRemove.add(client); 
                                    e1.printStackTrace();
                                }
                            }
                    
                            clients.removeAll(toRemove);
                        }
                    }
                    for(int i : online2.keySet()){
                        // if(checkFriend(i,userID)){
                            List<ClientInt> clients = online2.get(i);
                            List<ClientInt> toRemove = new ArrayList<>();

                            for (ClientInt client : clients) {
                                try {
                                    client.sendMessage(ret);
                                } catch (RemoteException e1) {
                                    toRemove.add(client); 
                                    e1.printStackTrace();
                                }
                            }
                    
                            clients.removeAll(toRemove);
                        // }
                    }
                
                return rowsUpdated;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public int update(int userID, String name, String bio, UserMode userMode) throws RemoteException {
        UserDTO ret = new UserDTO();
        StringBuilder query = new StringBuilder("UPDATE User SET ");
        List<Object> params = new ArrayList<>();
// System.out.println(online2);
        if (name != null) {
            query.append("name = ?, ");
            params.add(name);
            ret.setName(name);
        }else{
            ret.setName(null);
        }
        if (bio != null) {
            query.append("bio = ?, ");
            params.add(bio);
            ret.setBio(bio);
        }else{
            ret.setBio(null);
        }
        if (userMode != null) {
            query.append("userMode = ?, ");
            params.add(userMode.name());
            ret.setUserMode(userMode);
        }else{
            ret.setUserMode(null);
        }

        if (params.isEmpty()) {
            return 0;
        }

        query.setLength(query.length() - 2);
        query.append(" WHERE userID = ?");
        params.add(userID);

        try (Connection con = meh.getConnection(); PreparedStatement stmnt = con.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmnt.setObject(i + 1, params.get(i));
            }
                ret.setUserStatus(UserStatus.ONLINE);
                ret.setUserID(userID);
                for(int i : online.keySet()){
                    if(checkFriend(i,userID)){
                        List<ClientInt> clients = online.get(i);
                        List<ClientInt> toRemove = new ArrayList<>();
                        for (ClientInt client : clients) {
                            try {
                                client.sendMessage(ret);
                            } catch (RemoteException e1) {
                                toRemove.add(client); 
                                e1.printStackTrace();
                            }
                        }
                
                        clients.removeAll(toRemove);
                    }
                }
                for(int i : online2.keySet()){
                    // if(checkFriend(i,userID)){
                        List<ClientInt> clients = online2.get(i);
                        List<ClientInt> toRemove = new ArrayList<>();
                        for (ClientInt client : clients) {
                            try {
                                client.sendMessage(ret);
                            } catch (RemoteException e1) {
                                toRemove.add(client); 
                                e1.printStackTrace();
                            }
                        }
                
                        clients.removeAll(toRemove);
                    // }
                }
            return stmnt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int updatePassword(int userID, String password) throws RemoteException {
        String query = "Update User \r\n"
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
    public int updateFirstLogin(int userID) throws RemoteException {
        String sql = "update User set firstLogin = 0 where  userID = ?;";
        String sql2 = "select * from User where userID = ? and firstLogin = 1;";
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
    public void delete(int userID) throws RemoteException {
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

    @Override
    public void changeStatus(int userID,String status) throws RemoteException {
        String query = "update User set userStatus = ? where userID = ?;";

        try (Connection con = meh.getConnection(); 
        PreparedStatement stmnt = con.prepareStatement(query);
        ) {

            UserStatus stat= UserStatus.valueOf(status);
            int ret = 0;
            if(stat.equals(UserStatus.ONLINE)){
                stmnt.setString(1, UserStatus.ONLINE.toString());
                stmnt.setInt(2, userID);
                ret = stmnt.executeUpdate();

            }else{
                stmnt.setString(1, UserStatus.OFFLINE.toString());
                stmnt.setInt(2, userID);
                ret = stmnt.executeUpdate();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean checkFriend(int user1, int user2) throws RemoteException {

        String query = "SELECT uc.* FROM UserContact uc  WHERE uc.receiverID = ? AND uc.senderID = ? AND uc.requestStatus = 'ACCEPT' \n" + //
                        "UNION SELECT uc.* FROM UserContact uc  WHERE uc.receiverID = ? AND uc.senderID = ? AND uc.requestStatus = 'ACCEPT' ;";

        try (Connection con = meh.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, user1);
            ps.setInt(2, user2);
            ps.setInt(3, user2);
            ps.setInt(4, user1);
            ResultSet re = ps.executeQuery();
            return re.isBeforeFirst();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

// class test {
// public static void main(String[] args) {
// UserDAO user = new UserDAO();
// ObservableList<PieChart.Data> pieChartData =
// user.getUserStatistics("country");
// System.out.println(pieChartData.size());
// }
// }
