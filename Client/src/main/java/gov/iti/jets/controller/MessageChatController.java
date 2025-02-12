package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import gov.iti.jets.chatbot.BotService;
import gov.iti.jets.chatbot.ChatbotInterface;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import gov.iti.jets.client.Images;
import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.AttachementDAOInterface;
import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dao.MessageDAOInterface;
import gov.iti.jets.dao.NotificationDAOInterface;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.AttachementDTO;
import gov.iti.jets.dto.ChatDTO;
import gov.iti.jets.dto.MessageDTO;
import gov.iti.jets.dto.UserDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.scene.input.MouseEvent;

public class MessageChatController {
    private ScheduledFuture<?> messagePollingTask;
    private boolean autoScrollEnabled = true;
    ChatbotInterface chatbot;
    ObservableList<MessageDTO> chats = FXCollections.observableArrayList();
    private UserDTO userDTO = new UserDTO();
    private ChatDTO chatDTO = new ChatDTO();
    private AttachementDTO attachement = null;
    private UserDAOInterface userDAO;
    private AttachementDAOInterface attachementDAO;
    private MessageDAOInterface messageDAO;
    private NotificationDAOInterface notificationDAO;
    private int chatID;
    private Stage stage;
    Images images = new Images();
    private byte[] upload;
    private ChatCadController chatCadController;

    private UserDTO contactdto = new UserDTO();
    private ContactDAOInterface contactDAO;

    public void setContact(UserDTO user) {
        contactdto = user;
    }

    public void setuserData(UserDTO currentUser, UserDTO selectedUser) {
        currentUser = userDTO;
        selectedUser = contactdto;
    }

    public void setChatCadController(ChatCadController chatCadController) {
        this.chatCadController = chatCadController;
    }

    // We'll hold a reference to the vertical ScrollBar so that we add the listener
    // only once.
    private ScrollBar verticalBar;

    @FXML
    private ListView<MessageDTO> listView;
    @FXML
    private ImageView image;



    @FXML
    private Label name;
    @FXML
    private Label status;
    @FXML
    private TextField text;
    private String formattedText;

    public void setStage(Stage s) {
        stage = s;
    }

    public void setImage(byte[] i) {
        if (i != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(i);
            Image image = new Image(inputStream);
            this.image.setImage(image);
        }
    }

    public void setName(String s) {
        if (s != null)
            name.setText(s);
    }

