package gov.iti.jets.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

import java.io.ByteArrayInputStream;

public class FriendRequestController{
    @FXML
    private HBox DynamicHBox;
    @FXML
    private ImageView friendRequestImage;
    @FXML
    private Label requestContent;
    @FXML
    private Button acceptButton;
    @FXML
    private Button rejectButton;
    @FXML
    public void initialize() {
        Circle clip = new Circle();
        clip.setRadius(25); 
        clip.setCenterX(25);
        clip.setCenterY(25);
        friendRequestImage.setClip(clip);
    }

    public void setName(String name) {
        requestContent.setText(name + " sent you a friend request");
    }

    public void setPicture(byte[] imageData) {
        if (imageData != null) {
            Image image = new Image(new ByteArrayInputStream(imageData));
            friendRequestImage.setImage(image);
        }
    }

    public Button getAcceptButton() {
        return acceptButton;
    }

    public Button getRejectButton() {
        return rejectButton;
    }
}
