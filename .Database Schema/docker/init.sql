CREATE TABLE `User`(
    `userID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `phone` VARCHAR(11) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `country` VARCHAR(50) NOT NULL,
    `gender` ENUM('MALE', 'FEMALE') NOT NULL,
    `email` VARCHAR(100) NULL,
    `birthdate` DATE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `userPicture` VARCHAR(255) NULL,
    `bio` TEXT NULL,
    `firstLogin` BOOLEAN NOT NULL,
    `userStatus` ENUM('ONLINE', 'OFFLINE') NOT NULL,
    `userMode` ENUM('AVAILABLE', 'BUSY', 'AWAY') NULL
);
CREATE TABLE `Admin`(
    `userID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `phone` VARCHAR(11) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `firstLogin` BOOLEAN NOT NULL
);
ALTER TABLE
    `User` ADD UNIQUE `user_phone_unique`(`phone`);
CREATE TABLE `Chat`(
    `chatID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `chatType` ENUM('SINGLE', 'GROUP') NOT NULL DEFAULT 'SINGLE',
    `chatName` VARCHAR(255) NULL,
    `chatPicture` VARCHAR(255) NULL
);
CREATE TABLE `Message`(
    `messageID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `messageContent` TEXT NOT NULL,
    `chatID` INT UNSIGNED  NOT NULL,
    `userID` INT UNSIGNED  NOT NULL,
    `messageDate` DATE NOT NULL,
    `attachmentID` INT UNSIGNED  NULL
);
CREATE TABLE `Attachment`(
    `attachmentID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `attachmentTitle` VARCHAR(255) NOT NULL,
    `attachmentType` VARCHAR(255),
    `attachmentSize` INT NULL
);
CREATE TABLE `Announcement`(
    `announcementID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `announcementTitle` TEXT NOT NULL,
    `announcementContent` TEXT NOT NULL
);
CREATE TABLE `UserChat`(
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `chatID` INT UNSIGNED  NOT NULL,
    `userID` INT UNSIGNED  NOT NULL
);
CREATE TABLE `UserContact`(
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `senderID` INT UNSIGNED NOT NULL,
    `receiverID` INT UNSIGNED NOT NULL,
    `requestStatus` ENUM('ACCEPT', 'PENDING', 'REJECT','BLOCK') NOT NULL DEFAULT 'PENDING',
    `blockedBy` INT UNSIGNED
);
CREATE TABLE `Notification`(
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `userID` INT UNSIGNED NOT NULL,
    `messageID` INT UNSIGNED NOT NULL

);
ALTER TABLE
    `UserContact` ADD CONSTRAINT `usercontact_senderid_foreign` FOREIGN KEY(`senderID`) REFERENCES `User`(`userID`);
ALTER TABLE 
    `UserContact` ADD CONSTRAINT `usercontact_blockedBy_foreign` FOREIGN KEY (`blockedBy`) REFERENCES `User`(`userID`);
ALTER TABLE 
    `Notification` ADD CONSTRAINT `Notification_userID_foreign` FOREIGN KEY (`userID`) REFERENCES `User`(`userID`);
ALTER TABLE 
    `Notification` ADD CONSTRAINT `Notification_messageID_foreign` FOREIGN KEY (`messageID`) REFERENCES `Message`(`messageID`);
ALTER TABLE
    `UserChat` ADD CONSTRAINT `userchat_userid_foreign` FOREIGN KEY(`userID`) REFERENCES `User`(`userID`);
ALTER TABLE
    `Message` ADD CONSTRAINT `message_attachmentid_foreign` FOREIGN KEY(`attachmentID`) REFERENCES `Attachment`(`attachmentID`);
ALTER TABLE
    `Message` ADD CONSTRAINT `message_chatid_foreign` FOREIGN KEY(`chatID`) REFERENCES `Chat`(`chatID`);
ALTER TABLE
    `UserContact` ADD CONSTRAINT `usercontact_receiverID_foreign` FOREIGN KEY(`receiverID`) REFERENCES `User`(`userID`);
ALTER TABLE
    `Message` ADD CONSTRAINT `message_userid_foreign` FOREIGN KEY(`userID`) REFERENCES `User`(`userID`);
ALTER TABLE
    `UserChat` ADD CONSTRAINT `userchat_chatid_foreign` FOREIGN KEY(`chatID`) REFERENCES `Chat`(`chatID`);


