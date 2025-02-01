package gov.iti.jets.dao;

import org.junit.jupiter.api.BeforeEach;

public class ContactDAOTest extends MockingDBUtiltiy{
    private ContactDAO contactDAO;

    @BeforeEach
    void initDAO() throws Exception {
        contactDAO = new ContactDAO();
    }
}
