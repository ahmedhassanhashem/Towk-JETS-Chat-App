package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

public class ContactCardController {

    @FXML
    ImageView picture;
    @FXML
    Label name;
    @FXML
    Label bio;
    @FXML
    Circle status;

    public Circle getStatus() {
        return status;
    }

    public void setStatus(Circle status) {
        this.status = status;
    }

    public void setPicture(byte[] i) {
        if (i != null) {

            ByteArrayInputStream inputStream = new ByteArrayInputStream(i);
            Image image = new Image(inputStream);
            this.picture.setImage(image);
        }
    }

    public void setName(String s) {
        if (s != null)
            name.setText(s);
    }

    public void setBio(String s) {
        if (s != null)
            bio.setText(s);
    }
    @FXML
    public void initialize() {
        Circle clip = new Circle();
        clip.setRadius(20); 
        clip.setCenterX(20);
        clip.setCenterY(20);
        picture.setClip(clip);
    }
}
