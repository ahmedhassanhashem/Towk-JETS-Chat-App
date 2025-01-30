package gov.iti.jets.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;

import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public interface UserDAOInterface extends Remote {

        public UserDTO create(UserDTO user) throws RemoteException ;

    public UserDTO read(UserDTO user) throws RemoteException ;

    public String read(int id) throws RemoteException;

    public ObservableList<PieChart.Data> getUserStatistics(String columnName) throws RemoteException ;


    public int updatePicture(int userID , String fileName, byte[] userPicture) throws RemoteException ;


    public int update(int userID, String name, String bio, UserMode userMode) throws RemoteException ;

    public int updatePassword(int userID ,String password) throws RemoteException ;

    public void delete(int userID) throws RemoteException;
}
