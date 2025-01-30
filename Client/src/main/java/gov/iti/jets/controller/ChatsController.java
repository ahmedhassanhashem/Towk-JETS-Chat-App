package gov.iti.jets.controller;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.Properties;

// import gov.iti.jets.dao.UserChatDAO;

import gov.iti.jets.dao.UserDAOInterface;
// import gov.iti.jets.dao.ChatDAO;
import gov.iti.jets.dao.ChatDAOInterface;
import gov.iti.jets.dao.ContactDAOInterface;
// import gov.iti.jets.dao.ContactDAO;
// import gov.iti.jets.dao.MessageDAO;
import gov.iti.jets.dao.MessageDAOInterface;
import gov.iti.jets.dao.UserChatDAOInterface;
import gov.iti.jets.dto.UserDTO;
import gov.iti.jets.dto.UserStatus;

public class ChatsController {

    private Stage stage;
    ObservableList<UserDTO> contacts = FXCollections.observableArrayList();
    @FXML
    private ListView<UserDTO> listView;
    @FXML
    private BorderPane borderPane;
    private UserDTO userDTO = new UserDTO();
    private UserChatDAOInterface userChatDAO;
    private ContactDAOInterface contactDAO ;
    private MessageDAOInterface messageDAO;
    private ChatDAOInterface chatDao;
    

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
        VBox hold = null;
        FXMLLoader addContactLoader = new FXMLLoader(getClass().getResource("/screens/CreateGroup.fxml"));
        try {
            hold = addContactLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        var addContactScene = new Scene(hold, 700, 550);
        Stage info = new Stage();
        info.initOwner(stage);
        info.initModality(Modality.APPLICATION_MODAL);
        info.setScene(addContactScene);
        info.show();
    }

    public void chatScene() {
        listView.setItems(contacts);

        ObservableList<UserDTO> userDTOs = FXCollections.observableArrayList();
        try {
            userDTOs = FXCollections.observableArrayList(userChatDAO.findAll(userDTO.getUserID()));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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
                            chatCardController.setImage(user.getUserPicture());
                            chatCardController.setLabel(user.getName());
                            try {
                                chatCardController.setText(messageDAO.findLastMessage(userDTO.getUserID(), user.getUserID()));
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            chatCard.setOnMouseClicked((e) -> {
                                try {
                                    final FXMLLoader chatLoader = new FXMLLoader(
                                            getClass().getResource("/screens/messageChat.fxml"));
                                    final BorderPane chat = chatLoader.load();
                                    MessageChatController messageController = chatLoader.getController();
                                    messageController.setImage(user.getUserPicture());
                                    messageController.setName(user.getName());
                                    messageController.setStatus(user.getUserStatus().toString());
                                    messageController.setStage(stage);

                                    try {

                                        messageController.setUserDTO(userDTO, chatDao.findExistingSingleChat(userDTO.getUserID(), user.getUserID()));
                                    } catch (SQLException e1) {
                                        e1.printStackTrace();
                                    }
                                    // chat.setTop(new VBox());
                                    borderPane.setCenter(chat);
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            });
                            setGraphic(chatCard);
                        }
                    }

                };

            }
        });

        contacts.addAll(userDTOs);
    }

    public void contactScene() {
        listView.setItems(contacts);

        ObservableList<UserDTO> list = FXCollections.observableArrayList();
        try {
            list = FXCollections.observableArrayList(contactDAO.findAllContactsACCEPTED(userDTO.getPhone()));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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
                                    addContactLoader = new FXMLLoader(getClass().getResource("/screens/CardContact.fxml"));
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
                                try {
                                    FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/messageChat.fxml"));
                                    BorderPane chat = chatLoader.load();
                                    MessageChatController messageController = chatLoader.getController();
                                    messageController.setImage(user.getUserPicture());
                                    messageController.setName(user.getName());
                                    messageController.setStatus(user.getUserStatus().toString());
                                    messageController.setStage(stage);

                                    try {
                                        int chatID = chatDao.findExistingSingleChat(userDTO.getUserID(), user.getUserID());
                                        if (chatID == 0) {
                                            chatID = chatDao.createSingle(userDTO.getPhone(), user.getPhone());
                                        }
                                        messageController.setUserDTO(userDTO, chatID);
                                    } catch (SQLException e1) {
                                        e1.printStackTrace();
                                    }

                                    borderPane.setCenter(chat);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            });
                        }
                    }
                };
            }
        });
        contacts.addAll(list);
    }

    public void groupScene() {
        listView.setItems(contacts);

        ObservableList<UserDTO> list = FXCollections.observableArrayList();
        try {
            list = FXCollections.observableArrayList(chatDao.findAllGroups(userDTO.getUserID()));
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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
                            chatCardController.setImage(user.getUserPicture());
                            chatCardController.setLabel(user.getName());
                            try {
                                chatCardController.setText(messageDAO.findLastMessageGroup(user.getUserID()));
                            } catch (RemoteException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            chatCard.setOnMouseClicked((e) -> {
                                try {
                                    final FXMLLoader chatLoader = new FXMLLoader(
                                            getClass().getResource("/screens/messageChat.fxml"));
                                    final BorderPane chat = chatLoader.load();
                                    MessageChatController messageController = chatLoader.getController();
                                    messageController.setImage(user.getUserPicture());
                                    messageController.setName(user.getName());
                                    // messageController.setStatus(user.getUserStatus().toString());
                                    messageController.setStage(stage);

                                    messageController.setUserDTO(userDTO, user.getUserID());

                                    // chat.setTop(new VBox());
                                    borderPane.setCenter(chat);
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            });
                            setGraphic(chatCard);
                        }
                    }

                };

            }
        });

        contacts.addAll(list);
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
}
