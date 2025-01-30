package gov.iti.jets.dto;

import java.io.Serializable;
import java.sql.Date;

public class UserDTO implements Serializable {

    private int userID;
    private String phone;
    private String name;
    private String country;
    private Gender gender;
    private String email;
    private Date birthdate;
    private String password;
    private byte[] userPicture;
    private String bio;
    private boolean firstLogin;
    private UserStatus userStatus;
    private UserMode userMode;

    /**
     * @return int return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return String return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return String return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return String return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return Gender return the gender
     */
    public Gender getGender() {
        return gender;
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    /**
     * @return String return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return String return the birthdate
     */
    public Date getBirthdate() {
        return birthdate;
    }

    /**
     * @param birthdate the birthdate to set
     */
    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * @return String return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return byte[] return the userPicture
     */
    public byte[] getUserPicture() {
        return userPicture;
    }

    /**
     * @param userPicture the userPicture to set
     */
    public void setUserPicture(byte[] userPicture) {
        this.userPicture = userPicture;
    }

    /**
     * @return String return the bio
     */
    public String getBio() {
        return bio;
    }

    /**
     * @param bio the bio to set
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * @return boolean return the firstLogin
     */
    public boolean isFirstLogin() {
        return firstLogin;
    }

    /**
     * @param firstLogin the firstLogin to set
     */
    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    /**
     * @return UserStatus return the userStatus
     */
    public UserStatus getUserStatus() {
        return userStatus;
    }

    /**
     * @param userStatus the userStatus to set
     */
    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * @return UserMode return the userMode
     */
    public UserMode getUserMode() {
        return userMode;
    }

    /**
     * @param userMode the userMode to set
     */
    public void setUserMode(UserMode userMode) {
        this.userMode = userMode;
    }

    

}

