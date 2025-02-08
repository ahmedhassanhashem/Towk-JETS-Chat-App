package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import gov.iti.jets.client.Images;
import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.AttachementDAOInterface;
import gov.iti.jets.dao.MessageDAOInterface;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

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
    private int chatID;
    private Stage stage;
    Images images = new Images();
    private byte[] upload;
    private ChatCadController chatCadController;
    
    public void setChatCadController(ChatCadController chatCadController) {
        this.chatCadController = chatCadController;
    }

    // We'll hold a reference to the vertical ScrollBar so that we add the listener only once.
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
    
    @FXML
    private void attach(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
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
        sendMessage();
    }
    
    private void sendMessage() {
        String msgContent = text.getText();
        if (msgContent.trim().isEmpty() && attachement == null) return;
        MessageDTO msg = new MessageDTO();
        msg.setMessageContent(msgContent);
        msg.setChatID(chatID);
        msg.setUserID(userDTO.getUserID());
        msg.setMessageDate(Date.valueOf(LocalDate.now()));
        if(attachement != null) {
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
    }
    private void sendMessage(String msgContent) {
        if (msgContent.trim().isEmpty() && attachement == null) return;
        MessageDTO msg = new MessageDTO();
        msg.setMessageContent(msgContent);
        msg.setChatID(chatID);
        msg.setUserID(userDTO.getUserID());
        msg.setMessageDate(Date.valueOf(LocalDate.now()));
        if(attachement != null) {
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
    public void addToChats(MessageDTO m){
        Platform.runLater(() -> {
        chats.add(m);
            if (autoScrollEnabled) {
                listView.scrollTo(chats.size() - 1);
            }
            String ret = m.getMessageContent();
            if (ret.length() > 10) ret = ret.substring(0, 10) + "...";
            chatCadController.setText(ret);


            if(m.getUserID() != userDTO.getUserID() && BotService.getInstance().getBotServiceStatus()){
                    try {
                        sendMessage(chatbot.sendMessage(m.getMessageContent()));
    
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
                                messageCardController.setMessageData(
                                    userDAO.read(chat.getUserID()), 
                                    chat.getMessageContent(), 
                                    attachementDAO.getAttachmentTitle(chat.getAttachmentID()), 
                                    chat.getMessageDate().toString(), 
                                    chat.getUserID() != user.getUserID(), 
                                    chat.getAttachmentID() != 0
                                );
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
        sendMessage();
    }
    
    @FXML
    private void initialize() {
        // Load RMI configuration.
        RMIConfig p = null;
        try {
            File XMLfile = new File(getClass().getResource("/rmi.xml").toURI());
            JAXBContext context = JAXBContext.newInstance(RMIConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            p = (RMIConfig) unmarshaller.unmarshal(XMLfile);
        } catch (JAXBException | URISyntaxException ex) {
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
}