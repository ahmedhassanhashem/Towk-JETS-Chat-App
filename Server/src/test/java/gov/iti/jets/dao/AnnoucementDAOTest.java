package gov.iti.jets.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.iti.jets.dto.AnnouncementDTO;

public class AnnoucementDAOTest extends MockingDBUtiltiy{
private AnnouncementDAO annoucementDAO;

    @BeforeEach
    void initDAO() throws Exception {
        annoucementDAO = new AnnouncementDAO();
    }

    @Test
    void testCreate() throws Exception{
        AnnouncementDTO announcement = new AnnouncementDTO();
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
 ;
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1);

        AnnouncementDTO createdAnnouncement = annoucementDAO.create(announcement);

        assertNotNull(createdAnnouncement, "Created announcement should not be null");
        assertEquals(1, createdAnnouncement.getAnnouncementID(), "Announcement ID should be 1");
   
    }

    @Test
    void testRead() throws Exception{
         AnnouncementDTO announcement = new AnnouncementDTO();
         announcement.setAnnouncementID(1);
 
         when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
         when(mockResultSet.next()).thenReturn(true);
         when(mockResultSet.getInt("announcementID")).thenReturn(1);
         when(mockResultSet.getString("announcementTitle")).thenReturn("Test Announcement");
         when(mockResultSet.getString("announcementContent")).thenReturn("Test Content");
 
         AnnouncementDTO fetchedAnnouncement = annoucementDAO.read(announcement);
 
         assertNotNull(fetchedAnnouncement, "Fetched announcement should not be null");
         assertEquals(1, fetchedAnnouncement.getAnnouncementID(), "Announcement ID should be 1");
         assertEquals("Test Announcement", fetchedAnnouncement.getAnnouncementTitle(), "Announcement title should match");
         assertEquals("Test Content", fetchedAnnouncement.getAnnouncementContent(), "Announcement content should match");
     
    }

    @Test
    void testUpdate() throws Exception{
        AnnouncementDTO announcement = new AnnouncementDTO();
        announcement.setAnnouncementID(1);
        announcement.setAnnouncementTitle("Updated Title");
        announcement.setAnnouncementContent("Updated Content");

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        AnnouncementDTO updatedAnnouncement = annoucementDAO.update(announcement);

        assertNotNull(updatedAnnouncement, "Updated announcement should not be null");
        assertEquals(1, updatedAnnouncement.getAnnouncementID(), "Announcement ID should be 1");
        assertEquals("Updated Title", updatedAnnouncement.getAnnouncementTitle(), "Updated title should match");
        assertEquals("Updated Content", updatedAnnouncement.getAnnouncementContent(), "Updated content should match");


    }

    @Test
    void testDelete() throws Exception{
        AnnouncementDTO announcement = new AnnouncementDTO();
        announcement.setAnnouncementID(1);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        annoucementDAO.delete(announcement);

        verify(mockPreparedStatement, times(1)).executeUpdate();
   
    }
    
    @Test
    void testFindAll() throws Exception{
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("announcementID")).thenReturn(1, 2);
        when(mockResultSet.getString("announcementTitle")).thenReturn("Title 1", "Title 2");
        when(mockResultSet.getString("announcementContent")).thenReturn("Content 1", "Content 2");

        List<AnnouncementDTO> fetchedAnnouncements = annoucementDAO.findAll();

        assertNotNull(fetchedAnnouncements, "List of announcements should not be null");
        assertEquals(2, fetchedAnnouncements.size(), "There should be 2 announcements");
        assertEquals("Title 1", fetchedAnnouncements.get(0).getAnnouncementTitle(), "First announcement title should match");
        assertEquals("Title 2", fetchedAnnouncements.get(1).getAnnouncementTitle(), "Second announcement title should match");
    
    }
    
}
