package gov.iti.jets.dao;

import java.rmi.Remote;
import java.rmi.RemoteException;

import gov.iti.jets.client.ClientInt;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserMode;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public interface AdminDAOInterface {


    public UserDTO read(UserDTO user) ;


    public int updatePassword(int userID ,String password) ;
    public int updateFirstLogin(int userID);

    public void delete(int userID) ;

}
