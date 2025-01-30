package gov.iti.jets.controller;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Properties;

// import gov.iti.jets.dao.ContactDAO;

import gov.iti.jets.dao.ContactDAOInterface;
import gov.iti.jets.dao.UserDAOInterface;
import gov.iti.jets.dto.UserDTO;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class AddContactController {

    private ArrayList<ArrayList<Object>> nwContacts= new ArrayList<>(); 
    private AddContactController addContactController;
    private ContactTabController tabController;
    private Stage stage;
    private int pageCounter = 2;
    private UserDTO userDTO;
    private ContactDAOInterface cdao;

    @FXML
    private TabPane tabPane;



    public void setStage(Stage s){
        stage =s;
        tabController.setStage(stage);
    }
    
    public int getPageCounter() {
        return pageCounter;
    }



    public void addNewContact(UserDTO contact,Tab tab,ContactTabController cont) {
        // this.nwContacts.add(contact);
        ArrayList<Object> entry = new ArrayList<>();
        entry.add(contact);
        entry.add(tab);
        entry.add(cont);
        this.nwContacts.add(entry);

    }   

    public void removeContact(UserDTO contact) {
        // this.nwContacts.remove(contact);
        this.nwContacts.removeIf(entry -> entry.get(0).equals(contact));
    }    

    /**
     * @return Stage return the stage
     */
    public Stage getStage() {
        return stage;
    }


    /**
     * @param addContactController the addContactController to set
     */
    public void setAddContactController(AddContactController addContactController) {

        this.addContactController = addContactController;
        tabController.setAddContactController(addContactController);
        
    }

    public void addAll(){
        int cnt = 1;
        String ret ="Error";
        for (ArrayList<Object> entry : new ArrayList<>(nwContacts)) {
            UserDTO user = (UserDTO) entry.get(0);
            Tab tab = (Tab)entry.get(1);
            ContactTabController cont = (ContactTabController) entry.get(2);
            // System.out.println(userDTO.getPhone()+" "+ user.getPhone());
            
            try {
                ret = cdao.create(userDTO.getPhone(), user.getPhone());
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if(!ret.equals("Sent Successfully")){
                tabPane.getSelectionModel().select(tab);
                cont.hamar();
                cont.writeVbox(ret);
                return;
            }else{
                tabPane.getTabs().remove(tab);
                removeContact(user);
            }
            
        }

        
        stage.close();
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
            cdao = (ContactDAOInterface) reg.lookup("contactDAO");

        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        UserDTO cdto = new UserDTO();
        Tab tab = null;
        FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/screens/ContactTab.fxml"));
        try {
            tab = tabLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tabController = tabLoader.getController();
        tabController.setPageCounter(1);
        tabController.setCdto(cdto);
        
        tabController.setCdao(cdao);
        // nwContacts.add(Map.entry(cdto,tab));
        addNewContact(cdto,tab,tabController);
        tabPane.getTabs().add(tab);
        
        tabPane.getSelectionModel().select(tab);
        tabPane.getTabs().addListener(new ListChangeListener<Tab>() {
        @Override
        public void onChanged(javafx.collections.ListChangeListener.Change<? extends Tab> c) {
            if(tabPane.getTabs().isEmpty()){
                stage.close();
            }

        }

    });
        if(tab.getTabPane().getTabs().isEmpty()){
            stage.close();
        }
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
        tabController.setUserDTO(userDTO);
    }


}
