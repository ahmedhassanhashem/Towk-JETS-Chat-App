package gov.iti.jets.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.iti.jets.dto.AttachementDTO;

public class AttachementDAOTest extends MockingDBUtiltiy{
    private AttachementDAO attachementDAO;

    @BeforeEach
    void initDAO() throws Exception {
        attachementDAO = new AttachementDAO();
    }

    @Test
    void testGetAttachmentTitle() throws Exception {

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn("Test Title");

        String title = attachementDAO.getAttachmentTitle(1);

        assertEquals("Test Title", title);
    }

    @Test
    void testGetAttachmentID() throws Exception {
        
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(5);

        int id = attachementDAO.getAttachmentID("Test Title");

        assertEquals(5, id);
    }

    @Test
    void testCreateAttachment() throws Exception {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate 1 row affected
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Simulate that a key is generated
        when(mockResultSet.getInt(1)).thenReturn(123); // Simulate the generated ID
    
        when(mockDbManager.getConnection()).thenReturn(mockConnection); // Return the mock connection
    
        AttachementDTO attachment = new AttachementDTO();
        attachment.setAttachmentTitle("Test Title");
        attachment.setAttachmentType("pdf");
        attachment.setAttachmentSize(1024);
    
        AttachementDAO attachementDAO = new AttachementDAO();
    
        AttachementDTO savedAttachment = attachementDAO.createAttachment(attachment);
    
        assertNotNull(savedAttachment, "Attachment should not be null");
        assertTrue(savedAttachment.getAttachmentID() > 0, "Attachment ID should be greater than zero");
        assertEquals(123, savedAttachment.getAttachmentID(), "Generated ID should match the mock value");
    }
    

    
}
