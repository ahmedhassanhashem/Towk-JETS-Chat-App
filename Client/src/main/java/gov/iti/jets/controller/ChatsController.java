package gov.iti.jets.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;

// import gov.iti.jets.dao.UserChatDAO;

import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.config.RMIConfig;
// import gov.iti.jets.dao.ChatDAO;
import gov.iti.jets.dao.ChatDAOInterface;
import gov.iti.jets.dao.ContactDAOInterface;
// import gov.iti.jets.dao.ContactDAO;
// import gov.iti.jets.dao.MessageDAO;
import gov.iti.jets.dao.MessageDAOInterface;
import gov.iti.jets.dao.UserChatDAOInterface;
import gov.iti.jets.dto.ChatDTO;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserStatus;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class ChatsController {

    private MessageChatController currentMessageController;

    private Stage stage;
    ObservableList<UserDTO> contacts = FXCollections.observableArrayList();
    @FXML
    private ListView<UserDTO> listView;
    @FXML
    private BorderPane borderPane;
    private UserDTO userDTO = new UserDTO();
    private UserChatDAOInterface userChatDAO;
    private ContactDAOInterface contactDAO;
    private MessageDAOInterface messageDAO;
    private ChatDAOInterface chatDao;
    private ScheduledExecutorService scheduledExecutorService;


    private void loadChat(UserDTO user) {
        try {
            FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/messageChat.fxml"));
            BorderPane chat = chatLoader.load();
            MessageChatController messageController = chatLoader.getController();

            // Stop previous chat's polling
            if (currentMessageController != null) {
                currentMessageController.stopMessagePolling();
            }
            currentMessageController = messageController;

            // Set up the new controller
            messageController.setStage(stage);
            messageController.setUserDTO(userDTO, chatDao.findExistingSingleChat(userDTO.getUserID(), user.getUserID()), scheduledExecutorService);

            borderPane.setCenter(chat);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadGroupChat(UserDTO group) {
        try {
            FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/messageChat.fxml"));
            BorderPane chat = chatLoader.load();
            MessageChatController messageController = chatLoader.getController();
            
            // Stop previous polling if needed
            // if (currentMessageController != null) {
            //     currentMessageController.stopMessagePolling();
            // }
            
            messageController.setStage(stage);
            // For group chats, the chat ID is already provided by group.getUserID()
            messageController.setUserDTO(userDTO, group.getUserID(), scheduledExecutorService);
            // Optionally set the image and name if needed:
            messageController.setImage(group.getUserPicture());
            messageController.setName(group.getName());
            
            borderPane.setCenter(chat);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage s) {
        stage = s;
    }

    public void setUserDTO(UserDTO user) {
        userDTO = user;

    }

    @FXML
    private void addContact(ActionEvent event) {
        AnchorPane hold = null;
        FXMLLoader addContactLoader = new FXMLLoader(getClass().getResource("/screens/AddNewContacts.fxml"));
        try {
            hold = addContactLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AddContactController addContactController = addContactLoader.getController();
        var addContactScene = new Scene(hold, 500, 400);
        Stage info = new Stage();
        info.initOwner(stage);
        info.initModality(Modality.APPLICATION_MODAL);
        info.setScene(addContactScene);
        info.show();
        addContactController.setStage(info);
        addContactController.setAddContactController(addContactController);
        addContactController.setUserDTO(userDTO);
    }

   @FXML
    private void addGroup(ActionEvent event) {
        VBox hold;
        FXMLLoader addContactLoader = new FXMLLoader(getClass().getResource("/screens/CreateGroup.fxml"));
        try {
            hold = addContactLoader.load();
            CreateGroupController c = addContactLoader.getController();
            var addContactScene = new Scene(hold, 700, 550);
            Stage info = new Stage();
            info.initOwner(stage);
            info.initStyle(StageStyle.UNDECORATED);
            info.initStyle(StageStyle.TRANSPARENT);
            info.initModality(Modality.APPLICATION_MODAL);
            Platform.runLater(() -> c.applyRoundedCorners(hold, 15));
            addContactScene.setFill(Color.TRANSPARENT);
            info.setScene(addContactScene);
            c.setStage(info);
            System.out.println("AddGroupUSERDTO" + userDTO.getPhone());
            c.setUserDTO(userDTO);
            c.loadContacts();
            info.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

    public void chatScene() {
        contacts.clear();
        listView.setItems(contacts);

        ObservableList<UserDTO> userDTOs = FXCollections.observableArrayList();
        try {
            userDTOs = FXCollections.observableArrayList(userChatDAO.findAll(userDTO.getUserID()));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        listView.setCellFactory(null);
        listView.setCellFactory(new Callback<ListView<UserDTO>, ListCell<UserDTO>>() {
            @Override
            public ListCell<UserDTO> call(ListView<UserDTO> p) {
                return new ListCell<UserDTO>() {
                    FXMLLoader addContactLoader;
                    HBox chatCard;
                    ChatCadController chatCardController;

                    protected void updateItem(UserDTO user, boolean empty) {
                        // super.updateItem(user, empty);
                        if (user == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            if (addContactLoader == null) {
                                try {
                                    addContactLoader = new FXMLLoader(getClass().getResource("/screens/ChatCad.fxml"));
                                    chatCard = addContactLoader.load();
                                    chatCardController = addContactLoader.getController();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            // super.updateItem(item, empty);     
                            Platform.runLater(() -> {
                                chatCard.setPrefWidth(listView.getWidth() - 20);
                                chatCardController.setImage(user.getUserPicture());
                                chatCardController.setLabel(user.getName());
                                });
                            try {
                                String ret = messageDAO.findLastMessage(userDTO.getUserID(), user.getUserID());
                                if (ret.length() > 7) {
                                    ret = ret.substring(0, 7) + "...";
                                }
                                chatCardController.setText(ret);
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            setGraphic(chatCard);
                            chatCard.setOnMouseClicked((e) -> {
                                loadChat(user); // Use the centralized method

                            });

                        }
                    }

                };

            }
        });

        contacts.addAll(userDTOs);
    }

    public void contactScene() {
        contacts.clear();
        listView.setItems(contacts);

        ObservableList<UserDTO> list = FXCollections.observableArrayList();
        try {
            list = FXCollections.observableArrayList(contactDAO.findAllContactsACCEPTED(userDTO.getPhone()));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        listView.setCellFactory(null);
        listView.setCellFactory(new Callback<ListView<UserDTO>, ListCell<UserDTO>>() {
            @Override
            public ListCell<UserDTO> call(ListView<UserDTO> p) {
                return new ListCell<UserDTO>() {
                    private FXMLLoader addContactLoader;
                    private HBox contactCard;
                    private ContactCardController contactCardController;

                    @Override
                    protected void updateItem(UserDTO user, boolean empty) {
                        // super.updateItem(user, empty); 

                        if (empty || user == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            if (addContactLoader == null) {
                                try {
                                    addContactLoader = new FXMLLoader(
                                            getClass().getResource("/screens/CardContact.fxml"));
                                    contactCard = addContactLoader.load();
                                    contactCardController = addContactLoader.getController();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            contactCardController.setPicture(user.getUserPicture());
                            contactCardController.setName(user.getName());
                            contactCardController.setBio(user.getBio());

                            if (user.getUserStatus() == UserStatus.OFFLINE) {
                                contactCardController.getStatus().setFill(Color.GRAY);
                            } else {
                                contactCardController.getStatus().setFill(Color.GREEN);
                            }
                            setGraphic(contactCard);

                            contactCard.setOnMouseClicked((e) -> {
                                loadChat(user); // Use the centralized method

                            });


                            
                        }
                    }
                };
            }
        });
        contacts.addAll(list);
    }

    public void groupScene() {
        contacts.clear();
        listView.setItems(contacts);

        ObservableList<UserDTO> list = FXCollections.observableArrayList();
        try {
            list = FXCollections.observableArrayList(chatDao.findAllGroups(userDTO.getUserID()));
            // System.out.println(list.size());
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        listView.setCellFactory(null);
        listView.setFixedCellSize(70);
        listView.setCellFactory(new Callback<ListView<UserDTO>, ListCell<UserDTO>>() {
            @Override
            public ListCell<UserDTO> call(ListView<UserDTO> p) {
                return new ListCell<UserDTO>() {
                    FXMLLoader addContactLoader;
                    HBox chatCard;
                    ChatCadController chatCardController;

                    protected void updateItem(UserDTO user, boolean empty) {
                       // super.updateItem(user, empty);
                        if (user == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            if (addContactLoader == null) {
                                try {
                                    addContactLoader = new FXMLLoader(getClass().getResource("/screens/ChatCad.fxml"));
                                    chatCard = addContactLoader.load();
                                    chatCardController = addContactLoader.getController();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Platform.runLater(() -> {
                            chatCard.setPrefWidth(listView.getWidth() - 20);
                            chatCardController.setImage(user.getUserPicture());
                            chatCardController.setLabel(user.getName());
                            });
                            try {
                                String ret = messageDAO.findLastMessageGroup(user.getUserID());
                                if (ret.length() > 10) {
                                    ret = ret.substring(0, 10) + "...";
                                }
                                chatCardController.setText(ret);
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            setGraphic(chatCard);
                            
                            chatCard.setOnMouseClicked((e) -> {
                                loadGroupChat(user); // Use the group chat method

                            });

                        }
                    }

                };

            }
        });

        contacts.addAll(list);
    }

    @FXML
    private void initialize() {
        RMIConfig p = null;
                try { 
            File XMLfile = new File(getClass().getResource("/rmi.xml").toURI()); 
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller(); 
            p = (RMIConfig) unmarshaller.unmarshal(XMLfile);
            // System.out.println(p.getIp() +" " + p.getPort());
        } catch (JAXBException ex) {
            ex.printStackTrace();
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String ip =p.getIp();
        int port = p.getPort();

        Registry reg;
        try {
            reg = LocateRegistry.getRegistry(ip, port);
            chatDao = (ChatDAOInterface) reg.lookup("chatDAO");
            messageDAO = (MessageDAOInterface) reg.lookup("messageDAO");
            userChatDAO = (UserChatDAOInterface) reg.lookup("userChatDAO");
            contactDAO = (ContactDAOInterface) reg.lookup("contactDAO");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
    }
}
