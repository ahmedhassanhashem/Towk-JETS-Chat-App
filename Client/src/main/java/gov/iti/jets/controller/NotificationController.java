package gov.iti.jets.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;

import gov.iti.jets.client.ClientImplNot;
import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.AttachementDAOInterface;
import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dao.MessageDAOInterface;
import gov.iti.jets.dao.NotificationDAOInterface;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.NotificationDTO;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class NotificationController {
    private ContactDAOInterface contactDAO;
    private NotificationDAOInterface notificationDAO;
    private UserDAOInterface userDAO;
    private MessageDAOInterface messageDAO;
    private AttachementDAOInterface attachementDAO;

    ObservableList<UserDTO> contacts = FXCollections.observableArrayList();
    ObservableList<NotificationDTO> notifications = FXCollections.observableArrayList();
    private UserDTO userDTO = new UserDTO();
    private Stage stage;
    private Scene settingsScene;
    private Scene dashboardScene;


    @FXML
private ListView<UserDTO> friendRequestsListView;

@FXML
private ListView<NotificationDTO> missedMessagesListView;

    @FXML
    private ToggleButton friendRequestsButton;

    @FXML
    private ToggleButton missedMessagesButton;

    @FXML
    private ToggleGroup buttonBar;

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
        RMIConfig p = null;
        try {
            InputStream inputStream = getClass().getResourceAsStream("/rmi.xml");
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            p = (RMIConfig) unmarshaller.unmarshal(inputStream);
            inputStream.close();
            // System.out.println(p.getIp() +" " + p.getPort());
        } catch (JAXBException ex) {
            ex.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String ip = p.getIp();
        int port = p.getPort();

        Registry reg;
        try {
            reg = LocateRegistry.getRegistry(ip, port);
            contactDAO = (ContactDAOInterface) reg.lookup("contactDAO");
            notificationDAO = (NotificationDAOInterface) reg.lookup("notificationDAO");
            userDAO = (UserDAOInterface) reg.lookup("userDAO");
            messageDAO = (MessageDAOInterface) reg.lookup("messageDAO");
            attachementDAO = (AttachementDAOInterface) reg.lookup("attachementDAO");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        friendRequestsListView.setItems(contacts);
        friendRequestsButton.setDisable(true);
        missedMessagesButton.setDisable(false);
        friendRequestsListView.setVisible(true);
        missedMessagesListView.setVisible(false);
        friendRequestsButton.setOnAction(event -> handleFriendRequestsToggle());
        missedMessagesButton.setOnAction(event -> handleMissedMessagesToggle());

        buttonBar.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == friendRequestsButton) {

                friendRequestsButton.setDisable(true);
                missedMessagesButton.setDisable(false);
                friendRequestsListView.setVisible(true);
                missedMessagesListView.setVisible(false);
            } else if (newToggle == missedMessagesButton) {
                friendRequestsButton.setDisable(false);
                missedMessagesButton.setDisable(true);
                friendRequestsListView.setVisible(false);
                missedMessagesListView.setVisible(true);

            }
        });

    }

    private void handleFriendRequestsToggle() {
        if (friendRequestsButton.isSelected()) {
            contacts.clear();
            friendRequestsListView.getItems().clear(); 
            friendRequestsListView.setItems(contacts);
            friendRequestsListView.setCellFactory(null);
            loadNotifications();
        }
    }

    private void handleMissedMessagesToggle() {
        if (missedMessagesButton.isSelected()) {
            notifications.clear(); 
            missedMessagesListView.getItems().clear();
            missedMessagesListView.setItems(notifications);
            missedMessagesListView.setCellFactory(null);
            loadMissedMessages();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadNotifications() {
        System.out.println("UserDTO phone in NotificationController: " + userDTO.getPhone());

        ObservableList<UserDTO> pendingRequests = FXCollections.observableArrayList();
        try {
            pendingRequests = FXCollections.observableArrayList(contactDAO.findAllContactsPENDING(userDTO.getPhone()));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch(NullPointerException asd){ExceptionUtility.alert();}
        System.out.println("DAO returned: " + pendingRequests.size() + " pending requests");

        friendRequestsListView.setCellFactory(new Callback<ListView<UserDTO>, ListCell<UserDTO>>() {
            @Override
            public ListCell<UserDTO> call(ListView<UserDTO> p) {
                return new ListCell<UserDTO>() {
                    private FXMLLoader loader;
                    private HBox requestCard;
                    private FriendRequestController controller;

                    @Override
                    protected void updateItem(UserDTO user, boolean empty) {
                        super.updateItem(user, empty);

                        if (empty || user == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            try {
                                if (loader == null) {
                                    loader = new FXMLLoader(getClass().getResource("/screens/FriendRequestCard.fxml"));
                                    requestCard = loader.load();
                                    controller = loader.getController();

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

                                }
                                setGraphic(requestCard);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassCastException e) {

                            }
                        }
                    }
                };
            }
        });

        contacts.addAll(pendingRequests);
        System.out.println("Contacts size after adding: " + contacts.size());
    }

    @SuppressWarnings("unchecked")
    public void loadMissedMessages() {
        try {
            notificationDAO.delete(1, 1);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ObservableList<NotificationDTO> messages = FXCollections.observableArrayList();
        try {
            messages = FXCollections.observableArrayList(notificationDAO.getNotifications(userDTO.getUserID()));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        missedMessagesListView.setCellFactory(new Callback<ListView<NotificationDTO>, ListCell<NotificationDTO>>() {
            @Override
            public ListCell<NotificationDTO> call(ListView<NotificationDTO> p) {
                return new ListCell<NotificationDTO>() {
                    private FXMLLoader loader;
                    private MessageCardController messageCardController;
                    private HBox messageCard;

                    @Override
                    protected void updateItem(NotificationDTO notification, boolean empty) {
                        super.updateItem(notification, empty);
                        if (empty || notification == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            try {

                                if (loader == null) {
                                    try {
                                        loader = new FXMLLoader(getClass().getResource("/screens/messageCard.fxml"));
                                        messageCard = loader.load();
                                        messageCardController = loader.getController();
                                        messageCardController.setStage(stage);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                try {
                                    MessageDTO chat = messageDAO.read(notification.getMessageID());
                                    messageCardController.setMessageData(
                                            userDAO.read(chat.getUserID()),
                                            chat.getMessageContent(),
                                            attachementDAO.getAttachmentTitle(chat.getAttachmentID()),
                                            chat.getMessageDate().toString(),
                                            false,
                                            chat.getAttachmentID() != 0);
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                                setGraphic(messageCard);
                            } catch (ClassCastException e) {

                            }
                        }
                    }
                };
            }
        });

        notifications.addAll(messages);

    }

    public void addNot(NotificationDTO not) {
        notifications.add(not);
    }

    public void addUser(UserDTO user) {
        contacts.add(user);
    }

}