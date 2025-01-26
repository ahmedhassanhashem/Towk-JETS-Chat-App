package gov.iti.jets.controller;

import java.io.ByteArrayInputStream;

import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.text.Text;


public class ChatCadController {


   @FXML
   private ImageView image;

   @FXML
   private Label label;

   @FXML
   private Text text;

    public void setImage(byte[] i){
        if(i != null){

            ByteArrayInputStream inputStream = new ByteArrayInputStream(i);
            Image image = new Image(inputStream);
            this.image.setImage(image);
        }
    }   
    public void setLabel(String s){
        if(s != null)
        label.setText(s);
    }

    public void setText(String s){
        if(s != null)
        text.setText(s);
    }

    @FXML
    private void initialize() {
     

    }
}