-- Insert dummy users
INSERT INTO `User` (`phone`, `name`, `country`, `gender`, `email`, `birthdate`, `password`, `userPicture`, `bio`, `firstLogin`, `userStatus`, `userMode`)
VALUES
('12345678901', 'Alice Johnson', 'USA', 'FEMALE', 'alice@example.com', '1990-05-15', 'password123', NULL, 'Hello, I am Alice!', TRUE, 'OFFLINE', 'AVAILABLE'),
('23456789012', 'Bob Smith', 'Canada', 'MALE', 'bob@example.com', '1985-08-20', 'password123', NULL, 'Hi, I am Bob!', TRUE, 'OFFLINE', 'BUSY'),
('34567890123', 'Charlie Brown', 'UK', 'MALE', 'charlie@example.com', '1995-02-10', 'password123', NULL, 'Hey, I am Charlie!', TRUE, 'OFFLINE', 'AWAY'),
('45678901234', 'Diana Prince', 'USA', 'FEMALE', 'diana@example.com', '1992-11-25', 'password123', NULL, 'Hi, I am Diana!', TRUE, 'OFFLINE', 'AVAILABLE'),
('56789012345', 'Eve Adams', 'Australia', 'FEMALE', 'eve@example.com', '1988-07-30', 'password123', NULL, 'Hello, I am Eve!', TRUE, 'OFFLINE', 'BUSY');

-- Insert dummy admins
INSERT INTO `Admin` (`phone`, `password`,`firstLogin`)
VALUES
('2',  '',TRUE);

-- Insert dummy chats
INSERT INTO `Chat` (`chatType`, `chatName`, `chatPicture`)
VALUES
('SINGLE', NULL, NULL), -- Single chat between Alice and Bob
('SINGLE', NULL, NULL), -- Single chat between Charlie and Diana
('GROUP', 'Group Chat 1', NULL), -- Group chat with Alice, Bob, and Charlie
('GROUP', 'Group Chat 2', NULL); -- Group chat with Diana and Eve

-- Insert dummy UserChat relationships
INSERT INTO `UserChat` (`chatID`, `userID`)
VALUES
(1, 1), -- Alice in Chat 1 (Single)
(1, 2), -- Bob in Chat 1 (Single)
(2, 3), -- Charlie in Chat 2 (Single)
(2, 4), -- Diana in Chat 2 (Single)
(3, 1), -- Alice in Chat 3 (Group)
(3, 2), -- Bob in Chat 3 (Group)
(3, 3), -- Charlie in Chat 3 (Group)
(4, 4), -- Diana in Chat 4 (Group)
(4, 5); -- Eve in Chat 4 (Group)

-- Insert dummy attachments
INSERT INTO `Attachment` (`attachmentTitle`, `attachmentType`, `attachmentSize`)
VALUES
('image1.png', 'image/png', 1024),
('document1.pdf', 'application/pdf', 2048),
('video1.mp4', 'video/mp4', 4096);

-- Insert dummy messages
INSERT INTO `Message` (`messageContent`, `chatID`, `userID`, `messageDate`, `attachmentID`)
VALUES
('Hi Bob!', 1, 1, '2023-10-01', 1), -- Alice sends a message in Chat 1
('Hello Alice!', 1, 2, '2023-10-01', NULL), -- Bob replies in Chat 1
('Hey Diana!', 2, 3, '2023-10-02', NULL), -- Charlie sends a message in Chat 2
('Hi Charlie!', 2, 4, '2023-10-02', 2), -- Diana replies in Chat 2
('Hello everyone!', 3, 1, '2023-10-03', NULL), -- Alice sends a message in Chat 3 (Group)
('Hi Alice!', 3, 2, '2023-10-03', NULL), -- Bob replies in Chat 3 (Group)
('Hey guys!', 3, 3, '2023-10-03', 3), -- Charlie sends a message in Chat 3 (Group)
('Hi Diana!', 4, 5, '2023-10-04', NULL), -- Eve sends a message in Chat 4 (Group)
('Hello Eve!', 4, 4, '2023-10-04', NULL); -- Diana replies in Chat 4 (Group)

-- Insert dummy UserContact relationships
INSERT INTO `UserContact` (`senderID`, `receiverID`, `requestStatus`)
VALUES
(1, 2, 'ACCEPT'), -- Alice sends a contact request to Bob (accepted)
(3, 4, 'PENDING'), -- Charlie sends a contact request to Diana (pending)
(5, 4, 'REJECT'); -- Eve sends a contact request to Diana (rejected)

-- Insert dummy announcements
INSERT INTO `Announcement` (`announcementTitle`, `announcementContent`)
VALUES
('Welcome!', 'Welcome to our chat application!'),
('New Feature', 'Group chats are now available!');