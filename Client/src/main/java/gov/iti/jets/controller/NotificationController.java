package gov.iti.jets.controller;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dto.UserDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class NotificationController {
        private ContactDAOInterface contactDAO;

    ObservableList<UserDTO> contacts = FXCollections.observableArrayList();
    private UserDTO userDTO = new UserDTO();
    private Stage stage;
    private Scene settingsScene;
    private Scene dashboardScene;

    @FXML
    private ListView<UserDTO> listView;

    public void setSettingsScene(Scene s) {
        settingsScene = s;
    }

    public void setDashboardScene(Scene s) {
        dashboardScene = s;
    }

    public void setStage(Stage s) {
        stage = s;
    }

    public void setUserDTO(UserDTO user) {
        this.userDTO = user;
        System.out.println("NotificationController userDTO set: " + user.getPhone());
    }
    

    @FXML
    private void initialize() {
        Properties props = new Properties();

        try (InputStream input = getClass().getResourceAsStream("/rmi.properties")) {
            if (input == null) {
                throw new IOException("Properties file not found");
            }
            props.load(input);
        } catch (IOException ex) {
        }

        String ip = props.getProperty("rmi_ip");
        int port = Integer.parseInt(props.getProperty("rmi_port"));

        Registry reg;
        try {
            reg = LocateRegistry.getRegistry(ip, port);
            contactDAO = (ContactDAOInterface) reg.lookup("contactDAO");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        listView.setItems(contacts);
    }

    public void loadNotifications() {
        System.out.println("UserDTO phone in NotificationController: " + userDTO.getPhone());
    
        ObservableList<UserDTO> pendingRequests = FXCollections.observableArrayList();
        try {
            pendingRequests = FXCollections.observableArrayList(contactDAO.findAllContactsPENDING(userDTO.getPhone()));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("DAO returned: " + pendingRequests.size() + " pending requests");
    
        listView.setCellFactory(new Callback<ListView<UserDTO>, ListCell<UserDTO>>() {
            @Override
            public ListCell<UserDTO> call(ListView<UserDTO> p) {
                return new ListCell<UserDTO>() {
                    @Override
                    protected void updateItem(UserDTO user, boolean empty) {
                        super.updateItem(user, empty);
    
                        if (empty || user == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            try {
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/FriendRequestCard.fxml"));
                                HBox requestCard = loader.load();
                                FriendRequestController controller = loader.getController();
    
                                controller.setPicture(user.getUserPicture());
                                controller.setName(user.getName());
    
                                controller.getAcceptButton().setOnAction(e -> {
                                    try {
                                        if (contactDAO.acceptContactRequest(userDTO.getPhone(), user.getPhone())) {
                                            contacts.remove(user);
                                            System.out.println("Request accepted");
                                        }
                                    } catch (RemoteException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    }
                                });
    
                                controller.getRejectButton().setOnAction(e -> {
                                    try {
                                        if (contactDAO.rejectContactRequest(userDTO.getPhone(), user.getPhone())) {
                                            contacts.remove(user);
                                            System.out.println("Request rejected");
                                        }
                                    } catch (RemoteException e1) {
                                        // TODO Auto-generated catch block
                                        e1.printStackTrace();
                                    }
                                });
    
                                setGraphic(requestCard);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
            }
        });
    
        contacts.addAll(pendingRequests);
        System.out.println("Contacts size after adding: " + contacts.size());
    }
    
}