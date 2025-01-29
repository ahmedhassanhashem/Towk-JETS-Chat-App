package gov.iti.jets.controller;

import javafx.util.Callback;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import gov.iti.jets.dao.AnnouncementDAO;
import gov.iti.jets.dto.AnnouncementDTO;


import javafx.application.Platform;
import java.io.IOException;

public class AnnouncementController {

    private final AnnouncementDAO announcementDAO = new AnnouncementDAO();
    ObservableList<AnnouncementDTO> contacts = FXCollections.observableArrayList();

    @FXML
    private ListView<AnnouncementDTO> listView;

    @FXML
    private void initialize() {
        loadAnnouncements();
        listView.setItems(contacts);

        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setFocusTraversable(false);
    }

    private void loadAnnouncements() {
        ObservableList<AnnouncementDTO> announcementDTO = announcementDAO.findAll();

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
