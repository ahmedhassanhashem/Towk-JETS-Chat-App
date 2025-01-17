CREATE TABLE `User`(
    `userID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `phone` VARCHAR(11) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `country` VARCHAR(50) NOT NULL,
    `gender` ENUM('MALE', 'FEMALE') NOT NULL,
    `email` VARCHAR(100) NOT NULL,
    `birthdate` DATE NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `userPicture` BLOB NULL,
    `bio` TEXT NULL,
    `firstLogin` BOOLEAN NOT NULL,
    `userStatus` ENUM('ONLINE', 'OFFLINE') NOT NULL,
    `userMode` ENUM('AVAILABLE', 'BUSY', 'AWAY') NULL
);
ALTER TABLE
    `User` ADD UNIQUE `user_phone_unique`(`phone`);
CREATE TABLE `Chat`(
    `chatID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `chatType` ENUM('SINGLE', 'GROUP') NOT NULL DEFAULT 'SINGLE',
    `chatName` VARCHAR(100) NULL,
    `chatPicture` BLOB NULL
);
CREATE TABLE `Message`(
    `messageID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `messageContent` VARCHAR(255) NOT NULL,
    `chatID` INT NOT NULL,
    `userID` INT NOT NULL,
    `messageDate` DATE NOT NULL,
    `attachmentID` INT NOT NULL
);
CREATE TABLE `Attachment`(
    `attachmentID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `attachmentTitle` VARCHAR(255) NOT NULL,
    `attachmentType` VARCHAR(255) NOT NULL,
    `attachmentSize` INT NOT NULL
);
CREATE TABLE `Announcement`(
    `announcementID` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `announcementTitle` VARCHAR(255) NOT NULL,
    `announcementContent` VARCHAR(255) NOT NULL
);
CREATE TABLE `UserChat`(
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `chatID` INT NOT NULL,
    `userID` INT NOT NULL
);
CREATE TABLE `UserContact`(
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `senderID` INT NOT NULL,
    `recieverID` INT NOT NULL,
    `requestStatus` ENUM('ACCEPT', 'PENDING', 'REJECT') NOT NULL DEFAULT 'PENDING'
);
ALTER TABLE
    `UserContact` ADD CONSTRAINT `usercontact_senderid_foreign` FOREIGN KEY(`senderID`) REFERENCES `User`(`userID`);
ALTER TABLE
    `UserChat` ADD CONSTRAINT `userchat_userid_foreign` FOREIGN KEY(`userID`) REFERENCES `User`(`userID`);
ALTER TABLE
    `Message` ADD CONSTRAINT `message_attachmentid_foreign` FOREIGN KEY(`attachmentID`) REFERENCES `Attachment`(`attachmentID`);
ALTER TABLE
    `Message` ADD CONSTRAINT `message_chatid_foreign` FOREIGN KEY(`chatID`) REFERENCES `Chat`(`chatID`);
ALTER TABLE
    `UserContact` ADD CONSTRAINT `usercontact_recieverid_foreign` FOREIGN KEY(`recieverID`) REFERENCES `User`(`userID`);
ALTER TABLE
    `Message` ADD CONSTRAINT `message_userid_foreign` FOREIGN KEY(`userID`) REFERENCES `User`(`userID`);
ALTER TABLE
    `UserChat` ADD CONSTRAINT `userchat_chatid_foreign` FOREIGN KEY(`chatID`) REFERENCES `Chat`(`chatID`);