    public void setStatus(String s) {
        if (s != null)
            status.setText(s);
    }

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private void chatInfo(MouseEvent event) {
        try {

            FXMLLoader contactinfoLoader = new FXMLLoader(getClass().getResource("/screens/Contact_Info.fxml"));
            VBox contactInfo = contactinfoLoader.load();

            Contact_InfoController contactInfoController = contactinfoLoader.getController();

            Stage info = new Stage();
            info.initOwner(stage);
            info.initStyle(StageStyle.TRANSPARENT);

            Scene chatInfoScene = new Scene(contactInfo);
            chatInfoScene.setFill(Color.TRANSPARENT);

            info.setScene(chatInfoScene);
            info.initModality(Modality.APPLICATION_MODAL);
            info.setX(stage.getX() + 400);
            info.setY(stage.getY() + 100);
            info.show();

            contactInfoController.getNameLabel().setText(contactdto.getName());
            contactInfoController.getphoneLabel().setText(contactdto.getPhone());
            contactInfoController.bio().setText(contactdto.getBio());
            if(contactdto.getUserMode() != null)
            contactInfoController.getModeLabel().setText(contactdto.getUserMode().toString());
            contactInfoController.setUserDTO(userDTO);
            contactInfoController.setContact(contactdto);
            byte[] picture = contactdto.getUserPicture();

            if (picture != null) {
                contactInfoController.getImage().setImage(new Image(new ByteArrayInputStream(picture)));
            } else {
                contactInfoController.getImage()
                        .setImage(new Image(getClass().getResource("/images/defaultUser.png").toExternalForm()));
            }

            ImageView imageView = contactInfoController.getImage();

            contactInfo.setOnMousePressed(e -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });

            contactInfo.setOnMouseDragged(e -> {

                info.setX(e.getScreenX() - xOffset);

                info.setY(e.getScreenY() - yOffset);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void attach(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try (FileInputStream fIn = new FileInputStream(file)) {
                upload = fIn.readAllBytes();
                String fileName = file.getName();
                AttachementDTO attachementDTOCreated = new AttachementDTO();
                attachementDTOCreated.setAttachmentTitle(fileName); // TODO: hashed if needed
                attachementDTOCreated.setAttachmentSize(file.length());
                String type = Files.probeContentType(Paths.get(URLEncoder.encode(file.toURI().toString(), "UTF-8")));
                attachementDTOCreated.setAttachmentType(type);
                attachement = attachementDAO.createAttachment(attachementDTOCreated);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void textEnter(ActionEvent event) {
        // Similar to send action; you could combine these if needed.
        System.out.println("Starting textEnter method");
        try {
            boolean isBlocked = contactDAO.isContactBlocked(userDTO.getPhone(), contactdto.getPhone());
            boolean isBlocker = contactDAO.isUserBlocker(userDTO.getPhone(), contactdto.getPhone());
            System.out.println("isBlocked: " + isBlocked);
            System.out.println("isBlocker: " + isBlocker);

            if (isBlocked) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    if (isBlocker) {
                        alert.setTitle("Action Required");
                        alert.setHeaderText("You have blocked this contact");
                        alert.setContentText("Please unblock this contact to send messages.");
                    } else {
                        alert.setTitle("Message Not Delivered");
                        alert.setHeaderText("You have been blocked");
                        alert.setContentText("The recipient has blocked you. Your message cannot be delivered.");
                    }
                    alert.showAndWait();
                });
            } else {
                sendMessage();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred. Please try again later.");
                alert.showAndWait();
            });
        }
    }

    private void sendMessage() {
        // String msgContent = (formattedText != null && !formattedText.isEmpty()) ?
        // formattedText : text.getText();
        String msgContent;
        if (formattedText != null && !formattedText.isEmpty()) {
            msgContent = formattedText;
        } else {
            msgContent = "<html><body><p>" + text.getText().replace("\n", "<br>") + "</p></body></html>";
        }
        if (msgContent.trim().isEmpty() && attachement == null)
            return;
        MessageDTO msg = new MessageDTO();
        msg.setMessageContent(msgContent);
        msg.setChatID(chatID);
        msg.setUserID(userDTO.getUserID());
        msg.setMessageDate(Date.valueOf(LocalDate.now()));
        if (attachement != null) {
            msg.setAttachmentID(attachement.getAttachmentID());
            images.uploadAttachment(attachement.getAttachmentTitle(), upload);
        }
        try {
            messageDAO.create(msg);
            // After sending, force auto-scroll.
            Platform.runLater(() -> {
                listView.scrollTo(chats.size() - 1);
                autoScrollEnabled = true;
            });
        } catch (RemoteException e) {
            e.printStackTrace();
            ExceptionUtility.alert();
        }catch(NullPointerException sdf){
            ExceptionUtility.alert();
        }


        attachement = null;
        text.setText("");
        formattedText = null;
    }

    private void sendMessage(String msgContent) {
        if (msgContent.trim().isEmpty() && attachement == null)
            return;
        MessageDTO msg = new MessageDTO();
        msg.setMessageContent(msgContent);
        msg.setChatID(chatID);
        msg.setUserID(userDTO.getUserID());
        msg.setMessageDate(Date.valueOf(LocalDate.now()));
        if (attachement != null) {
            msg.setAttachmentID(attachement.getAttachmentID());
            images.uploadAttachment(attachement.getAttachmentTitle(), upload);
        }
        try {
            messageDAO.create(msg);
            // After sending, force auto-scroll.
            Platform.runLater(() -> {
                listView.scrollTo(chats.size() - 1);
                autoScrollEnabled = true;
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        attachement = null;
        text.setText("");
    }

    public void addToChats(MessageDTO m) {
        Platform.runLater(() -> {
            chats.add(m);
            if (autoScrollEnabled) {
                listView.scrollTo(chats.size() - 1);
            }
            String ret = m.getMessageContent();
            // if (ret.length() > 10) ret = ret.substring(0, 10) + "...";
            // chatCadController.setText(ret);

            try {
                notificationDAO.delete(userDTO.getUserID(), m.getMesssageID());
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (m.getUserID() != userDTO.getUserID() && BotService.getInstance().getBotServiceStatus()) {
                try {
                    
                    sendMessage(chatbot.sendMessage(Jsoup.parse(m.getMessageContent()).text()));

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    public void setUserDTO(UserDTO user, int chatID) {
        this.chatID = chatID;
        listView.setItems(chats);
        userDTO = user;

        // Cancel any existing polling task.
        if (messagePollingTask != null) {
            messagePollingTask.cancel(false);
        }

        // Load initial messages.
        try {
            List<MessageDTO> initialMessages = messageDAO.findAllMessages(chatID);
            chats.setAll(initialMessages);
            Platform.runLater(() -> {
                listView.scrollTo(chats.size() - 1);
                autoScrollEnabled = true;
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch(NullPointerException e){ExceptionUtility.alert();}
        
        // Setup the scroll listener (only once).
        setupScrollListener();

        // Set the cell factory for messages.
        listView.setCellFactory(new Callback<ListView<MessageDTO>, ListCell<MessageDTO>>() {
            @Override
            public ListCell<MessageDTO> call(ListView<MessageDTO> p) {
                return new ListCell<MessageDTO>() {
                    private FXMLLoader loader;
                    private MessageCardController messageCardController;
                    private HBox messageCard;

                    @Override
                    protected void updateItem(MessageDTO chat, boolean empty) {
                        super.updateItem(chat, empty);
                        if (empty || chat == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
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
                                messageCardController.getMessageContainer().getStyleClass().clear();
                                messageCardController.getMessageContainerr().getStyleClass().clear();
                                notificationDAO.delete(userDTO.getUserID(), chat.getMesssageID());
                                messageCardController.setSeen(notificationDAO.isSeen(chat.getMesssageID()));
                                messageCardController.setMessageData(
                                        userDAO.read(chat.getUserID()),
                                        chat.getMessageContent(),
                                        attachementDAO.getAttachmentTitle(chat.getAttachmentID()),
                                        chat.getMessageDate().toString(),
                                        chat.getUserID() == user.getUserID(),
                                        chat.getAttachmentID() != 0);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            setGraphic(messageCard);
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void send(ActionEvent event) {
        System.out.println("Starting Send method");
        try {
            boolean isBlocked = contactDAO.isContactBlocked(userDTO.getPhone(), contactdto.getPhone());
            boolean isBlocker = contactDAO.isUserBlocker(userDTO.getPhone(), contactdto.getPhone());
            System.out.println("isBlocked: " + isBlocked);
            System.out.println("isBlocker: " + isBlocker);

            if (isBlocked) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    if (isBlocker) {
                        alert.setTitle("Action Required");
                        alert.setHeaderText("You have blocked this contact");
                        alert.setContentText("Please unblock this contact to send messages.");
                    } else {
                        alert.setTitle("Message Not Delivered");
                        alert.setHeaderText("You have been blocked");
                        alert.setContentText("The recipient has blocked you. Your message cannot be delivered.");
                    }
                    alert.showAndWait();
                });
            } else {
                sendMessage();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred. Please try again later.");
                alert.showAndWait();
            });
        }
    }

    @FXML
    private void initialize() {
        // Load RMI configuration.
        RMIConfig p = null;
        try {
            InputStream inputStream = getClass().getResourceAsStream("/rmi.xml");
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            p = (RMIConfig) unmarshaller.unmarshal(inputStream);
            inputStream.close();
        } catch (JAXBException | IOException ex) {
            ex.printStackTrace();
        }
        String ip = p.getIp();
        int port = p.getPort();
        try {
            Registry reg = LocateRegistry.getRegistry(ip, port);
            userDAO = (UserDAOInterface) reg.lookup("userDAO");
            attachementDAO = (AttachementDAOInterface) reg.lookup("attachementDAO");
            messageDAO = (MessageDAOInterface) reg.lookup("messageDAO");
            chatbot = (ChatbotInterface) reg.lookup("chatbot");
            notificationDAO = (NotificationDAOInterface) reg.lookup("notificationDAO");

            contactDAO = (ContactDAOInterface) reg.lookup("contactDAO");
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        Circle clip = new Circle();
        clip.setRadius(30);
        clip.setCenterX(30);
        clip.setCenterY(30);
        image.setClip(clip);
    }

    public void stopMessagePolling() {
        if (messagePollingTask != null) {
            messagePollingTask.cancel(false);
            messagePollingTask = null;
        }
    }

    // Set up the vertical scroll listener only once.
    private void setupScrollListener() {
        Platform.runLater(() -> {
            if (verticalBar == null) {
                verticalBar = (ScrollBar) listView.lookup(".scroll-bar:vertical");
                if (verticalBar != null) {
                    verticalBar.valueProperty().addListener((obs, oldVal, newVal) -> {
                        // Enable auto-scroll if near the bottom.
                        autoScrollEnabled = newVal.doubleValue() >= verticalBar.getMax() - 0.1;
                    });
                }
            }
        });
    }

    @FXML
    private void openFormattingDialog(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/FormattingDialog.fxml"));
            Parent root = loader.load();

            FormattingDialogController controller = loader.getController();
            Stage stage = new Stage();

            controller.setParentController(this, stage, text.getText());

            stage.setTitle("Format Message");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void applyFormattedText(String formattedText) {
        this.formattedText = formattedText + " ";
        String plainText = Jsoup.parse(this.formattedText).text();
        text.setText(plainText);
        text.positionCaret(plainText.length());
    }

    
    @FXML
private void openEmojiDialog(MouseEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/EmojiDialog.fxml"));
        Parent root = loader.load();

        EmojiDialogController controller = loader.getController();
        Stage stage = new Stage();
        
        controller.setParentController(this, stage);
        
        stage.setTitle("Select Emoji");
        stage.setScene(new Scene(root));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

 private String escapeHtml(String text) {
    return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
 }
  
  public void insertEmoji(String emoji) {
    String currentText = text.getText();
    int caretPosition = text.getCaretPosition();
    String htmlContent = formattedText != null ? formattedText : "<html><body><p>" + escapeHtml(currentText) + "</p></body></html>";
    Document doc = Jsoup.parse(htmlContent);
    doc.outputSettings().prettyPrint(false); 

    Element body = doc.body();
    String bodyText = body.text();
    int textLength = bodyText.length();
    if (caretPosition <= textLength) {
        String newText = bodyText.substring(0, caretPosition) + emoji + bodyText.substring(caretPosition);
        body.text(newText);
    } else {
        body.appendText(emoji);
    }
    formattedText = doc.outerHtml();
    String plainText = body.text();
    text.setText(plainText);
    text.positionCaret(caretPosition + emoji.length());
}
// public void insertEmoji(String emoji) {
    
//     int caretPosition = text.getCaretPosition();

//     String htmlContent = formattedText != null ? formattedText : "<html><body><p></p></body></html>";
//     Document doc = Jsoup.parse(htmlContent);
//     doc.outputSettings().prettyPrint(false); 

//     Element body = doc.body();

//     String plainText = body.text();
//     String newPlainText = plainText.substring(0, caretPosition) + emoji + plainText.substring(caretPosition);

//     body.text(newPlainText);

//     formattedText = doc.outerHtml();
//     text.setText(newPlainText);

//     text.positionCaret(caretPosition + emoji.length());
// }
}