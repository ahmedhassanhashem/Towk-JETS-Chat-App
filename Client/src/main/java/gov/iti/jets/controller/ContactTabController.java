package gov.iti.jets.controller;

import java.io.IOException;
import java.rmi.RemoteException;

import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dto.UserDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ContactTabController {

    private int pageCounter = 1;
    private AddContactController addContactController;
    private UserDTO cdto;
    private UserDTO userDTO;
    private Stage stage;
    private  ContactDAOInterface cdao ;

    // @FXML
    // private Label name;

    @FXML
    private TextField phone;

    @FXML
    private  VBox vbox;

    public void setPageCounter(int x) {
        pageCounter = x;
        tab.setText("Contact #" + pageCounter);
    }

    @FXML
    private Tab tab;

    @FXML
    private void newContact(ActionEvent event) {
        Tab tab2 = null;
        UserDTO cdto = new UserDTO();
        FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/screens/ContactTab.fxml"));
        try {
            tab2 = tabLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ContactTabController tabController = tabLoader.getController();
        tabController.setPageCounter(pageCounter + 1);
        tabController.setCdto(cdto);
        tabController.setAddContactController(addContactController);
        tabController.setStage(stage);
        tabController.setUserDTO(userDTO);
        tabController.setCdao(cdao);
        addContactController.addNewContact(cdto, tab2,tabController);
        tab.getTabPane().getTabs().add(tab2);
        tab.getTabPane().getSelectionModel().select(tab2);

    }

    @FXML
    private void addContact(ActionEvent event){
         String ret = "Error";
        // for (ArrayList<Object> entry : new ArrayList<>(nwContacts)) {
        //     UserDTO user = (UserDTO) entry.get(0);
        //     Tab tab = (Tab)entry.get(1);
        //     ContactTabController cont = (ContactTabController) entry.get(2);
        //     System.out.println(userDTO.getPhone()+" "+ user.getPhone());
            
        //     ret = cdao.create(userDTO.getPhone(), user.getPhone());
            // if(!ret.equals("Sent Successfully")){
            //     tabPane.getSelectionModel().select(tab);
            //     cont.hamar();
            //     return;
            // }else{
        //         tabPane.getTabs().remove(tab);
        //         removeContact(user);
        //     }
        //     // Do something with user and tab
        // }

        try {
            ret = cdao.create(userDTO.getPhone(), cdto.getPhone());
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if(!ret.equals("Sent Successfully")){
            hamar();
            writeVbox(ret);
            return;
        }
        addContactController.removeContact(cdto);
        tab.getTabPane().getTabs().remove(tab);
        
        // if(tab.getTabPane().getTabs().isEmpty()){
        //     stage.close();
        // }
    }

    
    @FXML
    private void addAllContacts(ActionEvent event){
        // tab.getTabPane().getTabs().remove(tab);
        addContactController.addAll();
        // stage.close();
    }

    public void setAddContactController(AddContactController addContactController) {
        
        this.addContactController = addContactController;

    }
    
    public void setCdto(UserDTO cdto) {
        this.cdto = cdto;
    }

    public void hamar(){
        phone.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID,
                        new CornerRadii(3), new BorderWidths(2), new Insets(-2))));
    }

    public void writeVbox(String msg){
        if(vbox.getChildren().size()>1){
            Label label = (Label)vbox.getChildren().get(1);
            label.setText(msg);
        }else{
            Label label = new Label();
            label.setText(msg);
            label.setTextFill(Color.RED);
            vbox.getChildren().add(label);
        }
    }
    @FXML
    private void initialize() {

        tab.setOnClosed((e)->{
            addContactController.removeContact(cdto);

        });
        phone.setOnKeyTyped((keyEvent) -> {
            phone.setBorder(null);
            cdto.setPhone(phone.getText());
            if(vbox.getChildren().size()>1){
                vbox.getChildren().remove(vbox.getChildren().size() - 1);
            }
            
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }


    /**
     * @return int return the pageCounter
     */
    public int getPageCounter() {
        return pageCounter;
    }

    /**
     * @return AddContactController return the addContactController
     */
    public AddContactController getAddContactController() {
        return addContactController;
    }

    /**
     * @return UserDTO return the cdto
     */
    public UserDTO getCdto() {
        return cdto;
    }

    /**
     * @return UserDTO return the userDTO
     */
    public UserDTO getUserDTO() {
        return userDTO;
    }

    /**
     * @return Stage return the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * @return ContactDAO return the cdao
     */
    public ContactDAOInterface getCdao() {
        return cdao;
    }

    /**
     * @param cdao the cdao to set
     */
    public void setCdao(ContactDAOInterface cdao) {
        this.cdao = cdao;
    }

    /**
     * @param tab the tab to set
     */
    public void setTab(Tab tab) {
        this.tab = tab;
    }

}
