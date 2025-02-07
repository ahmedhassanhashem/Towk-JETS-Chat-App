package gov.iti.jets.chatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.rmi.RemoteException;

import org.junit.jupiter.api.Test;

public class ChatbotServiceTest {

    @Test
    public void testChatbotService(){
        
        try {
            String response = new ChatbotImpl().sendMessage("say hi only");
            assertEquals("Hi\n", response);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
