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

import gov.iti.jets.config.RMIConfig;
import gov.iti.jets.dao.AnnouncementDAOInterface;
import gov.iti.jets.dto.AnnouncementDTO;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class AnnouncementController {

    private AnnouncementDAOInterface announcementDAO;
    ObservableList<AnnouncementDTO> contacts = FXCollections.observableArrayList();

    @FXML
    private ListView<AnnouncementDTO> listView;

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
            System.out.println(ex.getMessage());
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String ip =p.getIp();
        int port = p.getPort();
        
        Registry reg;

                try {
            reg = LocateRegistry.getRegistry(ip, port);
            announcementDAO = (AnnouncementDAOInterface) reg.lookup("announcementDAO");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        loadAnnouncements();
        listView.setItems(contacts);

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setFocusTraversable(false);
    }

    private void loadAnnouncements() {
        ObservableList<AnnouncementDTO> announcementDTO = FXCollections.observableArrayList();;
        try {
            announcementDTO = FXCollections.observableArrayList(announcementDAO.findAll());
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        listView.setCellFactory(new Callback<ListView<AnnouncementDTO>, ListCell<AnnouncementDTO>>() {
            @Override
            public ListCell<AnnouncementDTO> call(ListView<AnnouncementDTO> p) {
                return new ListCell<AnnouncementDTO>() {
                    @Override
                    protected void updateItem(AnnouncementDTO announcement, boolean empty) {
                        super.updateItem(announcement, empty);

                        if (empty || announcement == null) {
                            setText(null); 
                            setGraphic(null); 
                        } else {
                            // Load the announcement card in the background
                            Task<HBox> loadTask = new Task<HBox>() {
                                @Override
                                protected HBox call() throws Exception {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/screens/AnnouncementCard.fxml"));
                                    HBox announcementCard = null;

                                    try {
                                        announcementCard = loader.load();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    AnnouncementCardController controller = loader.getController();
                                    if (controller != null) {
                                        controller.setAnnouncementData(announcement);
                                    }

                                    return announcementCard;
                                }
                            };

                            // Update the cell's graphic when the task is finished
                            loadTask.setOnSucceeded(event -> {
                                HBox announcementCard = loadTask.getValue();
                                setGraphic(announcementCard);
                            });

                            // Run the task in a new thread
                            new Thread(loadTask).start();
                        }
                    }
                };
            }
        });

        contacts.addAll(announcementDTO);
    }
}
