package gov.iti.jets.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import gov.iti.jets.dao.UserChatDAO;
import gov.iti.jets.dao.ContactDAO;
import gov.iti.jets.dto.UserDTO;

public class ChatsController {

    private Stage stage;
    ObservableList<UserDTO> contacts = FXCollections.observableArrayList();
    @FXML
    private ListView<UserDTO> listView;
    @FXML
    private BorderPane borderPane;
    private UserDTO userDTO = new UserDTO();
    private UserChatDAO userChatDAO = new UserChatDAO();
    private ContactDAO contactDAO = new ContactDAO();

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
        var addContactScene = new Scene(hold, 500, 400);
        Stage info = new Stage();
        info.initOwner(stage);
        info.initModality(Modality.APPLICATION_MODAL);
        info.setScene(addContactScene);
        info.show();
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


        ObservableList<UserDTO> userDTOs = userChatDAO.findAll(userDTO.getUserID());



        listView.setCellFactory(new Callback<ListView<UserDTO>, ListCell<UserDTO>>() {
            @Override
            public ListCell<UserDTO> call(ListView<UserDTO> p) {
                return new ListCell<UserDTO>() {
                    protected void updateItem(UserDTO user, boolean empty) {
                        // super.updateItem(item, empty);
                        FXMLLoader addContactLoader = new FXMLLoader(getClass().getResource("/screens/ChatCad.fxml"));
                        HBox chatCard = null;
                        try {
                            chatCard = addContactLoader.load();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
        
                        ChatCadController chatCardController = addContactLoader.getController();

                        if (user == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            chatCardController.setImage(user.getUserPicture());
                            chatCardController.setLabel(user.getName());
                            chatCardController.setText("last message");
                            setGraphic(chatCard);
                            chatCard.setOnMouseClicked((e) -> {
                                try {
                                    final FXMLLoader chatLoader = new FXMLLoader(
                                            getClass().getResource("/screens/messageChat.fxml"));
                                    final BorderPane chat = chatLoader.load();
                                    MessageChatController messageController = chatLoader.getController();
                                    messageController.setImage(user.getUserPicture());
                                    messageController.setName(user.getName());
                                    messageController.setStatus(user.getUserStatus().toString());
                                    // chat.setTop(new VBox());
                                    borderPane.setCenter(chat);
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            });
                        }
                    }

                };

            }
        });

        contacts.addAll(userDTOs);
    }

    public void contactScene() {
        listView.setItems(contacts);

        ObservableList<UserDTO> list = contactDAO.findAllContactsACCEPTED(userDTO.getPhone());


        listView.setCellFactory(new Callback<ListView<UserDTO>, ListCell<UserDTO>>() {
            @Override
            public ListCell<UserDTO> call(ListView<UserDTO> p) {
                return new ListCell<UserDTO>() {
                    protected void updateItem(UserDTO user, boolean empty) {
                        // super.updateItem(item, empty);
                        FXMLLoader addContactLoader = new FXMLLoader(getClass().getResource("/screens/CardContact.fxml"));
                        HBox contactCard = null;
                        try {
                            contactCard = addContactLoader.load();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
        
                        ContactCardController contactCardController = addContactLoader.getController();

                        if (user == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            contactCardController.setPicture(user.getUserPicture());
                            contactCardController.setName(user.getName());
                            contactCardController.setBio(user.getBio());
                            setGraphic(contactCard);
                            contactCard.setOnMouseClicked((e) -> {
                                try {
                                    final FXMLLoader chatLoader = new FXMLLoader(
                                            getClass().getResource("/screens/messageChat.fxml"));
                                    final BorderPane chat = chatLoader.load();
                                    MessageChatController messageController = chatLoader.getController();
                                    messageController.setImage(user.getUserPicture());
                                    messageController.setName(user.getName());
                                    messageController.setStatus(user.getUserStatus().toString());
                                    
                                    borderPane.setCenter(chat);
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    // e1.printStackTrace();
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
        HBox hold = null;
        FXMLLoader addContactLoader = new FXMLLoader(getClass().getResource("/screens/CardContact.fxml"));

        try {
            hold = addContactLoader.load();
        } catch (IOException e) {

            e.printStackTrace();
        }
        // final BorderPane chat;
        final FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/screens/messageChat.fxml"));

        // try {
        // chat = chatLoader.load();
        // } catch (IOException e) {

        // e.printStackTrace();
        // }

        // contacts.add(hold);

        listView.setCellFactory(new Callback<ListView<UserDTO>, ListCell<UserDTO>>() {
            @Override
            public ListCell<UserDTO> call(ListView<UserDTO> p) {
                return new ListCell<UserDTO>() {
                    protected void updateItem(UserDTO user, boolean empty) {
                        // super.updateItem(item, empty);
                        FXMLLoader addContactLoader = new FXMLLoader(getClass().getResource("/screens/ChatCad.fxml"));
                        HBox chatCard = null;
                        try {
                            chatCard = addContactLoader.load();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
        
                        ChatCadController chatCardController = addContactLoader.getController();

                        if (user == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            chatCardController.setImage(user.getUserPicture());
                            chatCardController.setLabel(user.getName());
                            chatCardController.setText("last message");
                            
                            setGraphic(chatCard);
                            this.setOnMouseClicked((e) -> {
                                try {
                                    final BorderPane chat = chatLoader.load();
                                    // chat.setTop(new VBox());
                                    borderPane.setCenter(chat);
                                } catch (IOException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            });
                        }
                    }

                };

            }
        });
    }

    @FXML
    private void initialize() {
    }
}
