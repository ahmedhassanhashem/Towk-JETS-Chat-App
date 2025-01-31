package gov.iti.jets.controller;

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
import java.io.IOException;
import gov.iti.jets.dao.ContactDAO;
import gov.iti.jets.dto.UserDTO;

public class NotificationController {
    private ContactDAO contactDAO = new ContactDAO();
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
        listView.setItems(contacts);
    }

    public void loadNotifications() {
        System.out.println("UserDTO phone in NotificationController: " + userDTO.getPhone());
    
        ObservableList<UserDTO> pendingRequests = contactDAO.findAllContactsPENDING(userDTO.getPhone());
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
                                    if (contactDAO.acceptContactRequest(userDTO.getPhone(), user.getPhone())) {
                                        contacts.remove(user);
                                        System.out.println("Request accepted");
                                    }
                                });
    
                                controller.getRejectButton().setOnAction(e -> {
                                    if (contactDAO.rejectContactRequest(userDTO.getPhone(), user.getPhone())) {
                                        contacts.remove(user);
                                        System.out.println("Request rejected");
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