package gov.iti.jets.dao;

import org.junit.jupiter.api.BeforeEach;

public class MessageDAOTest extends MockingDBUtiltiy{
    
    private MessageDAO messageDAO;

    @BeforeEach
    void initDAO() throws Exception {
        messageDAO = new MessageDAO();
    }

}
